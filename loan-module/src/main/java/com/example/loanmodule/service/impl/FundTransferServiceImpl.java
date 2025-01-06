package com.example.loanmodule.service.impl;

import com.example.loanmodule.dtos.request.BankDetails;
import com.example.loanmodule.service.FundTransferService;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
public class FundTransferServiceImpl implements FundTransferService {
    public String transferFunds(BankDetails senderBankDetails, BankDetails receiverBankDetails, Double amount) {
        // Simulate fund transfer via banking API
        System.out.printf("Transferring %.2f to account: %s\n", amount, receiverBankDetails.getAccountNumber());

        // Return a dummy transaction ID
        return UUID.randomUUID().toString();
    }

    public String transferFunds(BankDetails receiverBankDetails, Double amount){
        // Simulate fund transfer via banking API
        System.out.printf("Transferring %.2f to account: %s\n", amount, receiverBankDetails.getAccountNumber());

        // Return a dummy transaction ID
        return UUID.randomUUID().toString();
    }
}
