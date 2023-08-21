import openai
import os
import env
import logging
import gc
import torch
from func_timeout import func_set_timeout
from transformers import AutoModelForCausalLM, AutoTokenizer

os.environ['CUDA_VISIBLE_DEVICES'] = '4,5,6,7'
os.environ['CUDA_LAUNCH_BLOCKING'] = '1'

openai.api_key = "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"

SANTA_FIM_PREFIX = "<fim-prefix>"
SANTA_FIM_MIDDLE = "<fim-middle>"
SANTA_FIM_SUFFIX = "<fim-suffix>"
SANTA_FIM_PAD = "<fim-pad>"
STAR_FIM_PREFIX = "<fim_prefix>"
STAR_FIM_MIDDLE = "<fim_middle>"
STAR_FIM_SUFFIX = "<fim_suffix>"
STAR_FIM_PAD = "<fim-pad>"
EOD = "<|endoftext|>"

santa_coder_model_path = './santacoder'
santa_coder_tokenizer_path = './santacoder'
star_coder_model_path = '/home/zhiquanyang/StarCoder/finetune/third_finetune_model'
star_coder_tokenizer_path = '/home/zhiquanyang/StarCoder/finetune/starcoder'
star_coder_evosuite_model_path = '/home/zhiquanyang/StarCoder/finetune/output_evosuite'
star_coder_evosuite_tokenizer_path = '/home/zhiquanyang/StarCoder/finetune/starcoder'
device = 'cuda'
read_token = "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"

def invokeSantaCoder(prompt, temperature, n):
    tokenizer = AutoTokenizer.from_pretrained(santa_coder_tokenizer_path, use_auth_token=read_token, padding_side="left")
    tokenizer.add_special_tokens({
        "additional_special_tokens": [EOD, SANTA_FIM_PREFIX, SANTA_FIM_MIDDLE, SANTA_FIM_SUFFIX, SANTA_FIM_PAD],
        "pad_token": EOD,
    })
    model = AutoModelForCausalLM.from_pretrained(santa_coder_model_path, use_auth_token=read_token, device_map='cuda:0', revision="final", trust_remote_code=True)
    prefix = prompt['prefix']
    suffix = prompt['suffix']
    prompt = f'{SANTA_FIM_PREFIX}{prefix}{SANTA_FIM_SUFFIX}{suffix}{SANTA_FIM_MIDDLE}'
    if len(tokenizer.tokenize(prompt)) > 2000:
        raise Exception('Token limit.')

    inputs = tokenizer.encode(prompt, padding=True, return_tensors="pt").to(device)
    outputs = model.generate(inputs, max_new_tokens=100, pad_token_id=tokenizer.pad_token_id, do_sample=True, temperature=temperature, num_beams=10, num_return_sequences=n)

    res = []
    for i in range(len(outputs)):
        output_text = tokenizer.decode(outputs[0])
        start = output_text.find(SANTA_FIM_MIDDLE) + len(SANTA_FIM_MIDDLE)
        end = output_text.find(EOD, start) or len(output_text)
        fill_part = output_text[start:end]
        res.append(prefix[prefix.find('public void test'):] + fill_part + suffix)
    return res

def invokeStarCoder(prompt, temperature, n):
    tokenizer = AutoTokenizer.from_pretrained(star_coder_tokenizer_path, use_auth_token=read_token, padding_side="left")
    tokenizer.add_special_tokens({
        "additional_special_tokens": [EOD, STAR_FIM_PREFIX, STAR_FIM_MIDDLE, STAR_FIM_SUFFIX, STAR_FIM_PAD],
        "pad_token": EOD,
    })
    model = AutoModelForCausalLM.from_pretrained(star_coder_model_path, use_auth_token=read_token, device_map='auto')
    prefix = prompt['prefix']
    suffix = prompt['suffix']
    prompt = f'{STAR_FIM_PREFIX}{prefix}{STAR_FIM_SUFFIX}{suffix}{STAR_FIM_MIDDLE}'
    if len(tokenizer.tokenize(prompt)) > 8100:
        raise Exception('Token limit.')

    inputs = tokenizer.encode(prompt, padding=True, return_tensors="pt").to(device)
    outputs = model.generate(inputs, max_new_tokens=100, pad_token_id=tokenizer.pad_token_id, temperature=temperature, num_beams=10, num_return_sequences=n)

    res = []
    for i in range(len(outputs)):
        output_text = tokenizer.decode(outputs[0])
        start = output_text.find(STAR_FIM_MIDDLE) + len(STAR_FIM_MIDDLE)
        end = output_text.find(EOD, start) or len(output_text)
        fill_part = output_text[start:end]
        res.append(fill_part)
    return res
    

def invokeStarCoderMultiInput(prompt, temperature, n):
    tokenizer = AutoTokenizer.from_pretrained(star_coder_evosuite_tokenizer_path, use_auth_token=read_token, padding_side="left")
    tokenizer.add_special_tokens({
        "additional_special_tokens": [EOD, STAR_FIM_PREFIX, STAR_FIM_MIDDLE, STAR_FIM_SUFFIX, STAR_FIM_PAD],
        "pad_token": EOD,
    })
    model = AutoModelForCausalLM.from_pretrained(star_coder_evosuite_model_path, use_auth_token=read_token, device_map='auto')
    results = []
    for p in prompt:
        prefix = p['prefix']
        suffix = p['suffix']
        prompt_ = f'{STAR_FIM_PREFIX}{prefix}{STAR_FIM_SUFFIX}{suffix}{STAR_FIM_MIDDLE}'
        if len(tokenizer.tokenize(prompt_)) > 8100:
            logging.error(f"Token limit for prompt:{prompt}")
            continue

        inputs = tokenizer.encode(prompt_, padding=True, return_tensors="pt").to(device)
        outputs = model.generate(inputs, max_new_tokens=100, pad_token_id=tokenizer.pad_token_id, temperature=temperature, num_beams=10, num_return_sequences=n)

        res = []
        for i in range(len(outputs)):
            output_text = tokenizer.decode(outputs[0])
            start = output_text.find(STAR_FIM_MIDDLE) + len(STAR_FIM_MIDDLE)
            end = output_text.find(EOD, start) or len(output_text)
            fill_part = output_text[start:end]
            res.append(fill_part)
        results.append(res)
    return results

@func_set_timeout(600)
def timerInvoke(model, messages, temperature, n):
    # clear gpu
    gc.collect()
    torch.cuda.empty_cache()
    if model == 'gpt-3.5-turbo':
        return openai.ChatCompletion.create(model=model, messages=messages, temperature=temperature, n=n)
    elif model == 'santacoder':
        return invokeSantaCoder(messages, temperature, n)
    elif model == 'starcoder':
        return invokeStarCoder(messages, temperature, n)
    elif model == 'starcoder-evosuite':
        return invokeStarCoderMultiInput(messages, temperature, n)

# invoke LLM to get response for the prompt
def invokeLLM(prompt, llm, logger=logging):
    response = None
    try:
        response = timerInvoke(llm, prompt, 0.7, env.SAMPLE_COUNT)
    except Exception as e:
        logger.debug(f'Invoke failed:{e}')
        response = None
    return response