package com.example.loanmodule.service.impl;

import com.example.loanmodule.dtos.request.BankDetails;
import com.example.loanmodule.entity.TransactionLog;
import com.example.loanmodule.repository.TransactionLogRepository;
import com.example.loanmodule.service.TransactionLogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionLogServiceImpl implements TransactionLogService {

    private final TransactionLogRepository transactionLogRepository;


    public void recordTransaction(Long loanApplicationId, BankDetails payerBankDetails, BankDetails receiverBankDetails,
                                  Double amount, String transactionId, String transactionType) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        String payerBankDetails1 = Objects.isNull(payerBankDetails) ? "" : objectMapper.writeValueAsString(payerBankDetails);
        String receiverBankDetails1 = Objects.isNull(receiverBankDetails) ? "" : objectMapper.writeValueAsString(receiverBankDetails);

        TransactionLog transLog = new TransactionLog();
        transLog.setTransactionId(transactionId);
        transLog.setTransactionType(transactionType);
        transLog.setLoanApplicationId(loanApplicationId);
        transLog.setPayerbankDetails(payerBankDetails1);
        transLog.setReceiverbankDetails(receiverBankDetails1);
        transLog.setAmount(amount);
        transactionLogRepository.save(transLog);

        log.info("Transaction recorded in accounting logs");
    }
}
