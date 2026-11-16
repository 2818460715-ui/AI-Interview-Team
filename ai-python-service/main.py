from fastapi import FastAPI
from ai_module import call_llm
from prompts import GENERATE_QUESTION_PROMPT, SCORE_PROMPT
from pydantic import BaseModel

app = FastAPI(title="AI面试服务")

# 请求体模型
class QuestionReq(BaseModel):
    job: str
    difficulty: str

class ScoreReq(BaseModel):
    question: str
    answer: str

# 1. 生成面试题接口
@app.post("/api/ai/generate_question")
async def generate_question(req: QuestionReq):
    prompt = GENERATE_QUESTION_PROMPT.format(job=req.job, difficulty=req.difficulty)
    res = await call_llm(prompt)
    return {"question": res}

# 2. 回答打分接口
@app.post("/api/ai/score_answer")
async def score_answer(req: ScoreReq):
    prompt = SCORE_PROMPT.format(question=req.question, answer=req.answer)
    res = await call_llm(prompt)
    return {"result": res}