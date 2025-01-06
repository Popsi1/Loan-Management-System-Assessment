package com.example.loanmodule.service;

import com.example.loanmodule.dtos.request.BankDetails;

public interface FundTransferService {

    public String transferFunds(BankDetails senderBankDetails, BankDetails receiverBankDetails, Double amount);
    public String transferFunds(BankDetails receiverBankDetails, Double amount);
}
