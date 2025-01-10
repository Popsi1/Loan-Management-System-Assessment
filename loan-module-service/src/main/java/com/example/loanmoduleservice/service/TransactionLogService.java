package com.example.loanmoduleservice.service;

import com.example.loanmoduleservice.dtos.request.BankDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;

public interface TransactionLogService {
    public void recordTransaction(Long loanApplicationId, BankDetails payerBankDetails, BankDetails receiverBankDetails,
                                  BigDecimal amount, String transactionId, String transactionType, Long userId) throws JsonProcessingException;

    public void exportToExcel(HttpServletResponse response, String userId, String startDate, String endDate) throws IOException;
}
