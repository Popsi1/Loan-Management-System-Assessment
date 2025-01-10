package com.example.loanmoduleservice.service.impl;

import com.example.loanmoduleservice.dtos.request.BankDetails;
import com.example.loanmoduleservice.dtos.request.LoanApplicationRequest;
import com.example.loanmoduleservice.dtos.response.ApiDataResponseDto;
import com.example.loanmoduleservice.entity.LoanApplication;
import com.example.loanmoduleservice.entity.LoanDisbursement;
import com.example.loanmoduleservice.enums.LoanApplicationStatus;
import com.example.loanmoduleservice.enums.LoanDisbursementStatus;
import com.example.loanmoduleservice.enums.TransactionType;
import com.example.loanmoduleservice.exception.BadRequestException;
import com.example.loanmoduleservice.exception.ResourceNotFoundException;
import com.example.loanmoduleservice.helper.LoanHelper;
import com.example.loanmoduleservice.repository.LoanApplicationRepository;
import com.example.loanmoduleservice.repository.LoanDisbursementRepository;
import com.example.loanmoduleservice.service.*;
import com.example.loanmoduleservice.util.DataResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
//import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {
    private final LoanApplicationRepository loanApplicationRepository;
    private final RiskAssessmentService riskAssessmentService;
    private final FundTransferService fundTransferService;
    private final LoanDisbursementRepository loanDisbursementRepository;
    private final NotificationService notificationService;
    private final TransactionLogService transactionLogService;
    private final RepaymentService repaymentService;
    private final AuditService auditService;


    private static final BigDecimal BASE_INTEREST_RATE = BigDecimal.valueOf(5.0);

    public ApiDataResponseDto applyForLoan(LoanApplicationRequest loanApplicationRequest, Long userId) {

        // Calculate Income-to-Loan Ratio
        BigDecimal incomeToLoanRatio = loanApplicationRequest.getAnnualIncome()
                .divide(loanApplicationRequest.getLoanAmount(), RoundingMode.HALF_UP);

        // Assess Risk
        String risk = riskAssessmentService.assessRisk(incomeToLoanRatio);

        // Deny loan application if risk is high
        if ("HIGH".equals(risk)) {
            auditService.logAuditEvent("Loan application denied for userId: " + userId, "HIGH_RISK", userId);
            throw new BadRequestException("Loan application denied due to high risk.");
        }

        // Build Loan Application Entity
        LoanApplication loanApplication = LoanHelper.buildLoanApplicationEntity(loanApplicationRequest, userId);

        loanApplication.setRiskLevel(risk);
        loanApplication.setInterestRate(calculateInterestRate(incomeToLoanRatio));

        loanApplication.setEmi(calculateEMI(
                loanApplication.getLoanAmount(),
                loanApplication.getInterestRate(),
                loanApplication.getTenure()
        ));

        // Save Loan Application with Audit Logging
        LoanApplication savedLoan = loanApplicationRepository.save(loanApplication);
        auditService.logAuditEvent("Loan application created for userId: " + userId, "APPLICATION_CREATED", userId);

        return DataResponse.successResponse(
                "Loan Application created successfully",
                LoanHelper.buildLoanApplicationResponseEntity(savedLoan)
        );
    }

    public ApiDataResponseDto updateLoanStatus(Long loanApplicationId, String status, Long userId) {
        if (!EnumUtils.isValidEnum(LoanApplicationStatus.class, status)) {
            throw new BadRequestException("Invalid Status");
        }

        LoanApplication loanApplication = loanApplicationRepository.findById(loanApplicationId)
                .orElseThrow(() -> new IllegalArgumentException("Loan application not found"));

        String oldStatus = loanApplication.getStatus();
        loanApplication.setStatus(status);

        switch (LoanApplicationStatus.valueOf(status)) {
            case REJECTED:
                notificationService.notifyLoanRejection(loanApplication.getEmail());
                auditService.logAuditEvent(
                        "Loan application rejected for userId: " + loanApplication.getUserId(),
                        "APPLICATION_REJECTED",
                        userId
                );
                break;
            case APPROVED:
                notificationService.notifyLoanApproval(
                        loanApplication.getEmail(),
                        loanApplication.getLoanAmount(),
                        loanApplication.getEmi(),
                        loanApplication.getTenure()
                );
                auditService.logAuditEvent(
                        "Loan application approved for userId: " + loanApplication.getUserId(),
                        "APPLICATION_APPROVED",
                        userId
                );
                break;
            case REPAID:
                notificationService.notifyLoanRepaid(
                        loanApplication.getEmail(),
                        loanApplication.getLoanAmount(),
                        loanApplication.getEmi(),
                        loanApplication.getTenure()
                );
                auditService.logAuditEvent(
                        "Loan application marked as repaid for userId: " + loanApplication.getUserId(),
                        "APPLICATION_REPAID",
                        userId
                );
                break;
            default:
                auditService.logAuditEvent(
                        "Loan application status changed to pending for userId: " + loanApplication.getUserId(),
                        "APPLICATION_PENDING",
                        userId
                );
        }

        LoanApplication savedLoan = loanApplicationRepository.save(loanApplication);

        auditService.logAuditEvent(
                "Loan application status updated from " + oldStatus + " to " + status + " for userId: " + loanApplication.getUserId(),
                "STATUS_UPDATE",
                userId
        );

        return DataResponse.successResponse(
                "Loan Application status updated",
                LoanHelper.buildLoanApplicationResponseEntity(savedLoan)
        );
    }

    public ApiDataResponseDto getLoanApplication(Long loanApplicationId) {
        LoanApplication loanApplication = loanApplicationRepository.findById(loanApplicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan application details not found"));

        return DataResponse.successResponse("Loan Application created", LoanHelper.buildLoanApplicationResponseEntity(loanApplication));

    }

    /**
     * Calculates the interest rate based on the income-to-loan ratio.
     *
     * @param incomeToLoanRatio the ratio of annual income to loan amount
     * @return the calculated interest rate as a BigDecimal
     */
    private BigDecimal calculateInterestRate(BigDecimal incomeToLoanRatio) {
        if (incomeToLoanRatio.compareTo(BigDecimal.valueOf(3)) >= 0) {
            return BASE_INTEREST_RATE.add(BigDecimal.valueOf(0.0)); // No additional risk increment for low risk
        } else if (incomeToLoanRatio.compareTo(BigDecimal.valueOf(1.5)) >= 0) {
            return BASE_INTEREST_RATE.add(BigDecimal.valueOf(5.0)); // Medium risk increment
        } else {
            return BASE_INTEREST_RATE.add(BigDecimal.valueOf(10.0)); // High risk increment
        }
    }


    /**
     * Calculates the Equated Monthly Installment (EMI) for a loan.
     *
     * @param loanAmount  the principal loan amount
     * @param tenureInMonths  the annual interest rate as a percentage (e.g., 5.0 for 5%)
     * @param annualInterestRate the tenure of the loan in years
     * @return the EMI as a BigDecimal
     */
    public BigDecimal calculateEMI(BigDecimal loanAmount, BigDecimal annualInterestRate, int tenureInMonths) {
        // Convert the annual interest rate into monthly rate
        BigDecimal monthlyRate = annualInterestRate.divide(BigDecimal.valueOf(100), MathContext.DECIMAL128)
                .divide(BigDecimal.valueOf(12), MathContext.DECIMAL128);

        // EMI formula
        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);
        BigDecimal power = onePlusRate.pow(tenureInMonths);

        // EMI = [loanAmount * monthlyRate * (1 + monthlyRate)^tenure] / [(1 + monthlyRate)^tenure - 1]
        BigDecimal numerator = loanAmount.multiply(monthlyRate).multiply(power);
        BigDecimal denominator = power.subtract(BigDecimal.ONE);

        // Final EMI result
        BigDecimal emi = numerator.divide(denominator, 2, RoundingMode.HALF_UP); // Rounding to 2 decimal places for accuracy

        return emi;
    }




    @Transactional
    public ApiDataResponseDto disburseLoan(Long loanApplicationId, BankDetails bankDetails, Long actinBy) throws JsonProcessingException {
        LoanApplication loanApplication = loanApplicationRepository.findById(loanApplicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan application not found"));

        validateLoanApplicationForDisbursement(loanApplication);

        LoanDisbursement disbursement = LoanHelper.buildLoanDisbursementEntity(loanApplication, bankDetails);

        String transactionId = null;
        try {
            // Attempt fund transfer and update disbursement details
            transactionId = processFundTransfer(bankDetails, loanApplication, disbursement);
        } catch (Exception e) {
            handleFundTransferFailure(disbursement, e);
        }


        LoanDisbursement savedDisbursement = loanDisbursementRepository.save(disbursement);

        loanApplication.setAccountDisburse(true);
        loanApplicationRepository.save(loanApplication);

        executeAsyncTasks(loanApplication, bankDetails, savedDisbursement);

        auditService.logAuditEvent(
                "Loan disbursed: loanApplicationId=" + loanApplicationId + ", userId=" + loanApplication.getUserId(),
                "LOAN_DISBURSEMENT",
                actinBy
        );

        return DataResponse.successResponse("Loan Application created",
                LoanHelper.buildLoanDisbursementResponseEntity(loanApplication, savedDisbursement));
    }

    private void validateLoanApplicationForDisbursement(LoanApplication loanApplication) {
        if (!LoanApplicationStatus.APPROVED.name().equals(loanApplication.getStatus())) {
            throw new BadRequestException("Loan application must be approved for disbursement.");
        }
        if (BooleanUtils.isTrue(loanApplication.isAccountDisburse())) {
            throw new BadRequestException("Loan application is already disbursed.");
        }
    }

    private String processFundTransfer(BankDetails bankDetails, LoanApplication loanApplication, LoanDisbursement disbursement) {
        String transactionId = fundTransferService.transferFunds(bankDetails, loanApplication.getLoanAmount());
        disbursement.setTransactionId(transactionId);
        disbursement.setStatus(LoanDisbursementStatus.SUCCESS.name());
        return transactionId;
    }

    private void handleFundTransferFailure(LoanDisbursement disbursement, Exception e) {
        disbursement.setTransactionId(null);
        disbursement.setStatus(LoanDisbursementStatus.FAILED.name());
        throw new RuntimeException("Loan disbursement failed: " + e.getMessage(), e);
    }

    @Async
    public void executeAsyncTasks(LoanApplication loanApplication, BankDetails bankDetails, LoanDisbursement disbursement) throws JsonProcessingException {
        // Record transaction log
        transactionLogService.recordTransaction(
                loanApplication.getId(),
                null,
                bankDetails,
                loanApplication.getLoanAmount(),
                disbursement.getTransactionId(),
                TransactionType.DISBURSE.name(),
                loanApplication.getUserId()
        );

        // Generate repayment schedule
        repaymentService.generateRepaymentSchedule(loanApplication);

        // Notify user
        notificationService.notifyLoanDisbursement(
                loanApplication.getEmail(),
                loanApplication.getLoanAmount(),
                bankDetails.getAccountNumber()
        );
    }
}
