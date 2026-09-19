package com.mo.api_gateway.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(controllers = HealthController.class, excludeAutoConfiguration = {org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration.class})
public class HealthControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void health_shouldReturnRunningString() {
        webTestClient.get().uri("/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("API Gateway Running");
    }
}
