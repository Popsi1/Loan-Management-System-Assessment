package com.example.loanmoduleservice.controller;

import com.example.loanmoduleservice.dtos.request.BankDetails;
import com.example.loanmoduleservice.dtos.request.LoanApplicationRequest;
import com.example.loanmoduleservice.dtos.request.Repayment;
import com.example.loanmoduleservice.entity.RepaymentSchedule;
import com.example.loanmoduleservice.enums.RoleType;
import com.example.loanmoduleservice.repository.LoanApplicationRepository;
import com.example.loanmoduleservice.service.LoanService;
import com.example.loanmoduleservice.service.RepaymentService;
import com.example.loanmoduleservice.service.RiskAssessmentService;
import com.example.loanmoduleservice.service.TransactionLogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/loan")
@RequiredArgsConstructor
@Tag(name = "Loan Route", description = "Loan Management API documentation")
public class LoanController {

    private final LoanApplicationRepository loanApplicationRepository;
    private final RiskAssessmentService riskAssessmentService;
    private final LoanService loanService;
    private final RepaymentService repaymentService;
    private final TransactionLogService transactionLogService;

    @PostMapping("/application")
    @Operation(summary = "Create a Loan Application")
    public ResponseEntity<?> applyForLoan(@RequestBody @Valid LoanApplicationRequest loanApplicationRequest
    , @RequestHeader(value = "X-User-Role", required = false) String role, @RequestHeader(value = "X-User-Id", required = false) String userId) {

        // If no role or user ID is provided, return unauthorized or a forbidden status
        if (role == null || userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing required headers: X-User-Role or X-User-Id");
        }

        // Check if the role is USER
        if (!RoleType.USER.name().equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }

        return new ResponseEntity<>(loanService.applyForLoan(loanApplicationRequest, Long.valueOf(userId)), HttpStatus.CREATED);
    }

    @PatchMapping("/application/{loanApplicationId}")
    @Operation(summary = "Update Loan Application Status")
    public ResponseEntity<?> updateLoanApplicationStatus(@PathVariable Long loanApplicationId, @RequestParam String status
            , @RequestHeader(value = "X-User-Role", required = false) String role) {
        if (role == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing required headers: X-User-Role");
        }

        if (!RoleType.ADMIN.name().equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }
        return new ResponseEntity<>(loanService.updateLoanStatus(loanApplicationId, status), HttpStatus.OK);
    }

    @GetMapping("/application/{loanApplicationId}")
    @Operation(summary = "Get a Loan Application Details")
    public ResponseEntity<?> getLoanApplication(@PathVariable Long loanApplicationId
            , @RequestHeader(value = "X-User-Role", required = false) String role) {
        if (role == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing required headers: X-User-Role");
        }

        if (!(RoleType.ADMIN.name().equals(role) || RoleType.USER.name().equals(role))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }

        return new ResponseEntity<>(loanService.getLoanApplication(loanApplicationId), HttpStatus.OK);
    }

    //admin that disburse id is needed
    @PostMapping("/disburse/{loanApplicationId}")
    @Operation(summary = "Disburse amount to Loan User Account")
    public ResponseEntity<?> disburseLoan(@PathVariable Long loanApplicationId, @RequestBody @Valid BankDetails bankDetails
            , @RequestHeader(value = "X-User-Role", required = false) String role, @RequestHeader(value = "X-User-Id", required = false) String userId)
            throws JsonProcessingException {
        if (role == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing required headers: X-User-Role");
        }

        if (!RoleType.ADMIN.name().equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }
        return new ResponseEntity<>(loanService.disburseLoan(loanApplicationId, bankDetails), HttpStatus.OK);
    }

    @PostMapping("/repayments/{repaymentScheduleId}")
    @Operation(summary = "Repayment of Loan Amount")
    public ResponseEntity<?> rePayment(@PathVariable Long repaymentScheduleId, @RequestBody @Valid Repayment repayment
            , @RequestHeader(value = "X-User-Role", required = false) String role, @RequestHeader(value = "X-User-Id", required = false) String userId)
            throws JsonProcessingException {

        if (role == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing required headers: X-User-Role");
        }

        if (!RoleType.USER.name().equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }

        RepaymentSchedule updatedSchedule = repaymentService.rePayment(repaymentScheduleId, repayment, Long.valueOf(userId));
        return ResponseEntity.ok(updatedSchedule);
    }

    @GetMapping("/transactions/file")
    @Operation(summary = "Generate Transaction Statement")
    public void exportToExcel(HttpServletResponse response, @RequestHeader(value = "X-User-Id", required = false) String userId,
                              @RequestParam String startDate, @RequestParam String endDate) throws IOException {
        this.transactionLogService.exportToExcel(response, userId, startDate, endDate);
    }

}
