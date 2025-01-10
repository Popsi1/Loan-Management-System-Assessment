package com.example.loanmoduleservice.service;


import com.example.loanmoduleservice.dtos.request.BankDetails;

import java.math.BigDecimal;

public interface FundTransferService {

    public String transferFunds(BankDetails senderBankDetails, BankDetails receiverBankDetails, BigDecimal amount);
    public String transferFunds(BankDetails receiverBankDetails, BigDecimal amount);
}
