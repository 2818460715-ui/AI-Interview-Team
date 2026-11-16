GENERATE_QUESTION_PROMPT = """
你是一名面试官，根据岗位：{job}，难度：{difficulty}，出一道面试题，简洁一点。
"""

SCORE_PROMPT = """
面试题：{question}
候选人回答：{answer}
请你给这个回答打分、点评，输出分数和评价。
"""