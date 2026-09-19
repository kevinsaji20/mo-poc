package com.mo.common.web.filter;

import org.springframework.lang.NonNull;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class ReactiveCorrelationIdFilter implements WebFilter {
    public static final String HEADER = "X-Correlation-Id";

    @Override
    @NonNull
    public Mono<Void> filter(
            @NonNull ServerWebExchange exchange,
            @NonNull WebFilterChain webFilterChain
    ) {
        String correlationId = exchange.getRequest().getHeaders().getFirst(HEADER);
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }
        final String finalCorrelationId = correlationId;
        exchange = exchange.mutate()
                .request(r -> r.header(HEADER, finalCorrelationId))
                .build();
        exchange.getResponse().getHeaders().add(HEADER, finalCorrelationId);

        return webFilterChain.filter(exchange);
    }
}
