package com.example.loanmoduleservice.repository;

import com.example.loanmoduleservice.entity.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionLogRepository extends JpaRepository<TransactionLog, Long> {

    @Query("SELECT t FROM TransactionLog t WHERE t.userId = :userId AND t.timestamp BETWEEN :startDate AND :endDate")
    List<TransactionLog> findByUserIdAndTimestampBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);
}
