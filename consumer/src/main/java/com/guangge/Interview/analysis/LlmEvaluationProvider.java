package com.guangge.Interview.analysis;

import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class LlmEvaluationProvider implements EvaluationProvider {

    @Override
    public EvaluationOutcome evaluate(EvaluationRequest request) {
        String rawResponse = """
                {
                    "overallScore": 76.5,
                    "scores": {
                        "relevance": 80.0,
                        "completeness": 72.0,
                        "professionalism": 75.0,
                        "starStructure": 70.0,
                        "logic": 78.0
                    },
                    "delivery": {
                        "speechRateCharactersPerMinute": 180.0,
                        "pauseCount": 3,
                        "totalPauseSeconds": 2.4,
                        "fillerWordCount": 2
                    },
                    "improvementSuggestions": [
                        "可以补充项目落地遇到的风险细节",
                        "回答结尾增加总结，强化STAR闭环"
                    ],
                    "sampleAnswer": "参考回答：先描述场景，明确任务，说明行动，最后给出结果...",
                    "evidence": [
                        {
                            "dimension": "completeness",
                            "quote": "我负责后端接口开发",
                            "startSeconds": 5.2,
                            "endSeconds": 8.1
                        }
                    ]
                }
                """;

        EvaluationResult.DimensionScores dimensionScores = new EvaluationResult.DimensionScores(
                80.0,
                72.0,
                75.0,
                70.0,
                78.0
        );
        EvaluationResult.DeliveryMetrics deliveryMetrics = new EvaluationResult.DeliveryMetrics(
                180.0,
                3,
                2.4,
                2
        );
        EvaluationResult.Evidence evidence = new EvaluationResult.Evidence(
                "completeness",
                "我负责后端接口开发",
                5.2,
                8.1
        );

        EvaluationResult result = new EvaluationResult(
                76.5,
                dimensionScores,
                deliveryMetrics,
                List.of("可以补充项目落地遇到的风险细节", "回答结尾增加总结，强化STAR闭环"),
                "参考回答：先描述场景，明确任务，说明行动，最后给出结果...",
                List.of(evidence)
        );

        return new EvaluationOutcome(result, rawResponse);
    }

    @Override
    public EvaluationProvenance provenance() {
        return new EvaluationProvenance("qwen3", "0.1", "v1");
    }
}
