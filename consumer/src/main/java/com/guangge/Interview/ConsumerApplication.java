package com.guangge.Interview;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(excludeName = {
		"com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeImageAutoConfiguration",
		"com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeEmbeddingAutoConfiguration",
		"com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeMultimodalEmbeddingAutoConfiguration",
		"com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeVideoAutoConfiguration",
		"com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeRerankAutoConfiguration",
		"com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeAudioSpeechAutoConfiguration",
		"com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeAudioTranscriptionAutoConfiguration",
		"com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeChatAutoConfiguration",
		"com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeAgentAutoConfiguration",
		"org.springframework.ai.model.chat.client.autoconfigure.ChatClientAutoConfiguration"
})
@EnableScheduling
public class ConsumerApplication {

	private static final Logger logger = LoggerFactory.getLogger(ConsumerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ConsumerApplication.class, args);
	}

	@Bean
	public ChatMemory chatMemory() {
		return MessageWindowChatMemory.builder().maxMessages(20).build();
	}
}
