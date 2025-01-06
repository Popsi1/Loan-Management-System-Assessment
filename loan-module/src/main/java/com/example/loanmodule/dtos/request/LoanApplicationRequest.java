package com.example.loanmodule.dtos.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class LoanApplicationRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @NotNull(message = "Loan amount is required")
    @Min(value = 1000, message = "Loan amount must be at least 1000")
    private Double loanAmount;

    @NotNull(message = "Tenure is required")
    @Min(value = 6, message = "Minimum tenure is 6 months")
    @Max(value = 60, message = "Maximum tenure is 60 months")
    private Integer tenure;

    @NotNull(message = "Annual income is required")
    @Min(value = 5000, message = "Annual income must be at least 5000")
    private Double annualIncome;

}

