package com.example.loanmoduleservice.entity;

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
public class LoanDisbursement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private Long loanApplicationId;

    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private Double disbursedAmount;

    @Column(nullable = false)
    private String transactionId;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime disbursementDate;
    private String status;
}
