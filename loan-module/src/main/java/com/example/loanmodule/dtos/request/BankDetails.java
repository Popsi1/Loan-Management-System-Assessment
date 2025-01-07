package com.example.loanmodule.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BankDetails {

    @NotBlank(message = "Bank name is required")
    @Size(max = 50, message = "Bank name must not exceed 50 characters")
    private String bankName;

    @NotBlank(message = "Bank Account name is required")
    @Size(max = 100, message = "Bank Account name must not exceed 100 characters")
    private String bankAccountName;

    @NotBlank(message = "Account Number is required")
    @Pattern(regexp = "\\d{10}", message = "Account Number must be exactly 10 digits")
    private String accountNumber;
}
