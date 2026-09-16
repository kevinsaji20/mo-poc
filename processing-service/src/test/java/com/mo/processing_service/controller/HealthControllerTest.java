package com.mo.processing_service.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HealthControllerTest {

    @Test
    void health_returnsServiceRunningMessage() {
        HealthController controller = new HealthController();

        assertThat(controller.health()).isEqualTo("Processing Service Running");
    }
}
