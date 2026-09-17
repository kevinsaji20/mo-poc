package com.mo.api_gateway.config;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter implements WebFilter {

    @Override
    @NonNull
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        Instant start = Instant.now();

        String method = request.getMethod().name();
        String path = request.getURI().getPath();
        String query = request.getURI().getQuery();
        InetSocketAddress remote = request.getRemoteAddress();
        String remoteAddr = remote != null ? remote.getAddress().getHostAddress() : "unknown";

        log.info("Incoming request: {} {}{} from {}", method, path, query != null ? "?" + query : "", remoteAddr);

        return chain.filter(exchange)
                .doOnError(throwable -> {
                    // Log exceptions that occur during processing
                    log.error("Request error: {} {} - {}", method, path, throwable.getMessage(), throwable);
                })
                .doFinally(signalType -> {
                    // Log response status and total time
                    Integer status = exchange.getResponse().getStatusCode() != null
                            ? exchange.getResponse().getStatusCode().value()
                            : null;

                    long durationMs = Duration.between(start, Instant.now()).toMillis();

                    if (status != null) {
                        if (status >= 500) {
                            log.error("Completed {} {} with status {} in {} ms", method, path, status, durationMs);
                        } else if (status >= 400) {
                            log.warn("Completed {} {} with status {} in {} ms", method, path, status, durationMs);
                        } else {
                            log.info("Completed {} {} with status {} in {} ms", method, path, status, durationMs);
                        }
                    } else {
                        log.info("Completed {} {} with unknown status in {} ms", method, path, durationMs);
                    }
                });
    }
}
