package com.example.loanmodule.controller;

import com.example.loanmodule.dtos.request.BankDetails;
import com.example.loanmodule.dtos.request.LoanApplicationRequest;
import com.example.loanmodule.dtos.request.Repayment;
import com.example.loanmodule.entity.RepaymentSchedule;
import com.example.loanmodule.repository.LoanApplicationRepository;
import com.example.loanmodule.service.LoanService;
import com.example.loanmodule.service.RepaymentService;
import com.example.loanmodule.service.RiskAssessmentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanApplicationRepository loanApplicationRepository;
    private final RiskAssessmentService riskAssessmentService;
    private final LoanService loanService;
    private final RepaymentService repaymentService;

    @PostMapping("/application")
    public ResponseEntity<?> applyForLoan(@RequestBody @Valid LoanApplicationRequest loanApplicationRequest) {
        return new ResponseEntity<>(loanService.applyForLoan(loanApplicationRequest), HttpStatus.CREATED);
    }

    @PatchMapping("/application/{loanApplicationId}")
    public ResponseEntity<?> getLoanApplication(@PathVariable Long loanApplicationId, @RequestParam String status) {
        return new ResponseEntity<>(loanService.updateLoanStatus(loanApplicationId, status), HttpStatus.OK);
    }

    @GetMapping("/application/{loanApplicationId}")
    public ResponseEntity<?> getLoanApplication(@PathVariable Long loanApplicationId) {
        return new ResponseEntity<>(loanService.getLoanApplication(loanApplicationId), HttpStatus.OK);
    }

    @PostMapping("/disburse/{loanApplicationId}")
    public ResponseEntity<?> disburseLoan(@PathVariable Long loanApplicationId, @RequestBody @Valid BankDetails bankDetails) throws JsonProcessingException {
        return new ResponseEntity<>(loanService.disburseLoan(loanApplicationId, bankDetails), HttpStatus.OK);
    }

    @PostMapping("/repayments/{repaymentScheduleId}")
    public ResponseEntity<?> rePayment(@PathVariable Long repaymentScheduleId, @RequestBody @Valid Repayment repayment) throws JsonProcessingException {
        RepaymentSchedule updatedSchedule = repaymentService.rePayment(repaymentScheduleId, repayment);
        return ResponseEntity.ok(updatedSchedule);
    }

}
