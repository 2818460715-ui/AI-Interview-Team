package com.guangge.Interview.practice.service;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "ai", name = "enable", havingValue = "true", matchIfMissing = false)
public class InterviewChatService {

    private final DashScopeChatModel dashScopeChatModel;
    private final ChatHistoryRedisService chatHistoryRedisService;

    private static final String SYS_PROMPT =
            "你是一名温和的校招面试官，本次面试岗位：Java开发，面试对象是应届大学生。题目难度为校招基础难度，不出冷门、深度前沿难题。一次只问一个问题，循序渐进。";

    public String chat(String sessionId, String userPrompt) {
        // 读取历史，直接拿到 List<Message>
        List<Message> history = chatHistoryRedisService.getAiMessageHistory(sessionId);

        // 新会话，塞入系统提示词
        if (history.isEmpty()) {
            chatHistoryRedisService.addMessage(sessionId, "SYSTEM", SYS_PROMPT);
            history.add(new SystemMessage(SYS_PROMPT));
        }

        history.add(new UserMessage(userPrompt));
        Prompt prompt = new Prompt(history);
        String reply = dashScopeChatModel.call(prompt)
                .getResult()
                .getOutput()
                .getText();
        if (reply == null) {
            reply = "";
        }

        // 保存本次对话
        chatHistoryRedisService.addMessage(sessionId, "USER", userPrompt);
        chatHistoryRedisService.addMessage(sessionId, "ASSISTANT", reply);
        return reply;
    }

    public void clearSession(String sessionId) {
        chatHistoryRedisService.clearHistory(sessionId);
    }

    public Flux<String> streamChat(String sessionId, String userPrompt) {
        List<Message> history = chatHistoryRedisService.getAiMessageHistory(sessionId);
        if (history.isEmpty()) {
            chatHistoryRedisService.addMessage(sessionId, "SYSTEM", SYS_PROMPT);
            history.add(new SystemMessage(SYS_PROMPT));
        }
        history.add(new UserMessage(userPrompt));

        Prompt prompt = new Prompt(history);
        StringBuilder fullReply = new StringBuilder();

        return dashScopeChatModel.stream(prompt)
                .map(chunk -> {
                    String text = chunk.getResult().getOutput().getText();
                    if (text != null) {
                        fullReply.append(text);
                    }
                    return text == null ? "" : text;
                })
                .doOnComplete(() -> {
                    String content = fullReply.toString();
                    chatHistoryRedisService.addMessage(sessionId, "USER", userPrompt);
                    chatHistoryRedisService.addMessage(sessionId, "ASSISTANT", content);
                })
                .doOnError(e -> {
                    System.err.println("流式对话异常：" + e.getMessage());
                });
    }
}
