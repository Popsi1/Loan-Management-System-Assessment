package com.example.loanmodule.dtos.request;

import lombok.Data;

@Data
public class Repayment {
    private Double amount;
    private BankDetails senderBankDetails;

    private BankDetails receiverBankDetails;
}
