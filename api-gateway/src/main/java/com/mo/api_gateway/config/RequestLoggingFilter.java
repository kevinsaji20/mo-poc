package com.mo.api_gateway.config;

import com.mo.api_gateway.entity.SecurityAuditLog;
import com.mo.api_gateway.enums.SecurityEventStatus;
import com.mo.api_gateway.enums.SecurityEventType;
import com.mo.api_gateway.service.SecurityAuditLogService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class RequestLoggingFilter implements WebFilter {
    private final SecurityAuditLogService securityAuditLogService;

    @Override
    @NonNull
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        OffsetDateTime start = OffsetDateTime.now();

        String method = request.getMethod().name();
        String path = request.getURI().getPath();
        String query = request.getURI().getQuery();
        InetSocketAddress remote = request.getRemoteAddress();
        String remoteAddr = remote != null ? remote.getAddress().getHostAddress() : "unknown";
        Map<String, Object> metadata = new HashMap<>();

        log.info("Incoming request: {} {}{} from {}", method, path, query != null ? "?" + query : "", remoteAddr);

        return chain.filter(exchange)
                .doOnError(throwable -> {
                    // Log exceptions that occur during processing
                    log.error("Request error: {} {} - {}", method, path, throwable.getMessage(), throwable);
                    metadata.put("error", throwable.getMessage());
                })
                .doFinally(signalType -> {
                    SecurityEventStatus eventStatus = SecurityEventStatus.SUCCESS;
                    // Log response status and total time
                    Integer status = exchange.getResponse().getStatusCode() != null
                            ? exchange.getResponse().getStatusCode().value()
                            : null;

                    long durationMs = Duration.between(start, OffsetDateTime.now()).toMillis();

                    if (status != null) {
                        if (status >= 500) {
                            eventStatus = SecurityEventStatus.FAILED;
                            log.error("Completed {} {} with status {} in {} ms", method, path, status, durationMs);
                        } else if (status >= 400) {
                            eventStatus = SecurityEventStatus.FAILED;
                            log.warn("Completed {} {} with status {} in {} ms", method, path, status, durationMs);
                        } else {
                            log.info("Completed {} {} with status {} in {} ms", method, path, status, durationMs);
                        }
                    } else {
                        eventStatus = SecurityEventStatus.FAILED;
                        log.info("Completed {} {} with unknown status in {} ms", method, path, durationMs);
                    }

                    metadata.put("durationMs", durationMs);

                    if (!method.equals("GET") && !method.equals("HEAD") && !method.equals("OPTIONS")) {
                        securityAuditLogService.log(new SecurityAuditLog(
                                null,
                                null,
                                SecurityEventType.COMMON,
                                eventStatus,
                                remoteAddr,
                                request.getHeaders().getFirst("User-Agent"),
                                request.getHeaders().getFirst("X-Device-Id"), // Device ID can be set if available
                                path,
                                method,
                                request.getId(),
                                "Request completed with status " + (status != null ? status : "unknown"),
                                metadata, // Metadata can be added if needed
                                OffsetDateTime.now()
                        ));
                    }
                });
    }
}
