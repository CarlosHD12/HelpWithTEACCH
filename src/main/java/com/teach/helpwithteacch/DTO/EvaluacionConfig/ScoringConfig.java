package com.teach.helpwithteacch.DTO.EvaluacionConfig;

import lombok.*;

import java.util.List;

@Getter
@Setter
public class ScoringConfig {
    private String unit;
    private Integer itemScoreMin;
    private Integer itemScoreMax;
    private Boolean partialScore;
    private String responseType;
    private List<OpcionConfig> options;
    private Boolean seriesAreIndependentlyScored;
}