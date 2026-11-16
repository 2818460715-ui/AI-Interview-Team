package com.guangge.Interview.coding;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guangge.Interview.coding.dto.CodingPlanRequest;
import com.guangge.Interview.coding.dto.JudgeOutcome;
import com.guangge.Interview.coding.dto.TestCase;
import com.guangge.Interview.util.JsonText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * AI 出题（含自检闭环）。
 *
 * 【修改说明】CODING、SQL 题型已禁用，不再使用 Docker 沙箱校验参考解
 * 只支持：FILL_BLANK、CHOICE、SHORT_ANSWER
 */
@Service
public class CodingQuestionPlanService {

    private static final Logger log = LoggerFactory.getLogger(CodingQuestionPlanService.class);

    private final ChatClient chatClient;
    private final ChatClient testcaseChatClient;
    private final ObjectMapper objectMapper;
    private final CodeJudgeService judge;
    private final int maxAttempts;

    // 移除 CodeRunnerService（Docker 沙箱）
    public CodingQuestionPlanService(
            ChatClient.Builder chatClientBuilder,
            ObjectMapper objectMapper,
            CodeJudgeService judge,
            @Value("classpath:prompt/coding-question-plan-system-prompt.st") Resource systemPrompt,
            @Value("classpath:prompt/coding-testcase-system-prompt.st") Resource testcasePrompt,
            @Value("${practice.coding.plan-max-attempts:3}") int maxAttempts) {
        this.chatClient = chatClientBuilder.clone().defaultSystem(systemPrompt).build();
        this.testcaseChatClient = chatClientBuilder.clone().defaultSystem(testcasePrompt).build();
        this.objectMapper = objectMapper;
        this.judge = judge;
        this.maxAttempts = Math.max(1, maxAttempts);
    }

