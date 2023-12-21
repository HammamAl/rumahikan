import requests

resp = requests.post("https://getprediction-tqc5taiqdq-lm.a.run.app", files={'file': open('lele.png', 'rb')})

print(resp.json())
