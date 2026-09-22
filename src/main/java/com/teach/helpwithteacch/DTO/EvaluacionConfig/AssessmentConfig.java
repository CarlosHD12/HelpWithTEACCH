package com.teach.helpwithteacch.DTO.EvaluacionConfig;

import lombok.*;

import java.util.List;

@Getter
@Setter
public class AssessmentConfig {
    private String code;
    private String version;
    private String name;
    private String language;
    private ScoringConfig scoring;
    private List<ItemConfig> items;
}