package com.guangge.Interview.analysis;

import org.springframework.stereotype.Component;

@Component
public class NoOpEvaluationProvider implements EvaluationProvider {
    @Override
    public EvaluationOutcome evaluate(EvaluationRequest request) {
        return new EvaluationOutcome(null, "评估功能已关闭（provider=none）");
    }

    @Override
    public EvaluationProvenance provenance() {
        return new EvaluationProvenance("noop", "noop", "none");
    }
}
