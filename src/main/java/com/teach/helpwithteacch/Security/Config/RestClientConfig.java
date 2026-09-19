package com.teach.helpwithteacch.Security.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${ia.service.url}")
    private String iaServiceUrl;

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(iaServiceUrl)
                .build();
    }
}