package com.example.loanmoduleservice.service.impl;

import com.example.loanmoduleservice.service.RiskAssessmentService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class RiskAssessmentServiceImpl implements RiskAssessmentService {

    public String assessRisk(BigDecimal incomeToLoanRatio) {
        if (incomeToLoanRatio.compareTo(BigDecimal.valueOf(3)) >= 0) {
            return "LOW";
        } else if (incomeToLoanRatio.compareTo(BigDecimal.valueOf(1.5)) >= 0) {
            return "MEDIUM";
        } else {
            return "HIGH";
        }
    }
}
