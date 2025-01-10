package com.example.loanmoduleservice.dtos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Repayment {
    @NotNull(message = "Loan amount is required")
    private BigDecimal amount;
    @NotNull(message = "Sender bankDetails is required")
    @Valid
    private BankDetails senderBankDetails;

    @NotNull(message = "Receiver bankDetails is required")
    @Valid
    private BankDetails receiverBankDetails;
}
