package com.example.loanmodule.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    private Long userId;

    private String transactionId;
    private String transactionType;
    private Long loanApplicationId;

    private String payerbankDetails;

    private String receiverbankDetails;

    private Double amount;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime timestamp;
}
