package com.guangge.Interview.practice.dto; // 改成你自己的包，比如 com.light.interview.entity

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRecord {
    // 角色：SYSTEM / USER / ASSISTANT
    private String role;
    // 消息内容
    private String content;
}