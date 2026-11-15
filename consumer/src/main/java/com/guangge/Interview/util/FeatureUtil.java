package com.guangge.Interview.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FeatureUtil {

    @Value("${feature.enable-free-chat:false}")
    private boolean enableFreeChat;

    @Value("${feature.enable-cohort:false}")
    private boolean enableCohort;

    @Value("${feature.enable-code-practice:false}")
    private boolean enableCodePractice;

    public void checkFreeChat() {
        if (!enableFreeChat) {
            throw new RuntimeException("闲聊功能已关闭");
        }
    }

    public void checkCohort() {
        if (!enableCohort) {
            throw new RuntimeException("小组互评模块已关闭");
        }
    }

    public void checkCodePractice() {
        if (!enableCodePractice) {
            throw new RuntimeException("代码刷题模块已关闭");
        }
    }

    public boolean isEnableFreeChat() {
        return enableFreeChat;
    }

    public boolean isEnableCohort() {
        return enableCohort;
    }

    public boolean isEnableCodePractice() {
        return enableCodePractice;
    }
}

