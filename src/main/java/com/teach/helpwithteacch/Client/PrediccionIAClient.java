package com.teach.helpwithteacch.Client;

import com.teach.helpwithteacch.DTO.PrediccionML.QChatMLRequest;
import com.teach.helpwithteacch.DTO.PrediccionML.QChatMLResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PrediccionIAClient {

    private final RestClient restClient;

    public PrediccionIAClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public QChatMLResponse predecir(QChatMLRequest request) {

        return restClient
                .post()
                .uri("/api/v1/predicciones")
                .body(request)
                .retrieve()
                .body(QChatMLResponse.class);
    }
}