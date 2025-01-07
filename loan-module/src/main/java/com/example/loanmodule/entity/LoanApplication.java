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
public class LoanApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Double loanAmount;

    @Column(nullable = false)
    private Integer tenure;

    @Column(nullable = false)
    private Double annualIncome;

    private String status;
    private Double interestRate;
    private String riskLevel;
    private Double emi; // Equated Monthly Installment

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;
}