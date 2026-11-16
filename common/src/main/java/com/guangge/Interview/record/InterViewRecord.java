package com.guangge.Interview.record;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record InterViewRecord(
        @JsonPropertyDescription("序号") String number,
        @JsonPropertyDescription("分数") int score,
        @JsonPropertyDescription("笔试评语") String evaluate
        // 【删掉 mp3path，其余保留】
        // 如果还有 question、userAnswer 这类字段，原样保留
) {}
