package com.guangge.Interview.practice.dto;

import lombok.Data;

@Data
public class PromptRequest {
    private String prompt;
    private String sessionId;
}