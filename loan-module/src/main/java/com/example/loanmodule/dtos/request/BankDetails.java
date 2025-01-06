package com.example.loanmodule.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BankDetails {

    @NotBlank(message = "Bank name is required")
    private String bankName;

    @NotBlank(message = "Bank Account name is required")
    private String bankAccountName;

    @NotBlank(message = "Account Number is required")
    private String accountNumber;
}
