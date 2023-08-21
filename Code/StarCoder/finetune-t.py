import argparse
import os

import torch
from accelerate import Accelerator
from datasets import load_dataset
from peft import LoraConfig, get_peft_model, prepare_model_for_int8_training, set_peft_model_state_dict
from torch.utils.data import IterableDataset
from tqdm import tqdm
from transformers import AutoConfig, AutoModelForCausalLM, AutoTokenizer, Trainer, TrainingArguments, logging, set_seed
from transformers import TrainerCallback, TrainingArguments, TrainerState, TrainerControl
from transformers.trainer_utils import PREFIX_CHECKPOINT_DIR

"""
Fine-Tune StarCoder on Code Alpaca/SE
"""

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
lr = 1e-6
num_epoches = 5
batch_size = 1

tokenizer = AutoTokenizer.from_pretrained(tokenizer_name, use_auth_token=True)

data_files = {"train": "./dataset_train_evosuite.json"}
dataset = load_dataset("json", data_files=data_files)

tokenizer = AutoTokenizer.from_pretrained(tokenizer_name)
tokenizer.add_special_tokens({
        "additional_special_tokens": [EOD, FIM_PREFIX, FIM_MIDDLE, FIM_SUFFIX, FIM_PAD],
        "pad_token": EOD,
    })
tokenizer.model_max_length = max_length
block_size = max_length

class SavePeftModelCallback(TrainerCallback):
    def on_save(
        self,
        args: TrainingArguments,
        state: TrainerState,
        control: TrainerControl,
        **kwargs,
    ):
        checkpoint_folder = os.path.join(args.output_dir, f"{PREFIX_CHECKPOINT_DIR}-{state.global_step}")

        kwargs["model"].save_pretrained(checkpoint_folder)

        pytorch_model_path = os.path.join(checkpoint_folder, "pytorch_model.bin")
        torch.save({}, pytorch_model_path)
        return control


class LoadBestPeftModelCallback(TrainerCallback):
    def on_train_end(
        self,
        args: TrainingArguments,
        state: TrainerState,
        control: TrainerControl,
        **kwargs,
    ):
        print(f"Loading best peft model from {state.best_model_checkpoint} (score: {state.best_metric}).")
        best_model_path = os.path.join(state.best_model_checkpoint, "adapter_model.bin")
        adapters_weights = torch.load(best_model_path)
        model = kwargs["model"]
        set_peft_model_state_dict(model, adapters_weights)
        return control
    

def get_args():
    parser = argparse.ArgumentParser()
    parser.add_argument("--model_path", type=str, default="bigcode/large-model")
    parser.add_argument("--dataset_name", type=str, default="HuggingFaceH4/CodeAlpaca_20K")
    parser.add_argument("--subset", type=str)
    parser.add_argument("--split", type=str)
    parser.add_argument("--size_valid_set", type=int, default=10000)
    parser.add_argument("--streaming", action="store_true")
    parser.add_argument("--shuffle_buffer", type=int, default=5000)

    parser.add_argument("--input_column_name", type=str, default="prompt")
    parser.add_argument("--output_column_name", type=str, default="completion")

    parser.add_argument("--seq_length", type=int, default=2048)
    parser.add_argument("--max_steps", type=int, default=10000)
    parser.add_argument("--batch_size", type=int, default=1)
    parser.add_argument("--gradient_accumulation_steps", type=int, default=16)
    parser.add_argument("--eos_token_id", type=int, default=49152)

    parser.add_argument("--lora_r", type=int, default=16)
    parser.add_argument("--lora_alpha", type=int, default=32)
    parser.add_argument("--lora_dropout", type=float, default=0.05)

    parser.add_argument("--learning_rate", type=float, default=5e-6)
    parser.add_argument("--lr_scheduler_type", type=str, default="cosine")
    parser.add_argument("--num_warmup_steps", type=int, default=100)
    parser.add_argument("--weight_decay", type=float, default=0.05)

    parser.add_argument("--local_rank", type=int, default=0)
    parser.add_argument("--no_fp16", action="store_false")
    parser.add_argument("--bf16", action="store_true", default=True)
    parser.add_argument("--no_gradient_checkpointing", action="store_false", default=False)
    parser.add_argument("--seed", type=int, default=0)
    parser.add_argument("--num_workers", type=int, default=None)
    parser.add_argument("--output_dir", type=str, default="./checkpoints")
    parser.add_argument("--log_freq", default=100, type=int)
    parser.add_argument("--eval_freq", default=100, type=int)
    parser.add_argument("--save_freq", default=1000, type=int)

    return parser.parse_args()

def print_trainable_parameters(model):
    """
    Prints the number of trainable parameters in the model.
    """
    trainable_params = 0
    all_param = 0
    for _, param in model.named_parameters():
        all_param += param.numel()
        if param.requires_grad:
            trainable_params += param.numel()
    print(
        f"trainable params: {trainable_params} || all params: {all_param} || trainable%: {100 * trainable_params / all_param}"
    )

def run_training(args, train_data, val_data):
    print("Loading the model")
    # disable caching mechanism when using gradient checkpointing
    model = AutoModelForCausalLM.from_pretrained(
        args.model_path,
        use_auth_token=True,
        use_cache=not args.no_gradient_checkpointing,
        load_in_8bit=True,
        device_map={"": Accelerator().process_index},
    )
    model = prepare_model_for_int8_training(model)

    lora_config = LoraConfig(
        r=args.lora_r,
        lora_alpha=args.lora_alpha,
        lora_dropout=args.lora_dropout,
        bias="none",
        task_type="CAUSAL_LM",
        target_modules = ["c_proj", "c_attn", "q_attn"]
    )

    model = get_peft_model(model, lora_config)

    print_trainable_parameters(model)

    train_data.start_iteration = 0

    print("Starting main loop")

    training_args = TrainingArguments(
        output_dir=args.output_dir,
        dataloader_drop_last=True,
        evaluation_strategy="steps",
        save_strategy="steps",
        load_best_model_at_end=True,
        max_steps=args.max_steps,
        eval_steps=args.eval_freq,
        save_steps=args.save_freq,
        logging_steps=args.log_freq,
        per_device_train_batch_size=args.batch_size,
        per_device_eval_batch_size=args.batch_size,
        learning_rate=args.learning_rate,
        lr_scheduler_type=args.lr_scheduler_type,
        warmup_steps=args.num_warmup_steps,
        gradient_accumulation_steps=args.gradient_accumulation_steps,
        gradient_checkpointing=not args.no_gradient_checkpointing,
        fp16=not args.no_fp16,
        bf16=args.bf16,
        weight_decay=args.weight_decay,
        run_name="StarCoder-finetuned",
        ddp_find_unused_parameters=False,
    )

    trainer = Trainer(model=model, args=training_args, train_dataset=train_data, eval_dataset=val_data, callbacks=[SavePeftModelCallback, LoadBestPeftModelCallback])

    print("Training...")
    trainer.train()

    print("Saving last checkpoint of the model")
    model.save_pretrained(os.path.join(args.output_dir, "final_checkpoint/"))


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

def main(args):
    
    tokenized_dataset = dataset.map(tokenize_function, batched=True, num_proc=1, remove_columns=["instruction", "input", "output"])
    lm_dataset = tokenized_dataset.map(group_texts, batched=True, batch_size=100)

    run_training(args, lm_dataset['train'], None)


if __name__ == "__main__":
    args = get_args()

    set_seed(args.seed)
    os.makedirs(args.output_dir, exist_ok=True)

    logging.set_verbosity_error()

    main(args)
