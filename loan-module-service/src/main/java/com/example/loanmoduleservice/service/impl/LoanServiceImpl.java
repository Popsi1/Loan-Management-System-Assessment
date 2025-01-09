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
import org.apache.commons.lang3.EnumUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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


    private static final double BASE_INTEREST_RATE = 5.0;

    public ApiDataResponseDto applyForLoan(LoanApplicationRequest loanApplicationDto, Long userId) {

        double incomeToLoanRatio = loanApplicationDto.getAnnualIncome() / loanApplicationDto.getLoanAmount();
        String risk = riskAssessmentService.assessRisk(incomeToLoanRatio);

        if ("HIGH".equals(risk)) {
            throw new RuntimeException("Loan application denied due to high risk.");
        }

        LoanApplication loanApplication = LoanHelper.buildLoanApplicationEntity(loanApplicationDto, userId);

        loanApplication.setRiskLevel(risk);
        loanApplication.setInterestRate(calculateInterestRate(incomeToLoanRatio));
        loanApplication.setEmi(calculateEMI(loanApplication.getLoanAmount(), loanApplication.getInterestRate(), loanApplication.getTenure()));

        LoanApplication savedLoan = loanApplicationRepository.save(loanApplication);

        return DataResponse.successResponse("Loan Application created", LoanHelper.buildLoanApplicationResponseEntity(savedLoan));

    }

    public ApiDataResponseDto updateLoanStatus(Long loanApplicationId, String status) {

        if (!EnumUtils.isValidEnum(LoanApplicationStatus.class, status))
            throw new BadRequestException("Invalid Status");

        LoanApplication loanApplication = loanApplicationRepository.findById(loanApplicationId)
                .orElseThrow(() -> new IllegalArgumentException("Loan application not found"));

        if (status.equals(LoanApplicationStatus.REJECTED.name())){
            loanApplication.setStatus(LoanApplicationStatus.REJECTED.name());
            notificationService.notifyLoanRejection(loanApplication.getEmail());
        } else if (status.equals(LoanApplicationStatus.APPROVED.name())) {
            loanApplication.setStatus(LoanApplicationStatus.APPROVED.name());
            notificationService.notifyLoanApproval(loanApplication.getEmail(), loanApplication.getLoanAmount(),
                    loanApplication.getEmi(),loanApplication.getTenure());
        } else if (status.equals(LoanApplicationStatus.REPAID.name())) {
            loanApplication.setStatus(LoanApplicationStatus.REPAID.name());
            notificationService.notifyLoanRepaid(loanApplication.getEmail(), loanApplication.getLoanAmount(),
                    loanApplication.getEmi(),loanApplication.getTenure());
        }else {
            loanApplication.setStatus(LoanApplicationStatus.PENDING.name());
        }
        LoanApplication savedLoan = loanApplicationRepository.save(loanApplication);

        return DataResponse.successResponse("Loan Application created", LoanHelper.buildLoanApplicationResponseEntity(savedLoan));

    }

    public ApiDataResponseDto getLoanApplication(Long loanApplicationId) {
        LoanApplication loanApplication = loanApplicationRepository.findById(loanApplicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan application details not found"));

        return DataResponse.successResponse("Loan Application created", LoanHelper.buildLoanApplicationResponseEntity(loanApplication));

    }

    private double calculateInterestRate(double incomeToLoanRatio) {
        return BASE_INTEREST_RATE + (incomeToLoanRatio < 3 ? 1.5 : 0.5);
    }

    private double calculateEMI(double loanAmount, double annualInterestRate, int tenureInMonths) {
        double monthlyRate = (annualInterestRate / 100) / 12;
        return (loanAmount * monthlyRate * Math.pow(1 + monthlyRate, tenureInMonths)) /
                (Math.pow(1 + monthlyRate, tenureInMonths) - 1);
    }

    @Transactional
    public ApiDataResponseDto disburseLoan(Long loanApplicationId, BankDetails bankDetails) throws JsonProcessingException {
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

        executeAsyncTasks(loanApplication, bankDetails, savedDisbursement);

        return DataResponse.successResponse("Loan Application created",
                LoanHelper.buildLoanDisbursementResponseEntity(loanApplication, savedDisbursement));
    }

    private void validateLoanApplicationForDisbursement(LoanApplication loanApplication) {
        if (!LoanApplicationStatus.APPROVED.name().equals(loanApplication.getStatus())) {
            throw new BadRequestException("Loan application must be approved for disbursement.");
        }

        if (!"APPROVED".equals(loanApplication.getStatus())) {
            throw new BadRequestException("Loan application must be approved for disbursement.");
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
