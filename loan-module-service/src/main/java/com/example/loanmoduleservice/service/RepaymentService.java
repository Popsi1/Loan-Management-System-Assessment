package com.example.loanmoduleservice.service;

import com.example.loanmoduleservice.dtos.request.Repayment;
import com.example.loanmoduleservice.entity.LoanApplication;
import com.example.loanmoduleservice.entity.RepaymentSchedule;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.math.BigDecimal;
import java.util.List;

public interface RepaymentService {
    public List<RepaymentSchedule> generateRepaymentSchedule(LoanApplication loanApplication);
    public BigDecimal calculatePenalty(RepaymentSchedule schedule);

    public RepaymentSchedule rePayment(Long repaymentScheduleId, Repayment repayment, Long userId) throws JsonProcessingException;
}
