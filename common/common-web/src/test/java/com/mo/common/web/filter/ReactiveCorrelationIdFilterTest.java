package com.mo.common.web.filter;

import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

class ReactiveCorrelationIdFilterTest {

    @Test
    void addsCorrelationIdWhenMissing() {
        ReactiveCorrelationIdFilter filter = new ReactiveCorrelationIdFilter();
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test").build());
        WebFilterChain chain = exchange1 -> {
            assertThat(exchange1.getResponse().getHeaders().getFirst(ReactiveCorrelationIdFilter.HEADER)).isNotNull();
            return Mono.empty();
        };

        filter.filter(exchange, chain).block();

        assertThat(exchange.getResponse().getHeaders().getFirst(ReactiveCorrelationIdFilter.HEADER)).isNotNull();
    }

    @Test
    void preservesIncomingCorrelationId() {
        ReactiveCorrelationIdFilter filter = new ReactiveCorrelationIdFilter();
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test")
                .header(ReactiveCorrelationIdFilter.HEADER, "incoming-id")
                .build());
        WebFilterChain chain = exchange1 -> {
            assertThat(exchange1.getResponse().getHeaders().getFirst(ReactiveCorrelationIdFilter.HEADER)).isEqualTo("incoming-id");
            return Mono.empty();
        };

        filter.filter(exchange, chain).block();

        assertThat(exchange.getResponse().getHeaders().getFirst(ReactiveCorrelationIdFilter.HEADER)).isEqualTo("incoming-id");
    }
}
