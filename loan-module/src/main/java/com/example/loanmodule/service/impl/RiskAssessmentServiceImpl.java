package com.example.loanmodule.service.impl;

import com.example.loanmodule.service.RiskAssessmentService;
import org.springframework.stereotype.Service;

@Service
public class RiskAssessmentServiceImpl implements RiskAssessmentService {

    public String assessRisk(double incomeToLoanRatio) {
        if (incomeToLoanRatio >= 3) {
            return "LOW";
        } else if (incomeToLoanRatio >= 1.5) {
            return "MEDIUM";
        } else {
            return "HIGH";
        }
    }
}
