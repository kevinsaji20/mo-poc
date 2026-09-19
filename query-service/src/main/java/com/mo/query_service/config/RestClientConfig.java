package com.mo.query_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    @Value("${spring.rest.catalog-client-uri}")
    private String catalogClientUri;

    @Bean
    public RestClient catalogRestClient() {
        return RestClient.builder()
                .baseUrl(catalogClientUri)
                .build();
    }
}
