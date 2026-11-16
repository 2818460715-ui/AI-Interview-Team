package com.guangge.Interview.practice.dto;

import org.springframework.ai.chat.messages.*;
import java.util.ArrayList;
import java.util.List;

public class MsgConverter {

    // ChatRecord → SpringAI Message
    public static List<Message> toAiMessage(List<ChatRecord> records) {
        List<Message> msgs = new ArrayList<>();
        for (ChatRecord r : records) {
            switch (r.getRole()) {
                case "SYSTEM" -> msgs.add(new SystemMessage(r.getContent()));
                case "USER" -> msgs.add(new UserMessage(r.getContent()));
                case "ASSISTANT" -> msgs.add(new AssistantMessage(r.getContent()));
            }
        }
        return msgs;
    }

    // SpringAI Message → ChatRecord
    public static List<ChatRecord> toRecordList(List<Message> aiMessages) {
        List<ChatRecord> list = new ArrayList<>();
        for (Message m : aiMessages) {
            String role = switch (m.getMessageType()) {
                case SYSTEM -> "SYSTEM";
                case USER -> "USER";
                case ASSISTANT -> "ASSISTANT";
                default -> "OTHER";
            };
            list.add(new ChatRecord(role, m.getText()));
        }
        return list;
    }
}