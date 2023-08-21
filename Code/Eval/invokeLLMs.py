import openai
import os
import utils
import env
import logging
from func_timeout import func_set_timeout
from transformers import AutoModelForCausalLM, AutoTokenizer

os.environ['CUDA_VISIBLE_DEVICES'] = '1'

openai.api_key = "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"

FIM_PREFIX = "<fim-prefix>"
FIM_MIDDLE = "<fim-middle>"
FIM_SUFFIX = "<fim-suffix>"
FIM_PAD = "<fim-pad>"
EOD = "<|endoftext|>"

checkpoint = './santacoder'
device = 'cuda'
read_token = "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"

tokenizer = AutoTokenizer.from_pretrained(checkpoint, use_auth_token=read_token, padding_side="left")
tokenizer.add_special_tokens({
    "additional_special_tokens": [EOD, FIM_PREFIX, FIM_MIDDLE, FIM_SUFFIX, FIM_PAD],
    "pad_token": EOD,
})
model = AutoModelForCausalLM.from_pretrained(checkpoint, use_auth_token=read_token, device_map='auto', revision="final", trust_remote_code=True)


@func_set_timeout(30)
def timerInvoke(model, messages, temperature, n):
    if model == 'gpt-3.5-turbo':
        return openai.ChatCompletion.create(model=model, messages=messages, temperature=temperature, n=n)

# invoke LLM to get response for the prompt
def invokeLLM(prompt, llm, logger=logging):
    response = None
    try:
        response = timerInvoke(llm, prompt, 0.7, env.SAMPLE_COUNT)
    except Exception as e:
        logger.debug(f'Invoke failed:{e}')
        response = None
    return response