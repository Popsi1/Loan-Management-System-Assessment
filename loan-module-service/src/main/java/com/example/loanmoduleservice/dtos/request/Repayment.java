package com.example.loanmoduleservice.dtos.request;

import lombok.Data;

@Data
public class Repayment {
    private Double amount;
    private BankDetails senderBankDetails;

    private BankDetails receiverBankDetails;
}
