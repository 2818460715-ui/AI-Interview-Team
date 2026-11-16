package com.guangge.Interview.config;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    // 注册 ChatModel Bean，解决：找不到ChatModel
    // 注意：启动类已 exclude 掉 DashScopeChatAutoConfiguration，所以这里手动组装。
    // Spring AI Alibaba 1.1.x 里 apiKey 设在 DashScopeApi 上，不再是 DashScopeChatModel.builder().apiKey(...)
    @Bean
    public DashScopeChatModel chatModel(
            @Value("${spring.ai.dashscope.api-key}") String apiKey,
            @Value("${spring.ai.dashscope.chat.options.model:qwen-plus}") String model,
            @Value("${spring.ai.dashscope.chat.options.temperature:0.7}") Double temperature,
            @Value("${spring.ai.dashscope.chat.options.top-p:0.8}") Double topP,
            @Value("${spring.ai.dashscope.chat.options.max-tokens:800}") Integer maxToken) {

        DashScopeApi dashScopeApi = DashScopeApi.builder()
                .apiKey(apiKey)
                .build();

        DashScopeChatOptions options = DashScopeChatOptions.builder()
                .model(model)
                .temperature(temperature)
                .topP(topP)
                .maxToken(maxToken)
                .build();

        return DashScopeChatModel.builder()
                .dashScopeApi(dashScopeApi)
                .defaultOptions(options)
                .build();
    }

    // 提供 ChatClient.Builder Bean：JavaAssistant / ProgramAssistant 都按它注入。
    // 启动类排除了 ChatClientAutoConfiguration，容器里原本没有这个 Bean，这里手动基于 DashScopeChatModel 建一个。
    @Bean
    public ChatClient.Builder chatClientBuilder(ChatModel chatModel) {
        return ChatClient.builder(chatModel);
    }

    // 名字严格叫 assistantChatMemory，解决：找不到限定符assistantChatMemory
    // Spring AI 1.x 已移除 InMemoryChatMemory，改用 MessageWindowChatMemory（与启动类里的 chatMemory 写法一致）
    @Bean("assistantChatMemory")
    public ChatMemory assistantChatMemory() {
        return MessageWindowChatMemory.builder().maxMessages(20).build();
    }
}
