package com.mo.catalog_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.assertj.core.api.Assertions.assertThat;

class HealthControllerTest {

    private HealthController controller;

    @BeforeEach
    void setup() {
        controller = new HealthController();
    }

    @Test
    void health_shouldReturnRunningString() {
        String res = controller.health();
        assertEquals("Catalog Service is Running", res);
    }
}
