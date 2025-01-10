package com.example.loanmoduleservice.service;

import com.example.loanmoduleservice.entity.AuditLog;
import com.example.loanmoduleservice.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    public void logAuditEvent(String eventDescription, String eventType, Long actionBy) {
        AuditLog auditLog = AuditLog.builder()
                .eventType(eventType)
                .actionBy(actionBy)
                .description(eventDescription)
                .createdAt(LocalDateTime.now())
                .build();

        // Persist Audit Log
        auditLogRepository.save(auditLog);
    }
}