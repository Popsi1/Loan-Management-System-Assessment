package com.example.loanmoduleservice.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@Builder
public class LoanDisbursementResponse {

    private Long id;

    private String fullName;
    private String email;

    private String accountNumber;

    private BigDecimal disbursedAmount;
    private String transactionId;

    private LocalDateTime disbursementDate;
    private String status;
}
