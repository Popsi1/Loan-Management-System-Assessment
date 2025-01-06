package com.example.loanmodule.service;

import com.example.loanmodule.entity.RepaymentSchedule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    public void sendNotification(String email, String subject, String message) {
        // implement mail sender
        System.out.printf("Sending email to %s:\nSubject: %s\nMessage: %s\n", email, subject, message);
    }

    public void notifyLoanApproval(String email, double loanAmount, double emi, int tenure) {
        String subject = "Loan Approved";
        String message = String.format(
                "Congratulations! Your loan of %.2f has been approved. Your EMI is %.2f for %d months.",
                loanAmount, emi, tenure
        );
        sendNotification(email, subject, message);
    }

    public void notifyLoanRepaid(String email, double loanAmount, double emi, int tenure) {
        String subject = "Loan Approved";
        String message = String.format(
                "Congratulations! Your loan of %.2f has been repaid. Your EMI is %.2f for %d months.",
                loanAmount, emi, tenure
        );
        sendNotification(email, subject, message);
    }
    public void notifyLoanRejection(String email) {
        String subject = "Loan Rejected";
        String message = "We regret to inform you that your loan application has been rejected.";
        sendNotification(email, subject, message);
    }

    public void notifyLoanDisbursement(String email, Double amount, String accountNumber) {
        String subject = "Loan Disbursed";
        String message = String.format("Your loan of %.2f has been disbursed to account %s.", amount, accountNumber);
        sendNotification(email, subject, message);
    }

    public void sendMissedPaymentNotification(RepaymentSchedule schedule) {
        // Placeholder for sending email/SMS
        System.out.printf("Reminder: Your payment of %.2f due on %s is overdue.%n",
                schedule.getInstallmentAmount(), schedule.getDueDate());
    }

    public void sendMissedPaymentNotificationWithPenalty(RepaymentSchedule schedule, Double penalty) {
        System.out.printf("Reminder: Your payment of %.2f due on %s is overdue.%nPenalty incurred: %.2f.%n",
                schedule.getInstallmentAmount(), schedule.getDueDate(), penalty);
    }

}
