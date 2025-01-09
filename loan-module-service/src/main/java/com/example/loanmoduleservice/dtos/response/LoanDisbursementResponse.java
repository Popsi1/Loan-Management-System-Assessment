package com.example.loanmoduleservice.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Builder
public class LoanDisbursementResponse {

    private Long id;

    private String fullName;
    private String email;

    private String accountNumber;

    private Double disbursedAmount;
    private String transactionId;

    private LocalDateTime disbursementDate;
    private String status;
}
