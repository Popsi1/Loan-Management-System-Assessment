package com.example.loanmoduleservice.repository;

import com.example.loanmoduleservice.entity.AuditLog;
import com.example.loanmoduleservice.entity.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
