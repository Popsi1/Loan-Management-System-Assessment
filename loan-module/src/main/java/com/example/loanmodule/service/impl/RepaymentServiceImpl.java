package com.example.loanmodule.service.impl;

import com.example.loanmodule.dtos.request.Repayment;
import com.example.loanmodule.entity.LoanApplication;
import com.example.loanmodule.entity.RepaymentSchedule;
import com.example.loanmodule.enums.TransactionType;
import com.example.loanmodule.exception.BadRequestException;
import com.example.loanmodule.exception.ResourceNotFoundException;
import com.example.loanmodule.repository.RepaymentScheduleRepository;
import com.example.loanmodule.service.FundTransferService;
import com.example.loanmodule.service.RepaymentService;
import com.example.loanmodule.service.TransactionLogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RepaymentServiceImpl implements RepaymentService {
    private final RepaymentScheduleRepository repaymentScheduleRepository;
    private final FundTransferService fundTransferService;
    private final TransactionLogService transactionLogService;

    private static final double PENALTY_RATE_PER_DAY = 0.02; // 2% daily penalty rate

    public List<RepaymentSchedule> generateRepaymentSchedule(LoanApplication loanApplication) {
        List<RepaymentSchedule> schedule = new ArrayList<>();
        Double monthlyInstallment = loanApplication.getLoanAmount() / loanApplication.getTenure();

        for (int i = 1; i <= loanApplication.getTenure(); i++) {
            RepaymentSchedule installment = new RepaymentSchedule();
            installment.setLoanApplicationId(loanApplication.getId());
            installment.setDueDate(LocalDate.now().plusMonths(i));
            installment.setInstallmentAmount(monthlyInstallment);
            installment.setCreatedAt(LocalDateTime.now());
            schedule.add(installment);
        }

        return repaymentScheduleRepository.saveAll(schedule);
    }

    @Transactional
    public RepaymentSchedule rePayment(Long repaymentScheduleId, Repayment repayment, Long userId) throws JsonProcessingException {
        Optional<RepaymentSchedule> scheduleOpt = repaymentScheduleRepository.findById(repaymentScheduleId);

        if (scheduleOpt.isEmpty()) {
            throw new ResourceNotFoundException("Repayment schedule not found");
        }

        RepaymentSchedule schedule = scheduleOpt.get();

        if (schedule.getIsPaid()) {
            throw new BadRequestException("Payment already completed for this installment");
        }

        if (repayment.getAmount() < schedule.getInstallmentAmount()) {
            throw new BadRequestException("Paid amount is less than the installment amount");
        }

        String transactionId;

        try {
            // Simulate fund transfer
            transactionId = fundTransferService.transferFunds(repayment.getSenderBankDetails(), repayment.getReceiverBankDetails(), repayment.getAmount());
            schedule.setIsPaid(true);
            schedule.setPaymentDate(LocalDateTime.now());
        } catch(Exception e){
            schedule.setIsPaid(false);
            throw new RuntimeException("Loan repayment failed: " + e.getMessage(), e);
        }

        repaymentScheduleRepository.save(schedule);

        transactionLogService.recordTransaction(schedule.getLoanApplicationId(),
                repayment.getSenderBankDetails(), repayment.getReceiverBankDetails(),
                repayment.getAmount(), transactionId, TransactionType.REPAY.name(), userId);

        return repaymentScheduleRepository.save(schedule);
    }

    /**
     * Calculate penalty for an overdue payment.
     *
     * @param schedule The overdue repayment schedule.
     * @return The calculated penalty.
     */
    public Double calculatePenalty(RepaymentSchedule schedule) {
        if (schedule.getIsPaid()) {
            return 0.0;
        }

        long overdueDays = ChronoUnit.DAYS.between(schedule.getDueDate(), LocalDate.now());
        if (overdueDays <= 0) {
            return 0.0;
        }

        // Penalty = Installment Amount * Daily Rate * Overdue Days
        return schedule.getInstallmentAmount() * PENALTY_RATE_PER_DAY * overdueDays;
    }
}
