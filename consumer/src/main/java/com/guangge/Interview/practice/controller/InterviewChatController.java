package com.guangge.Interview.practice.controller;

import com.guangge.Interview.practice.dto.PromptRequest;
import com.guangge.Interview.practice.service.InterviewChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("/api/interview")
@RequiredArgsConstructor
public class InterviewChatController {

    private final InterviewChatService interviewChatService;

    @Value("${ai.debug.echo-mode:true}")
    private Boolean echoMode;

    @PostMapping("/chat")
    public String chat(@RequestBody PromptRequest request) {
        log.info("==== echoMode = {} ====", echoMode);
        log.info("收到 prompt: {} , sessionId: {}", request.getPrompt(), request.getSessionId());
        if (Boolean.TRUE.equals(echoMode)) {
            log.info("进入 echo 回显分支");
            return request.getPrompt();
        }
        log.info("进入真实 AI 调用分支");
        return interviewChatService.chat(request.getSessionId(), request.getPrompt());
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(
            @RequestParam String sessionId,
            @RequestParam String prompt
    ) {
        log.info("==== echoMode(stream) = {} ====", echoMode);
        log.info("收到流式 prompt: {} , sessionId: {}", prompt, sessionId);
        if (Boolean.TRUE.equals(echoMode)) {
            return Flux.just(prompt);
        }
        return interviewChatService.streamChat(sessionId, prompt);
    }

    @DeleteMapping("/session/{sessionId}")
    public String clearSession(@PathVariable String sessionId) {
        log.info("清空会话 sessionId: {}", sessionId);
        interviewChatService.clearSession(sessionId);
        return "success";
    }
}
