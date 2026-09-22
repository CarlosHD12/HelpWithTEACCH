package com.teach.helpwithteacch.DTO.EvaluacionConfig;

import lombok.*;
import tools.jackson.databind.JsonNode;

@Getter
@Setter
public class SerieConfig {
    private Integer id;
    private JsonNode stimulus;
    private JsonNode options;
    private JsonNode expectedAnswer;
    private JsonNode responseGrid;
}