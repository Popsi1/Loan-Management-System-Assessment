package com.example.loanmodule.service;

import com.example.loanmodule.dtos.request.Repayment;
import com.example.loanmodule.entity.LoanApplication;
import com.example.loanmodule.entity.RepaymentSchedule;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface RepaymentService {
    public List<RepaymentSchedule> generateRepaymentSchedule(LoanApplication loanApplication);
    public Double calculatePenalty(RepaymentSchedule schedule);

    public RepaymentSchedule rePayment(Long repaymentScheduleId, Repayment repayment, Long userId) throws JsonProcessingException;
}
