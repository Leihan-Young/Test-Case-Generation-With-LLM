import os
import argparse
import logging
import json
import torch
import time
import math
from multiprocessing import cpu_count, Process, Manager, set_start_method, freeze_support
from joblib import Parallel, delayed
from accelerate import init_empty_weights
from transformers import AutoTokenizer, AutoModelForCausalLM
from tqdm import tqdm

STAR_FIM_PREFIX = "<fim_prefix>"
STAR_FIM_MIDDLE = "<fim_middle>"
STAR_FIM_SUFFIX = "<fim_suffix>"
STAR_FIM_PAD = "<fim-pad>"
EOD = "<|endoftext|>"

GPU_COUNT = 8

SEP = '<sep>'

TEMPERATURE = 0.7

SAMPLE_COUNT = 1

model_path = '/home/zhiquanyang/StarCoder/finetune/output_model_evosuite_latest'
tokenizer_path = '/home/zhiquanyang/StarCoder/finetune/starcoder'

prompt_path = '/home/zhiquanyang/StarCoder/eval/output_evosuite_oracle_test/'

def generate(device, count, kv_pair):
    tokenizer = AutoTokenizer.from_pretrained(tokenizer_path, padding_side="left")
    tokenizer.add_special_tokens({
        "additional_special_tokens": [EOD, STAR_FIM_PREFIX, STAR_FIM_MIDDLE, STAR_FIM_SUFFIX, STAR_FIM_PAD],
        "pad_token": EOD,
    })
    model = AutoModelForCausalLM.from_pretrained(model_path, device_map=device, load_in_8bit=True)
    tests = {}
    for (key, value) in kv_pair:
        test_methods = []
        for prompt in value:
            prefix = prompt['prefix']
            suffix = prompt['suffix']
            prompt_ = f'{STAR_FIM_PREFIX}{prefix}{STAR_FIM_SUFFIX}{suffix}{STAR_FIM_MIDDLE}'
            if len(tokenizer.tokenize(prompt_)) > 8100:
                logging.error(f"Token limit for prompt:{prompt_}")
                continue

            inputs = tokenizer.encode(prompt_, padding=True, return_tensors="pt").to(device)
            outputs = model.generate(inputs, max_new_tokens=20, pad_token_id=tokenizer.pad_token_id, temperature=TEMPERATURE, num_beams=10, num_return_sequences=SAMPLE_COUNT)

            res = []
            for i in range(len(outputs)):
                output_text = tokenizer.decode(outputs[0])
                start = output_text.find(STAR_FIM_MIDDLE) + len(STAR_FIM_MIDDLE)
                end = output_text.find(EOD, start) or len(output_text)
                fill_part = output_text[start:end]
                res.append(prefix + fill_part + suffix)
            test_methods.append(res)
        tests[key] = test_methods
    count.set(count.get() + 1)
    return tests

def counting(length, share_value):
    while True:
        print(f'Total:{length}, Finished:{share_value.get()}, Current:{share_value.get()/length * 100}%')
        time.sleep(10)

def main():
    devices = []
    for i in range(GPU_COUNT):
        devices.append(f'cuda:{i}')

    print("getting prompts...")
    prompts = {}
    for root, dirs, files in os.walk(prompt_path):
        for file in files:
            if not file.startswith('prompt'):
                continue
            bug_num = root.split(os.sep)[-1]
            project = root.split(os.sep)[-2]
            with open(os.path.join(root, file)) as f:
                prompts[f'{project}{SEP}{bug_num}'] = json.load(f)
    
    print("seperate prompts into 8 parts...")
    kv_pair = []
    for key, value in zip(prompts.keys(), prompts.values()):
        kv_pair.append((key, value))

    kv_pair_sep = []
    for i in range(len(devices)):
        kv_pair_sep.append(kv_pair[math.floor(len(kv_pair)*(i/len(devices))):math.floor(len(kv_pair)*((i+1)/len(devices)))])
    
    
    print("start counting...")
    counting_manager = Manager()
    share_value = counting_manager.Value('i', 0)
    counting_process = Process(target=counting, name='counting', args=(len(prompts), share_value,))
    counting_process.start()


    print("generating tests...")
    results = []
    results = Parallel(n_jobs=8, backend='multiprocessing')(delayed(generate)(devices[i], share_value, kv_pair_sep[i]) for i in range(8))

    counting_process.terminate()

    print("writing to files...")
    output_path = os.path.join(os.sep.join(prompt_path.split(os.sep)[:-1]), "response")
    for result in results:
        for key, value in zip(result.keys(), result.values()):
            write_data = json.dumps(value, indent=2)
            project = key.split(SEP)[0]
            bug_number = key.split(SEP)[1]
            if not os.path.exists(os.path.join(output_path, project, bug_number)):
                os.makedirs(os.path.join(output_path, project, bug_number))
            with open(os.path.join(output_path, project, bug_number, "response.json"), 'w', newline='\n') as f:
                f.write(write_data)

if __name__ == "__main__":
    freeze_support()
    torch.multiprocessing.set_start_method('spawn')
    main()

