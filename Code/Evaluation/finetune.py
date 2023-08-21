import os
import torch
import random
import datasets
import numpy as np
from accelerate import Accelerator

from tqdm import tqdm
from typing import Dict
from torch.utils.data import DataLoader
from transformers import (
    AutoModelForCausalLM,
    AutoTokenizer,
    DataCollatorForSeq2Seq,
    TrainingArguments,
    Trainer
)
from peft import (
    LoraConfig,
    TaskType,
    get_peft_model,
    get_peft_model_state_dict,
    set_peft_model_state_dict
)

def set_random_seed(seed):
    if seed is not None and seed > 0:
        random.seed(seed)
        np.random.seed(seed)
        torch.manual_seed(seed)
        torch.random.manual_seed(seed)
        torch.cuda.manual_seed(seed)
        torch.cuda.manual_seed_all(seed)
        torch.backends.cudnn.deterministic = True

set_random_seed(1234)

FIM_PREFIX = "<fim-prefix>"
FIM_MIDDLE = "<fim-middle>"
FIM_SUFFIX = "<fim-suffix>"
FIM_PAD = "<fim-pad>"
EOD = "<|endoftext|>"

# LoRA参数
LORA_R = 8
LORA_ALPHA = 32
LORA_DROPOUT = 0.1
# 训练参数
EPOCHS=3
LEARNING_RATE=5e-5
OUTPUT_DIR="./checkpoints"
BATCH_SIZE=4 # 2
GRADIENT_ACCUMULATION_STEPS=3
# 其他参数
MODEL_PATH = "/data/zhiquanyang/StarCoder/starcoder"
DATA_PATH = {"train": "/data/zhiquanyang/StarCoder/dataset_train.json", "test": "/data/zhiquanyang/StarCoder/dataset_test.json"}
MAX_LENGTH = 2048
PATTERN = "{}\n{}"
DS_CONFIG = "ds_config.json"

tokenizer = AutoTokenizer.from_pretrained(MODEL_PATH) # 加载tokenizer
tokenizer.add_special_tokens({
        "additional_special_tokens": [EOD, FIM_PREFIX, FIM_MIDDLE, FIM_SUFFIX, FIM_PAD],
        "pad_token": EOD,
    })

dataset = datasets.load_dataset("json", data_files=DATA_PATH)

def tokenize(text: str, add_eos_token=True):
    result = tokenizer(
        text,
        truncation=True,
        max_length=MAX_LENGTH,
        padding=False,
        return_tensors=None,
    )
    if (result["input_ids"][-1] != tokenizer.eos_token_id
        and len(result["input_ids"]) < MAX_LENGTH
        and add_eos_token):
        result["input_ids"].append(tokenizer.eos_token_id)
        result["attention_mask"].append(1)
    result["labels"] = result["input_ids"].copy()
    return result

def preprocess(example: Dict, train_on_inputs: bool = False):
    prompt = example["instruction"]
    response = example["output"]
    text = PATTERN.format(prompt, response)
    tokenized_inp = tokenize(text)
    if not train_on_inputs:
        tokenized_prompt = tokenize(prompt, add_eos_token=False)
        prompt_tokens_len = len(tokenized_prompt["input_ids"])
        tokenized_inp["labels"] = [-100]*prompt_tokens_len + tokenized_inp["labels"][prompt_tokens_len:]
    return tokenized_inp

train_data = dataset["train"].shuffle().map(preprocess, remove_columns=["id", "input"])

collate_fn = DataCollatorForSeq2Seq(tokenizer, pad_to_multiple_of=8, return_tensors="pt", padding=True)

device_map = "auto"
model = AutoModelForCausalLM.from_pretrained(MODEL_PATH, device_map=device_map)

lora_config = LoraConfig(
    task_type=TaskType.CAUSAL_LM,
    inference_mode=False,
    r=LORA_R,
    lora_alpha=LORA_ALPHA,
    lora_dropout=LORA_DROPOUT,
)
model = get_peft_model(model, lora_config)
model.config.use_cache = False
old_state_dict = model.state_dict
model.state_dict = (
    lambda self, *_, **__: get_peft_model_state_dict(self, old_state_dict())
).__get__(model, type(model))
model.print_trainable_parameters()

training_args = TrainingArguments(
    output_dir=OUTPUT_DIR,
    per_device_eval_batch_size=BATCH_SIZE,
    gradient_accumulation_steps=GRADIENT_ACCUMULATION_STEPS,
    warmup_steps=100,
    num_train_epochs=EPOCHS,
    learning_rate=LEARNING_RATE,
    fp16=True,
    logging_steps=50,
    evaluation_strategy="no",
    save_strategy="steps",
    save_steps=1000,
    save_total_limit=10,
    deepspeed=DS_CONFIG,
    per_device_train_batch_size=1,
)

trainer = Trainer(
    model=model,
    train_dataset=train_data,
    eval_dataset=None,
    args=training_args,
    data_collator=collate_fn,
)
trainer.train()
model.save_pretrained("best_model")