package com.teach.helpwithteacch.DTO.EvaluacionConfig;

import lombok.*;
import tools.jackson.databind.JsonNode;

import java.util.List;

@Getter
@Setter
public class ItemConfig {
    private Integer id;
    private String code;
    private String subtest;
    private String type;
    private String question;
    private String instruction;
    private List<OpcionConfig> options;
    private List<SerieConfig> series;
    private String scoringRule;
    private Integer maxScore;
    private JsonNode input;
    private JsonNode responseGrid;
}