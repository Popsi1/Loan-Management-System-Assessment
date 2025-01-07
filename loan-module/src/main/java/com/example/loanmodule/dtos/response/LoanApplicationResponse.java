package com.example.loanmodule.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoanApplicationResponse {

    private Long id;

    private Long userId;

    private String fullName;

    private String email;

    private Double loanAmount;

    private Integer tenure;

    private Double annualIncome;

    private String status;
    private Double interestRate;
    private String riskLevel;
    private Double emi;
    private String createdAt;
}
