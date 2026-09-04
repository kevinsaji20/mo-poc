package com.mo.query_service.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CatalogClient {
    @Value("${spring.rest.catalog-client-uri}")
    private String catalogClientURI;
    private final RestClient restClient;

    public boolean contentExists(UUID contentId) {
        try {
            restClient
                    .get()
                    .uri(catalogClientURI)
                    .retrieve()
                    .toBodilessEntity();

            return true;
        } catch (HttpClientErrorException exception) {
            return false;
        }
    }
}
