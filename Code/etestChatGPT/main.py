import openai
import os

openai.api_key = "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"


with open('.\\etestChatGPT\\prompt.txt', 'r') as p:
    content = ''.join(p.readlines())
req = [{'role': 'user', 'content': content}]
response = openai.ChatCompletion.create(model='gpt-3.5-turbo', messages=req, temperature=0.7, n=5)
if response != None:
    for i in range(len(response.choices)):
        with open(os.path.join('.\\etestChatGPT', f'{i+1}_sample_test_response.txt'), 'w') as f:
            f.write(response.choices[i].message.content)