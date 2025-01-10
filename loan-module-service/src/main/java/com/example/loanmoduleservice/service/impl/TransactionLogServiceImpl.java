package com.example.loanmoduleservice.service.impl;

import com.example.loanmoduleservice.dtos.request.BankDetails;
import com.example.loanmoduleservice.entity.TransactionLog;
import com.example.loanmoduleservice.repository.TransactionLogRepository;
import com.example.loanmoduleservice.service.TransactionLogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionLogServiceImpl implements TransactionLogService {

    private final TransactionLogRepository transactionLogRepository;
    private final UserExportToExcelService userExportToExcelService;


    public void recordTransaction(Long loanApplicationId, BankDetails payerBankDetails, BankDetails receiverBankDetails,
                                  BigDecimal amount, String transactionId, String transactionType, Long userId) throws JsonProcessingException {
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
        transLog.setUserId(userId);
        transLog.setTimestamp(LocalDateTime.now());
        transactionLogRepository.save(transLog);

        log.info("Transaction recorded in accounting logs");
    }
    public void exportToExcel(HttpServletResponse response, String userId, String startDate, String endDate) throws IOException {

        List<TransactionLog> data = transactionLogRepository.findByUserIdAndTimestampBetween(Long.parseLong(userId),
                LocalDate.parse(startDate).atStartOfDay(), LocalDate.parse(endDate).atTime(LocalTime.MAX));

        // export to pdf
        userExportToExcelService.exportToExcel(response, data);

    }
}
