package com.example.loanmoduleservice.service;


import com.example.loanmoduleservice.dtos.request.BankDetails;

public interface FundTransferService {

    public String transferFunds(BankDetails senderBankDetails, BankDetails receiverBankDetails, Double amount);
    public String transferFunds(BankDetails receiverBankDetails, Double amount);
}
