from transformers import AutoModelForCausalLM, AutoTokenizer
from transformers import Trainer, TrainingArguments, DataCollatorWithPadding
from transformers import default_data_collator, get_linear_schedule_with_warmup
from peft import PeftConfig, PeftModel, get_peft_model
import torch
from datasets import load_dataset
import os
from torch.utils.data import DataLoader
from tqdm import tqdm
from multiprocessing import freeze_support
import math

FIM_PREFIX = "<fim_prefix>"
FIM_MIDDLE = "<fim_middle>"
FIM_SUFFIX = "<fim_suffix>"
FIM_PAD = "<fim-pad>"
EOD = "<|endoftext|>"

model_name = "/home/zhiquanyang/StarCoder/finetune/starcoder"
tokenizer_name = "/home/zhiquanyang/StarCoder/finetune/starcoder"
device = 'cuda'
read_token = "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"
max_length = 2048
lr = 2e-5
num_epoches = 5
batch_size = 1

data_files = {"train": "./dataset_train_evosuite.json"}
dataset = load_dataset("json", data_files=data_files)

tokenizer = AutoTokenizer.from_pretrained(tokenizer_name)
tokenizer.add_special_tokens({
        "additional_special_tokens": [EOD, FIM_PREFIX, FIM_MIDDLE, FIM_SUFFIX, FIM_PAD],
        "pad_token": EOD,
    })
tokenizer.model_max_length = max_length
block_size = max_length

def tokenize_function(examples):
    return tokenizer(examples["output"], truncation=True)

def group_texts(examples):
    # concatenate
    concatenated_examples = {k: sum(examples[k], []) for k in examples.keys()}
    total_length = len(concatenated_examples[list(examples.keys())[0]])
    total_length = (total_length // block_size) * block_size

    # split
    result = {
        k: [t[i : i + block_size] for i in range(0, total_length, block_size)]
        for k, t in concatenated_examples.items()
    }
    result["labels"] = result["input_ids"].copy()
    return result

if __name__ == '__main__':
    freeze_support()
    tokenized_dataset = dataset.map(tokenize_function, batched=True, num_proc=1, remove_columns=["instruction", "input", "output"])
    lm_dataset = tokenized_dataset.map(group_texts, batched=True, batch_size=100)

    print(f'blocksize:{block_size}')
    print(f'dataset-train:{dataset["train"].num_rows}')
    print(f'tokenized_dataset-train:{tokenized_dataset["train"].num_rows}')
    print(f'train:{lm_dataset["train"].num_rows}')

    data_collator = DataCollatorWithPadding(tokenizer=tokenizer)

    model = AutoModelForCausalLM.from_pretrained(model_name, use_auth_token=read_token)
    training_args = TrainingArguments(
        output_dir="./output_models",
        evaluation_strategy="no",
        learning_rate=1e-6,
        weight_decay=0.01,
        num_train_epochs=5,
        per_device_train_batch_size=1,
        warmup_steps=100,
        logging_steps=50,
    )
    trainer = Trainer(
        model=model,
        args=training_args,
        train_dataset=lm_dataset['train'],
        eval_dataset=None,
        data_collator=data_collator,
        tokenizer=tokenizer,
    )
    trainer.train()
    model.save_pretrained("latest_model")