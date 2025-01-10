package com.example.loanmoduleservice.service.impl;

import com.example.loanmoduleservice.dtos.request.BankDetails;
import com.example.loanmoduleservice.service.FundTransferService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;
@Service
public class FundTransferServiceImpl implements FundTransferService {
    public String transferFunds(BankDetails senderBankDetails, BankDetails receiverBankDetails, BigDecimal amount) {
        // Simulate fund transfer via banking API
        System.out.printf("Transferring %.2f to account: %s\n", amount, receiverBankDetails.getAccountNumber());

        // Return a dummy transaction ID
        return UUID.randomUUID().toString();
    }

    public String transferFunds(BankDetails receiverBankDetails, BigDecimal amount){
        // Simulate fund transfer via banking API
        System.out.printf("Transferring %.2f to account: %s\n", amount, receiverBankDetails.getAccountNumber());

        // Return a dummy transaction ID
        return UUID.randomUUID().toString();
    }
}
