package com.example.loanmoduleservice.dtos.response;

import lombok.Builder;
import lombok.Data;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Builder
@ToString
public class LoanApplicationResponse {

    private final Long id;

    private final Long userId;

    private final String fullName;

    private final String email;

    private final BigDecimal loanAmount;

    private final Integer tenure;

    private final BigDecimal annualIncome;

    private final String status;

    private final BigDecimal interestRate;

    private final String riskLevel;

    private final BigDecimal emi;

    private final String createdAt;
    private final boolean isAccountDisburse;
}

