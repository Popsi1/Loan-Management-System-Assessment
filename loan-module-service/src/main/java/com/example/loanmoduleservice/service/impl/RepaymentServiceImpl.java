package com.example.loanmoduleservice.service.impl;

import com.example.loanmoduleservice.dtos.request.Repayment;
import com.example.loanmoduleservice.entity.LoanApplication;
import com.example.loanmoduleservice.entity.RepaymentSchedule;
import com.example.loanmoduleservice.enums.TransactionType;
import com.example.loanmoduleservice.exception.BadRequestException;
import com.example.loanmoduleservice.exception.ResourceNotFoundException;
import com.example.loanmoduleservice.repository.RepaymentScheduleRepository;
import com.example.loanmoduleservice.service.AuditService;
import com.example.loanmoduleservice.service.FundTransferService;
import com.example.loanmoduleservice.service.RepaymentService;
import com.example.loanmoduleservice.service.TransactionLogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private final AuditService auditService;

    private static final double PENALTY_RATE_PER_DAY = 0.02; // 2% daily penalty rate

    public List<RepaymentSchedule> generateRepaymentSchedule(LoanApplication loanApplication) {
        if (loanApplication.getLoanAmount().compareTo(BigDecimal.ZERO) <= 0 || loanApplication.getTenure() <= 0) {
            throw new BadRequestException("Loan amount and tenure must be positive values");
        }
        // Accuracy: Use BigDecimal for precise financial calculations
        BigDecimal monthlyInstallment = loanApplication.getLoanAmount().divide(
                new BigDecimal(loanApplication.getTenure()), 2, RoundingMode.HALF_UP); // Ensure two decimal places

        List<RepaymentSchedule> schedule = new ArrayList<>();

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
    public RepaymentSchedule rePayment(Long repaymentScheduleId, Repayment repayment, Long actinBy) throws JsonProcessingException {
        Optional<RepaymentSchedule> scheduleOpt = repaymentScheduleRepository.findById(repaymentScheduleId);

        if (scheduleOpt.isEmpty()) {
            throw new ResourceNotFoundException("Repayment schedule not found");
        }

        RepaymentSchedule schedule = scheduleOpt.get();

        if (schedule.getIsPaid()) {
            throw new BadRequestException("Payment already completed for this installment");
        }

        if (repayment.getAmount().compareTo(schedule.getInstallmentAmount()) < 0) {
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

        auditService.logAuditEvent(
                "Loan repaid: repaymentScheduleId=" + repaymentScheduleId,
                "LOAN_REPAYMENT",
                actinBy
        );

        transactionLogService.recordTransaction(schedule.getLoanApplicationId(),
                repayment.getSenderBankDetails(), repayment.getReceiverBankDetails(),
                repayment.getAmount(), transactionId, TransactionType.REPAY.name(), actinBy);

        return repaymentScheduleRepository.save(schedule);
    }

    /**
     * Calculate penalty for an overdue payment.
     *
     * @param schedule The overdue repayment schedule.
     * @return The calculated penalty.
     */
    public BigDecimal calculatePenalty(RepaymentSchedule schedule) {
        if (schedule.getIsPaid()) {
            return BigDecimal.ZERO; // No penalty if the payment is made
        }

        long overdueDays = ChronoUnit.DAYS.between(schedule.getDueDate(), LocalDate.now());
        if (overdueDays <= 0) {
            return BigDecimal.ZERO; // No penalty if the due date is not overdue
        }

        // Penalty = Installment Amount * Daily Rate * Overdue Days
        BigDecimal dailyPenaltyRate = new BigDecimal(PENALTY_RATE_PER_DAY); // Assuming PENALTY_RATE_PER_DAY is a constant
        BigDecimal penalty = schedule.getInstallmentAmount().multiply(dailyPenaltyRate).multiply(new BigDecimal(overdueDays));

        return penalty.setScale(2, RoundingMode.HALF_UP); // Ensure two decimal places for currency precision
    }

}
