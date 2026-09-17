package com.mo.api_gateway.service;

import com.mo.api_gateway.entity.SecurityAuditLog;
import com.mo.api_gateway.repository.SecurityAuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityAuditLogService {
    private final SecurityAuditLogRepository securityAuditLogRepository;

    @Async
    public void log(SecurityAuditLog log) {
        securityAuditLogRepository.save(log);
    }
}
