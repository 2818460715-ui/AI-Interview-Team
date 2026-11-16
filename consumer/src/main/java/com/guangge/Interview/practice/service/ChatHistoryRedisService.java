package com.guangge.Interview.practice.service;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ChatHistoryRedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String PREFIX = "interview:chat:session:";
    private static final long EXPIRE_MIN = 120;
    private static final Pattern SEP = Pattern.compile(Pattern.quote("|||"));

    public void addMessage(String sessionId, String role, String content) {
        String key = PREFIX + sessionId;
        String item = role.trim() + "|||" + content;
        redisTemplate.opsForList().rightPush(key, item);
        redisTemplate.expire(key, EXPIRE_MIN, java.util.concurrent.TimeUnit.MINUTES);
    }

    public List<Message> getAiMessageHistory(String sessionId) {
        String key = PREFIX + sessionId;
        List<Object> rawList = redisTemplate.opsForList().range(key, 0, -1);
        List<Message> messages = new ArrayList<>();
        if (rawList == null || rawList.isEmpty()) {
            return messages;
        }
        for (Object obj : rawList) {
            if (!(obj instanceof String raw)) {
                continue;
            }
            if (raw.isBlank()) {
                continue;
            }
            String[] parts = SEP.split(raw, 2);
            if (parts.length < 2) {
                continue;
            }
            String role = parts[0].trim();
            String content = parts[1].trim();
            switch (role) {
                case "SYSTEM" -> messages.add(new SystemMessage(content));
                case "USER" -> messages.add(new UserMessage(content));
                case "ASSISTANT" -> messages.add(new AssistantMessage(content));
                default -> {}
            }
        }
        return messages;
    }

    public void clearHistory(String sessionId) {
        String key = PREFIX + sessionId;
        redisTemplate.delete(key);
    }
}