    public List<CodeQuestionEntity> generate(UUID sessionId, CodingPlanRequest request) {
        List<String> lastFailure = new ArrayList<>();
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return generateOnce(sessionId, request);
            } catch (ResponseStatusException | IllegalStateException exception) {
                lastFailure.add(exception.getMessage());
                log.warn("第 {} 次出题未通过校验：{}", attempt, exception.getMessage());
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                "AI 出题未通过校验，已重试 " + maxAttempts + " 次。最后一次原因："
                        + (lastFailure.isEmpty() ? "未知" : lastFailure.get(lastFailure.size() - 1)));
    }

    private List<CodeQuestionEntity> generateOnce(UUID sessionId, CodingPlanRequest request) {
        // 前置拦截：如果请求里包含 CODING / SQL，直接拒绝
        for (CodingPlanRequest.QuestionSpec spec : request.questions()) {
            QuestionType type = QuestionType.fromId(spec.type()).orElse(null);
            if (type == QuestionType.CODING || type == QuestionType.SQL) {
                throw new IllegalStateException("当前已关闭代码上机、SQL 沙箱题型，无法生成");
            }
        }

        String raw = chatClient.prompt().user(userMessage(request)).call().content();
        try {
            JsonNode root = parseArray(raw);
            if (root.size() != request.questions().size()) {
                throw new IllegalStateException("题目数量与要求不一致：期望 "
                        + request.questions().size() + "，实际 " + root.size());
            }

            List<CodeQuestionEntity> questions = new ArrayList<>();
            for (int index = 0; index < root.size(); index++) {
                CodingPlanRequest.QuestionSpec spec = request.questions().get(index);
                CodeQuestionEntity entity = toEntity(sessionId, index, spec, root.get(index));
                // deriveExpectedOutputs：只对 CODING / SQL 生效，现在不会走到
                questions.add(entity);
            }
            return questions;
        } catch (RuntimeException exception) {
            log.warn("出题结果校验失败：{}；模型返回前 600 字：{}", exception.getMessage(), truncate(raw, 600));
            throw exception;
        }
    }

    private static String truncate(String value, int max) {
        if (value == null) return "(空)";
        return value.length() <= max ? value : value.substring(0, max) + "…";
    }

    private CodeQuestionEntity toEntity(UUID sessionId, int index,
                                        CodingPlanRequest.QuestionSpec spec, JsonNode node) {
        QuestionType type = QuestionType.fromId(spec.type())
                .orElseThrow(() -> new IllegalStateException("未知题型：" + spec.type()));
        String title = text(node, "title");
        String statement = text(node, "statement");
        if (title.isBlank() || statement.isBlank()) {
            throw new IllegalStateException("第 " + (index + 1) + " 题缺少 title/statement");
        }

        CodeQuestionEntity entity = new CodeQuestionEntity();
        entity.setId(UUID.randomUUID());
        entity.setSessionId(sessionId);
        entity.setPosition(index);
        entity.setType(type.id());
        entity.setTitle(title);
        entity.setStatement(statement);
        entity.setDifficulty(node.hasNonNull("difficulty") ? node.get("difficulty").asText() : null);
        entity.setTags(node.hasNonNull("tags") ? node.get("tags").asText() : null);
        entity.setStarterCode(node.hasNonNull("starterCode") ? node.get("starterCode").asText() : null);
        entity.setBlanksJson(json(node.get("blanks")));
        entity.setOptionsJson(json(node.get("options")));
        entity.setTestCasesJson(json(node.get("testCases")));
        entity.setSqlSetup(node.hasNonNull("sqlSetup") ? node.get("sqlSetup").asText() : null);
        entity.setAnswerJson(json(node.get("answer")));
        entity.setCreatedAt(Instant.now());

        validateShape(type, entity, index);
        return entity;
    }

    private void validateShape(QuestionType type, CodeQuestionEntity entity, int index) {
        String prefix = "第 " + (index + 1) + " 题（" + type.id() + "）";
        switch (type) {
            case CODING, SQL -> throw new IllegalStateException(prefix + "：该题型已禁用");
            case FILL_BLANK -> {
                if (isBlank(entity.getStarterCode())) throw new IllegalStateException(prefix + " 缺少 starterCode");
                if (readNode(entity.getBlanksJson()).isEmpty()) throw new IllegalStateException(prefix + " 缺少 blanks");
            }
            case CHOICE -> {
                if (readNode(entity.getOptionsJson()).size() < 2) throw new IllegalStateException(prefix + " 选项不足");
                if (answerArray(entity).isEmpty()) throw new IllegalStateException(prefix + " 缺少正确选项");
            }
            case SHORT_ANSWER -> {
                if (answerArray(entity).isEmpty()) throw new IllegalStateException(prefix + " 缺少参考要点");
            }
        }
    }

    /*
    // 【停用】沙箱自检：用来跑参考解生成测试用例，依赖 Docker
    private void deriveExpectedOutputs(CodeQuestionEntity entity) {
        QuestionType type = QuestionType.fromId(entity.getType()).orElseThrow();
        if (type == QuestionType.CODING) {
            // ...原代码全部封存
        } else if (type == QuestionType.SQL) {
            // ...原代码全部封存
        }
    }

    private List<TestCase> requestTestInputs(CodeQuestionEntity entity) {
        // ...原代码封存
    }

    private static String failureBrief(CodeRunResult run) {
        // ...原代码封存
    }
    */

    private List<TestCase> readCases(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, TestCase.class));
        } catch (Exception exception) {
            return List.of();
        }
    }

    private String reference(CodeQuestionEntity entity) {
        try {
            JsonNode answer = objectMapper.readTree(entity.getAnswerJson());
            return answer != null && answer.hasNonNull("code") ? answer.get("code").asText() : "";
        } catch (Exception exception) {
            return "";
        }
    }

    private List<JsonNode> answerArray(CodeQuestionEntity entity) {
        try {
            JsonNode answer = objectMapper.readTree(entity.getAnswerJson());
            if (answer == null) return List.of();
            for (String field : List.of("keys", "points")) {
                if (answer.hasNonNull(field) && answer.get(field).isArray() && answer.get(field).size() > 0) {
                    return toList(answer.get(field));
                }
            }
            return List.of();
        } catch (Exception exception) {
            return List.of();
        }
    }

    private List<JsonNode> readNode(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            JsonNode node = objectMapper.readTree(json);
            return node != null && node.isArray() ? toList(node) : List.of();
        } catch (Exception exception) {
            return List.of();
        }
    }

    private List<JsonNode> toList(JsonNode array) {
        List<JsonNode> items = new ArrayList<>();
        array.forEach(items::add);
        return items;
    }

    private JsonNode parseArray(String raw) {
        if (raw == null || raw.isBlank()) throw new IllegalStateException("模型返回为空");
        try {
            JsonNode node = objectMapper.readTree(JsonText.stripCodeFence(raw));
            if (node == null || !node.isArray() || node.isEmpty()) {
                throw new IllegalStateException("模型返回的不是非空 JSON 数组");
            }
            return node;
        } catch (IllegalStateException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalStateException("模型返回的 JSON 无法解析：" + exception.getMessage());
        }
    }

    private String json(JsonNode node) {
        if (node == null || node.isNull()) return null;
        try {
            return objectMapper.writeValueAsString(node);
        } catch (Exception exception) {
            throw new IllegalStateException("题目数据序列化失败", exception);
        }
    }

    private static String text(JsonNode node, String field) {
        return node.hasNonNull(field) ? node.get(field).asText() : "";
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String userMessage(CodingPlanRequest request) {
        StringBuilder specs = new StringBuilder();
        for (int index = 0; index < request.questions().size(); index++) {
            CodingPlanRequest.QuestionSpec spec = request.questions().get(index);
            specs.append(index + 1).append(". ").append(spec.type());
            if (spec.language() != null && !spec.language().isBlank()) {
                specs.append(" / ").append(spec.language());
            }
            specs.append('\n');
        }
        return """
                岗位：%s
                公司：%s
                岗位描述：%s
                难度：%s
                请按顺序出以下 %d 道题，输出 JSON 数组：
                %s
                """.formatted(
                request.role(),
                request.company() == null || request.company().isBlank() ? "（未提供）" : request.company(),
                request.jobDescription() == null || request.jobDescription().isBlank() ? "（未提供）" : request.jobDescription(),
                request.difficulty() == null || request.difficulty().isBlank() ? "standard" : request.difficulty(),
                request.questions().size(),
                specs);
    }
}