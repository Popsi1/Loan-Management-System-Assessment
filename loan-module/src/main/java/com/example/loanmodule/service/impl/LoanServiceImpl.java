package com.example.loanmodule.service.impl;

import com.example.loanmodule.dtos.request.BankDetails;
import com.example.loanmodule.dtos.request.LoanApplicationRequest;
import com.example.loanmodule.dtos.response.ApiDataResponseDto;
import com.example.loanmodule.entity.LoanApplication;
import com.example.loanmodule.entity.LoanDisbursement;
import com.example.loanmodule.enums.LoanApplicationStatus;
import com.example.loanmodule.enums.LoanDisbursementStatus;
import com.example.loanmodule.enums.TransactionType;
import com.example.loanmodule.helper.LoanHelper;
import com.example.loanmodule.repository.LoanApplicationRepository;
import com.example.loanmodule.repository.LoanDisbursementRepository;
import com.example.loanmodule.service.*;
import com.example.loanmodule.util.DataResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
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

    public ApiDataResponseDto applyForLoan(LoanApplicationRequest loanApplicationDto) {

        double incomeToLoanRatio = loanApplicationDto.getAnnualIncome() / loanApplicationDto.getLoanAmount();
        String risk = riskAssessmentService.assessRisk(incomeToLoanRatio);

        if ("HIGH".equals(risk)) {
            throw new RuntimeException("Loan application denied due to high risk.");
        }

        LoanApplication loanApplication = LoanHelper.buildLoanApplicationEntity(loanApplicationDto);

        loanApplication.setRiskLevel(risk);
        loanApplication.setInterestRate(calculateInterestRate(incomeToLoanRatio));
        loanApplication.setEmi(calculateEMI(loanApplication.getLoanAmount(), loanApplication.getInterestRate(), loanApplication.getTenure()));

        LoanApplication savedLoan = loanApplicationRepository.save(loanApplication);

        return DataResponse.successResponse("Loan Application created", LoanHelper.buildLoanApplicationResponseEntity(savedLoan));

    }

    public ApiDataResponseDto updateLoanStatus(Long loanApplicationId, String status) {
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
                .orElseThrow(() -> new IllegalArgumentException("Loan application not found"));

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
    
    public ApiDataResponseDto disburseLoan(Long loanApplicationId, BankDetails bankDetails) throws JsonProcessingException {
        LoanApplication loanApplication = loanApplicationRepository.findById(loanApplicationId)
                .orElseThrow(() -> new IllegalArgumentException("Loan application not found"));

        if (!"APPROVED".equals(loanApplication.getStatus())) {
            throw new IllegalStateException("Loan application must be approved for disbursement.");
        }

        LoanDisbursement disbursement = LoanHelper.buildLoanDisbursementEntity(loanApplication, bankDetails);

        String transactionId = null;
        try {
            // Simulate fund transfer
            transactionId = fundTransferService.transferFunds(bankDetails, loanApplication.getLoanAmount());
            disbursement.setTransactionId(transactionId);
            disbursement.setStatus(LoanDisbursementStatus.SUCCESS.name());
        } catch (Exception e) {
            disbursement.setTransactionId(transactionId);
            disbursement.setStatus(LoanDisbursementStatus.FAILED.name());
            throw new RuntimeException("Loan disbursement failed: " + e.getMessage(), e);
        }


        LoanDisbursement savedDisbursement = loanDisbursementRepository.save(disbursement);
        
        // Save transaction
        transactionLogService.recordTransaction(loanApplicationId, null, bankDetails,
                loanApplication.getLoanAmount(), transactionId, TransactionType.DISBURSE.name());

        repaymentService.generateRepaymentSchedule(loanApplication);

        // Notify user
        notificationService.notifyLoanDisbursement(
                loanApplication.getEmail(),
                loanApplication.getLoanAmount(),
                bankDetails.getAccountNumber()
        );

        return DataResponse.successResponse("Loan Application created",
                LoanHelper.buildLoanDisbursementResponseEntity(loanApplication, savedDisbursement));
    }

}
