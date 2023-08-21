import os
from transformers import AutoModelForCausalLM, AutoTokenizer


os.environ['CUDA_VISIBLE_DEVICES'] = '1'

FIM_PREFIX = "<fim-prefix>"
FIM_MIDDLE = "<fim-middle>"
FIM_SUFFIX = "<fim-suffix>"
FIM_PAD = "<fim-pad>"
EOD = "<|endoftext|>"

checkpoint = "C:\\Users\\Leihan\\Desktop\\Code\\StarCoder\\santacoder"
device = "cpu" # for GPU usage or "cpu" for CPU usage
read_token = "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"

tokenizer = AutoTokenizer.from_pretrained(checkpoint, use_auth_token=read_token, padding_side="left")
tokenizer.add_special_tokens({
    "additional_special_tokens": [EOD, FIM_PREFIX, FIM_MIDDLE, FIM_SUFFIX, FIM_PAD],
    "pad_token": EOD,
})
model = AutoModelForCausalLM.from_pretrained(checkpoint, use_auth_token=read_token, device_map='auto', revision='final', trust_remote_code=True)

prefix = "public Integer generateRandomInteger(){\n    "
suffix = "\n}"

inputs = tokenizer.encode(f'<fim-prefix>{prefix}<fim-suffix>{suffix}<fim-middle>', padding=True, return_tensors="pt").to(device)
outputs = model.generate(inputs, max_new_tokens=100, pad_token_id=tokenizer.pad_token_id, do_sample=True, temperature=1.0, num_beams=10, num_return_sequences=3)
for i in range(3):
    output_text = tokenizer.decode(outputs[0])
    start = output_text.find('<fim-middle>') + len('<fim-middle>')
    end = output_text.find('<|endoftext|>', start) or len(output_text)
    fill_part = output_text[start:end]
    print(f"{i+1}:\n{prefix}{fill_part}{suffix}\n")