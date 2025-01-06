package com.example.loanmodule.service;

import com.example.loanmodule.dtos.request.BankDetails;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface TransactionLogService {
    public void recordTransaction(Long loanApplicationId, BankDetails payerBankDetails, BankDetails receiverBankDetails,
                                  Double amount, String transactionId, String transactionType) throws JsonProcessingException;
}
