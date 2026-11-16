package com.guangge.Interview.assistant;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@ConditionalOnProperty(prefix = "ai", name = "enable", havingValue = "true", matchIfMissing = false)
public class InterviewAssistant {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final String systemPrompt;

    public InterviewAssistant(ChatModel chatModel,
                              @Qualifier("assistantChatMemory") ChatMemory chatMemory,
                              @Value("classpath:prompt/Interview-face2face-System-Prompt.st") Resource systemPromptRes) throws Exception {
        this.chatMemory = chatMemory;
        this.systemPrompt = systemPromptRes.getContentAsString(StandardCharsets.UTF_8);
        this.chatClient = ChatClient.builder(chatModel)
                .defaultOptions(DashScopeChatOptions.builder()
                        .model("qwen-plus")
                        .temperature(0.7)
                        .build())
                .build();
    }

    public String chat(String chatId, String userMsg) {
        List<Message> history = chatMemory.get(chatId);
        UserMessage userMessage = new UserMessage(userMsg);

        String resp = chatClient.prompt()
                .system(systemPrompt)
                .messages(history)
                .user(userMsg)
                .call()
                .content();

        String content = resp == null ? "" : resp;
        chatMemory.add(chatId, userMessage);
        chatMemory.add(chatId, new AssistantMessage(content));
        return content;
    }

    public Flux<String> streamChat(String chatId, String userMsg) {
        List<Message> history = chatMemory.get(chatId);
        UserMessage userMessage = new UserMessage(userMsg);
        StringBuilder sb = new StringBuilder();

        return chatClient.prompt()
                .system(systemPrompt)
                .messages(history)
                .user(userMsg)
                .stream()
                .content()
                .doOnNext(sb::append)
                .doOnComplete(() -> {
                    chatMemory.add(chatId, userMessage);
                    chatMemory.add(chatId, new AssistantMessage(sb.toString()));
                });
    }

    // 和 Controller 对接的方法，参数改成英文，里面直接复用 streamChat
    public Flux<String> chatByStream(String chatId, String userMsg) {
        return streamChat(chatId, userMsg);
    }
}
