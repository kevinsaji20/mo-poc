package com.mo.query_service.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

@Slf4j
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        Instant start = Instant.now();

        String method = request.getMethod();
        String path = request.getRequestURI();
        String query = request.getQueryString();
        String remoteAddr = request.getRemoteAddr();

        String fullPath = query != null ? path + "?" + query : path;

        log.info("Incoming request: {} {} from {}", method, fullPath, remoteAddr);

        try {
            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            log.error("Request error: {} {} - {}", method, path, ex.getMessage(), ex);
            throw ex;
        } finally {

            int status = response.getStatus();

            long durationMs = Duration
                    .between(start, Instant.now())
                    .toMillis();

            if (status >= 500) {
                log.error("Completed {} {} with status {} in {} ms", method, path, status, durationMs);
            } else if (status >= 400) {
                log.warn("Completed {} {} with status {} in {} ms", method, path, status, durationMs);
            } else {
                log.info("Completed {} {} with status {} in {} ms", method, path, status, durationMs);
            }
        }
    }
}