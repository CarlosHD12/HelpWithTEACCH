package com.teach.helpwithteacch.DTO.PrediccionML;

import lombok.*;

@Getter
@Setter
public class QChatMLResponse {
    private Integer qchat_score;
    private ModeloMLResponse random_forest;
    private ModeloMLResponse xgboost;
}