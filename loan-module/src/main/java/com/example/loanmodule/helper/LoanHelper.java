package com.example.loanmodule.helper;

import com.example.loanmodule.dtos.request.BankDetails;
import com.example.loanmodule.dtos.request.LoanApplicationRequest;
import com.example.loanmodule.dtos.response.LoanApplicationResponse;
import com.example.loanmodule.dtos.response.LoanDisbursementResponse;
import com.example.loanmodule.entity.LoanApplication;
import com.example.loanmodule.entity.LoanDisbursement;
import com.example.loanmodule.enums.LoanApplicationStatus;
import com.example.loanmodule.enums.LoanDisbursementStatus;

import java.time.LocalDateTime;

public class LoanHelper {
    public static LoanApplication buildLoanApplicationEntity(LoanApplicationRequest loanApplicationRequest, Long userId){
        return LoanApplication.builder()
                .email(loanApplicationRequest.getEmail())
                .fullName(loanApplicationRequest.getFullName())
                .annualIncome(loanApplicationRequest.getAnnualIncome())
                .loanAmount(loanApplicationRequest.getLoanAmount())
                .tenure(loanApplicationRequest.getTenure())
                .status(LoanApplicationStatus.PENDING.name())
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static LoanDisbursement buildLoanDisbursementEntity(LoanApplication loanApplication, BankDetails bankDetails){
        return LoanDisbursement.builder()
                .loanApplicationId(loanApplication.getId())
                .accountNumber(bankDetails.getAccountNumber())
                .disbursedAmount(loanApplication.getLoanAmount())
                .status(LoanDisbursementStatus.PENDING.name())
                .disbursementDate(LocalDateTime.now())
                .build();
    }
    public static LoanDisbursementResponse buildLoanDisbursementResponseEntity(LoanApplication loanApplication, LoanDisbursement loanDisbursement){
        return LoanDisbursementResponse.builder()
                .id(loanDisbursement.getId())
                .accountNumber(loanDisbursement.getAccountNumber())
                .transactionId(loanDisbursement.getTransactionId())
                .email(loanApplication.getEmail())
                .fullName(loanApplication.getFullName())
                .disbursedAmount(loanDisbursement.getDisbursedAmount())
                .disbursementDate(loanDisbursement.getDisbursementDate())
                .status(loanDisbursement.getStatus())
                .build();
    }

    public static LoanApplicationResponse buildLoanApplicationResponseEntity(LoanApplication loanApplication){
        return LoanApplicationResponse.builder()
                .id(loanApplication.getId())
                .userId(loanApplication.getUserId())
                .annualIncome(loanApplication.getAnnualIncome())
                .loanAmount(loanApplication.getLoanAmount())
                .emi(loanApplication.getEmi())
                .tenure(loanApplication.getTenure())
                .status(loanApplication.getStatus())
                .fullName(loanApplication.getFullName())
                .interestRate(loanApplication.getInterestRate())
                .riskLevel(loanApplication.getRiskLevel())
                .createdAt(loanApplication.getCreatedAt().toString())
                .email(loanApplication.getEmail())
                .build();
    }
}
