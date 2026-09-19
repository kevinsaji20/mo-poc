package com.mo.query_service.client;

import com.mo.query_service.dto.response.CatalogResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CatalogClient {
    @Value("${spring.rest.catalog-client-uri}")
    private String catalogClientURI;

    private final RestClient restClient;
    private final HttpServletRequest request;

    public boolean contentExists(UUID contentId) {
        try {
            String authorization = request.getHeader("Authorization");
            restClient
                    .get()
                    .uri(catalogClientURI + "/" + contentId)
                    .header("Authorization", authorization)
                    .retrieve()
                    .toBodilessEntity();

            return true;
        } catch (HttpClientErrorException exception) {
            System.out.println(exception);
            return false;
        }
    }

    public List<CatalogResponse> getContentByGenre(String genre) {
        String authorization = request.getHeader("Authorization");
        return restClient
                .get()
                .uri(catalogClientURI + "/genre/{genre}", genre)
                .header("Authorization", authorization)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
