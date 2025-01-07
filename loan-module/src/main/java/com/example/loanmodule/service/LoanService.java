package com.example.loanmodule.service;

import com.example.loanmodule.dtos.request.BankDetails;
import com.example.loanmodule.dtos.request.LoanApplicationRequest;
import com.example.loanmodule.dtos.response.ApiDataResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface LoanService {
    public ApiDataResponseDto applyForLoan(LoanApplicationRequest loanApplicationDto, Long userId);
    public ApiDataResponseDto updateLoanStatus(Long loanApplicationId, String status);
    public ApiDataResponseDto getLoanApplication(Long loanApplicationId);

    public ApiDataResponseDto disburseLoan(Long loanApplicationId, BankDetails bankDetails) throws JsonProcessingException;
}
