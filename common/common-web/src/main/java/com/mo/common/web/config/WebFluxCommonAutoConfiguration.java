package com.mo.common.web.config;

import com.mo.common.web.filter.ReactiveCorrelationIdFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class WebFluxCommonAutoConfiguration {
    @Bean
    public ReactiveCorrelationIdFilter reactiveCorrelationIdFilter() {
        return new ReactiveCorrelationIdFilter();
    }
}
