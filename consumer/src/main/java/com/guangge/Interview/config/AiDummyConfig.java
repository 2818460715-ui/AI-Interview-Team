//package com.guangge.Interview.config;
//
//import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
//import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
//import org.springframework.ai.chat.model.ChatResponse;
//import org.springframework.ai.chat.prompt.Prompt;
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//
//@Configuration
//public class AiDummyConfig {
//
//    @Bean
//    @Primary
//    public DashScopeChatModel dashScopeChatModel() {
//        return new DashScopeChatModel(null, DashScopeChatOptions.builder().build()) {
//            @Override
//            public ChatResponse call(Prompt prompt) {
//                throw new UnsupportedOperationException("Dummy DashScopeChatModel，仅占位，不要调用真实LLM");
//            }
//        };
//    }
//
//    @Bean
//    public ChatClient.Builder chatClientBuilder(DashScopeChatModel dashScopeChatModel) {
//        return ChatClient.builder(dashScopeChatModel);
//    }
//}
