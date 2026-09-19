package com.mo.query_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
		scanBasePackages = {
				"com.mo.query_service",
				"com.mo.common.security",
				"com.mo.common.kafka",
				"com.mo.common.web"
		}
)
public class QueryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(QueryServiceApplication.class, args);
	}

}
