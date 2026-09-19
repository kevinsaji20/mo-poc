package com.mo.ingestion_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
		scanBasePackages = {
				"com.mo.ingestion_service",
				"com.mo.common.kafka",
				"com.mo.common.security",
				"com.mo.common.web"
		}
)
public class IngestionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(IngestionServiceApplication.class, args);
	}

}
