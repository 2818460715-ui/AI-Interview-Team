package com.guangge.Interview.coding;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
// import lombok.Getter;
// import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * 一次作答与判分结果。
 */
@Entity
@Table(name = "code_submissions")
// @Getter
// @Setter
public class CodeSubmissionEntity {

    @Id
    private UUID id;

    @Column(name = "session_id", nullable = false)
    private UUID sessionId;

    @Column(name = "question_id", nullable = false)
    private UUID questionId;

    @Column(name = "owner_id", nullable = false, length = 255)
    private String ownerId;

    /** 代码 / SQL / 选项 key / 简答文本。 */
    @Column(name = "answer_text", columnDefinition = "text")
    private String answerText;

    @Column(nullable = false, length = 32)
    private String status;

    @Column(name = "passed_cases", nullable = false)
    private int passedCases;

    @Column(name = "total_cases", nullable = false)
    private int totalCases;

    @Column(name = "runtime_ms")
    private Long runtimeMs;

    @Column(columnDefinition = "text")
    private String stdout;

    @Column(columnDefinition = "text")
    private String stderr;

    @Column(name = "compile_output", columnDefinition = "text")
    private String compileOutput;

    @Column(name = "case_results_json", columnDefinition = "text")
    private String caseResultsJson;

    @Column(name = "ai_review_json", columnDefinition = "text")
    private String aiReviewJson;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public UUID getQuestionId() {
        return questionId;
    }

    public void setQuestionId(UUID questionId) {
        this.questionId = questionId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPassedCases() {
        return passedCases;
    }

    public void setPassedCases(int passedCases) {
        this.passedCases = passedCases;
    }

    public int getTotalCases() {
        return totalCases;
    }

    public void setTotalCases(int totalCases) {
        this.totalCases = totalCases;
    }

    public Long getRuntimeMs() {
        return runtimeMs;
    }

    public void setRuntimeMs(Long runtimeMs) {
        this.runtimeMs = runtimeMs;
    }

    public String getStdout() {
        return stdout;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    public String getStderr() {
        return stderr;
    }

    public void setStderr(String stderr) {
        this.stderr = stderr;
    }

    public String getCompileOutput() {
        return compileOutput;
    }

    public void setCompileOutput(String compileOutput) {
        this.compileOutput = compileOutput;
    }

    public String getCaseResultsJson() {
        return caseResultsJson;
    }

    public void setCaseResultsJson(String caseResultsJson) {
        this.caseResultsJson = caseResultsJson;
    }

    public String getAiReviewJson() {
        return aiReviewJson;
    }

    public void setAiReviewJson(String aiReviewJson) {
        this.aiReviewJson = aiReviewJson;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
