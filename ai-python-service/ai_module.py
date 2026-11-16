import os
import requests
import asyncio


LLM_API_KEY = os.environ.get("ARK_API_KEY", "")
LLM_URL = os.environ.get("ARK_API_URL", "https://ark.cn-beijing.volces.com/api/v3/chat/completions")
MODEL_NAME = os.environ.get("ARK_MODEL", "ep-20261115003554-tpjlp")
TEMPERATURE = 0.7


async def call_llm(prompt: str):
    """
    调用火山方舟大模型
    :param prompt: 传入完整提示词
    :return: 大模型返回文本
    """
    if not LLM_API_KEY:
        raise RuntimeError("未设置环境变量 ARK_API_KEY")
    headers = {
        "Authorization": f"Bearer {LLM_API_KEY}",
        "Content-Type": "application/json"
    }

    body = {
        "model": MODEL_NAME,
        "messages": [
            {"role": "user", "content": prompt}
        ],
        "temperature": TEMPERATURE
    }

    try:
        print(f"【发送给大模型的prompt】\n{prompt}\n")
        resp = requests.post(
            LLM_URL,
            headers=headers,
            json=body,
            timeout=60
        )
        print(f"【接口返回状态码】{resp.status_code}")
        print(f"【原始返回】{resp.text}")
        data = resp.json()

        if "error" in data:
            raise Exception(f"大模型报错：{data['error']}")

        answer = data["choices"][0]["message"]["content"]
        print(f"【大模型返回结果】\n{answer}\n")
        return answer

    except Exception as e:
        print(f"【调用异常】{str(e)}")
        raise e


if __name__ == "__main__":
    res = asyncio.run(call_llm("你好"))
    print(res)