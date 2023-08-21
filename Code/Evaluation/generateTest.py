import os
import argparse
import logging
import json
import torch
import math
import sys
from tqdm import tqdm
from accelerate import init_empty_weights
from transformers import AutoTokenizer, AutoModelForCausalLM

STAR_FIM_PREFIX = "<fim_prefix>"
STAR_FIM_MIDDLE = "<fim_middle>"
STAR_FIM_SUFFIX = "<fim_suffix>"
STAR_FIM_PAD = "<fim-pad>"
EOD = "<|endoftext|>"

SEP = '<sep>'

TEMPERATURE = 0.7

SAMPLE_COUNT = 1

SEP_COUNT = 8

model_path = '/home/zhiquanyang/StarCoder/finetune/output_model_evosuite_latest'
tokenizer_path = '/home/zhiquanyang/StarCoder/finetune/starcoder'

prompt_path = '/home/zhiquanyang/StarCoder/eval/output_evosuite_oracle_test/'

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

number = int(sys.argv[1])
# device = f"cuda:{number}"
device = "cuda:0"
kv_pair_sep = kv_pair[math.floor(len(kv_pair)*(number/SEP_COUNT)):math.floor(len(kv_pair)*((number+1)/SEP_COUNT))]


print("loading model...")
tokenizer = AutoTokenizer.from_pretrained(tokenizer_path)
tokenizer.add_special_tokens({
    "additional_special_tokens": [EOD, STAR_FIM_PREFIX, STAR_FIM_MIDDLE, STAR_FIM_SUFFIX, STAR_FIM_PAD],
    "pad_token": EOD,
})
model = AutoModelForCausalLM.from_pretrained(model_path, device_map=device, load_in_8bit=True, torch_dtype=torch.float16)


print("generating tests...")
tests = {}
for (key, value) in tqdm(kv_pair_sep):
    test_methods = []
    for prompt in tqdm(value):
        try:
            prefix = prompt['prefix']
            suffix = prompt['suffix']
            prompt_ = f'{STAR_FIM_PREFIX}{prefix}{STAR_FIM_SUFFIX}{suffix}{STAR_FIM_MIDDLE}'
            if len(tokenizer.tokenize(prompt_)) > 8100: 
                logging.error(f"Token limit for {key} prompt_len:{len(prompt_)}")
                continue

            # print(f'len={len(tokenizer.tokenize(prompt_))}')
            inputs = tokenizer(prompt_, padding=True, return_tensors="pt", return_token_type_ids=False).to(device)
            outputs = model.generate(**inputs, max_new_tokens=100, pad_token_id=tokenizer.pad_token_id, temperature=TEMPERATURE, num_beams=10, num_return_sequences=SAMPLE_COUNT)

            res = []
            for i in range(len(outputs)):
                output_text = tokenizer.decode(outputs[0])
                start = output_text.find(STAR_FIM_MIDDLE) + len(STAR_FIM_MIDDLE)
                end = output_text.find(EOD, start) or len(output_text)
                fill_part = output_text[start:end]
                prefix_lines = prefix.split('\n')

                for j in reversed(range(len(prefix_lines))):
                    if 'public' in prefix_lines[j] and 'test' in prefix_lines[j]:
                        test_prefix = '\n'.join(prefix_lines[j:])
                        break
                res.append(test_prefix + fill_part + suffix)
            test_methods.append(res)
        except Exception:
            continue
    tests[key] = test_methods


print("writing to files...")
output_path = os.path.join(os.sep.join(prompt_path.split(os.sep)[:-1]), "response")

for key, value in zip(tests.keys(), tests.values()):
    if len(value) < 1:
        continue
    write_data = json.dumps(value, indent=2)
    project = key.split(SEP)[0]
    bug_number = key.split(SEP)[1]
    if not os.path.exists(os.path.join(output_path, project, bug_number)):
        os.makedirs(os.path.join(output_path, project, bug_number))
    with open(os.path.join(output_path, project, bug_number, "response.json"), 'w', newline='\n') as f:
        f.write(write_data)



