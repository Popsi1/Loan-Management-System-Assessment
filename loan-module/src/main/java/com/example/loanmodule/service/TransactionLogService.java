package com.example.loanmodule.service;

import com.example.loanmodule.dtos.request.BankDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface TransactionLogService {
    public void recordTransaction(Long loanApplicationId, BankDetails payerBankDetails, BankDetails receiverBankDetails,
                                  Double amount, String transactionId, String transactionType, Long userId) throws JsonProcessingException;

    public void exportToExcel(HttpServletResponse response, String userId, String startDate, String endDate) throws IOException;
}
