package com.example.loanmoduleservice.service;

import java.math.BigDecimal;

public interface RiskAssessmentService {
    public String assessRisk(BigDecimal incomeToLoanRatio);
}
