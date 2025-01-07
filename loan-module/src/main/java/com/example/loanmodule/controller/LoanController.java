package com.example.loanmodule.controller;

import com.example.loanmodule.dtos.request.BankDetails;
import com.example.loanmodule.dtos.request.LoanApplicationRequest;
import com.example.loanmodule.dtos.request.Repayment;
import com.example.loanmodule.entity.RepaymentSchedule;
import com.example.loanmodule.enums.RoleType;
import com.example.loanmodule.repository.LoanApplicationRepository;
import com.example.loanmodule.service.LoanService;
import com.example.loanmodule.service.RepaymentService;
import com.example.loanmodule.service.RiskAssessmentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@Tag(name = "Loan Route", description = "Loan Management API documentation")
public class LoanController {

    private final LoanApplicationRepository loanApplicationRepository;
    private final RiskAssessmentService riskAssessmentService;
    private final LoanService loanService;
    private final RepaymentService repaymentService;

    @PostMapping("/application")
    @Operation(summary = "Create a Loan Application")
    public ResponseEntity<?> applyForLoan(@RequestBody @Valid LoanApplicationRequest loanApplicationRequest
    , @RequestHeader("X-User-Role") String role, @RequestHeader("X-User-Id") String userId) {

        if (!RoleType.USER.name().equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }

        return new ResponseEntity<>(loanService.applyForLoan(loanApplicationRequest, Long.valueOf(userId)), HttpStatus.CREATED);
    }

    @PatchMapping("/application/{loanApplicationId}")
    @Operation(summary = "Update Loan Application Status")
    public ResponseEntity<?> updateLoanApplicationStatus(@PathVariable Long loanApplicationId, @RequestParam String status
            , @RequestHeader("X-User-Role") String role) {

        if (!RoleType.ADMIN.name().equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }
        return new ResponseEntity<>(loanService.updateLoanStatus(loanApplicationId, status), HttpStatus.OK);
    }

    @GetMapping("/application/{loanApplicationId}")
    @Operation(summary = "Get a Loan Application Details")
    public ResponseEntity<?> getLoanApplication(@PathVariable Long loanApplicationId
            , @RequestHeader("X-User-Role") String role) {

        if (!RoleType.ADMIN.name().equals(role) && !RoleType.USER.name().equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }
        return new ResponseEntity<>(loanService.getLoanApplication(loanApplicationId), HttpStatus.OK);
    }

    @PostMapping("/disburse/{loanApplicationId}")
    @Operation(summary = "Disburse amount to Loan User Account")
    public ResponseEntity<?> disburseLoan(@PathVariable Long loanApplicationId, @RequestBody @Valid BankDetails bankDetails
            , @RequestHeader("X-User-Role") String role)
            throws JsonProcessingException {

        if (!RoleType.ADMIN.name().equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }
        return new ResponseEntity<>(loanService.disburseLoan(loanApplicationId, bankDetails), HttpStatus.OK);
    }

    @PostMapping("/repayments/{repaymentScheduleId}")
    @Operation(summary = "Repayment of Loan Amount")
    public ResponseEntity<?> rePayment(@PathVariable Long repaymentScheduleId, @RequestBody @Valid Repayment repayment
            , @RequestHeader("X-User-Role") String role)
            throws JsonProcessingException {

        if (!RoleType.USER.name().equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }

        RepaymentSchedule updatedSchedule = repaymentService.rePayment(repaymentScheduleId, repayment);
        return ResponseEntity.ok(updatedSchedule);
    }

}
