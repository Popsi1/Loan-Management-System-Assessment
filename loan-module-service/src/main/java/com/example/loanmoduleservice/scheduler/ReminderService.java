package com.example.loanmoduleservice.scheduler;

import com.example.loanmoduleservice.entity.RepaymentSchedule;
import com.example.loanmoduleservice.repository.RepaymentScheduleRepository;
import com.example.loanmoduleservice.service.NotificationService;
import com.example.loanmoduleservice.service.RepaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReminderService {

    private final RepaymentScheduleRepository repaymentScheduleRepository;
    private final NotificationService notificationService;
    private final RepaymentService repaymentService;

    /**
     * Sends reminders for due payments.
     * Scheduled to run daily.
     */
    @Scheduled(cron = "0 0 8 * * ?") // Every day at 8 AM
    public void sendPaymentReminders() {
        // Retrieve all overdue schedules
        List<RepaymentSchedule> overdueSchedules = repaymentScheduleRepository.findAllByDueDateBeforeAndIsPaid(LocalDate.now(), false);

        for (RepaymentSchedule schedule : overdueSchedules) {
            // Increment missed payment count
            schedule.setMissedPaymentCount(schedule.getMissedPaymentCount() + 1);

            // Calculate penalty and apply it
            BigDecimal penalty = repaymentService.calculatePenalty(schedule);
            BigDecimal updatedPenaltyAmount = schedule.getPenaltyAmount().add(penalty); // Ensures penalty is added correctly
            schedule.setPenaltyAmount(updatedPenaltyAmount);

            // Save the updated repayment schedule
            repaymentScheduleRepository.save(schedule);

            // Send payment reminder with penalty notification
            notificationService.sendMissedPaymentNotificationWithPenalty(schedule, penalty);

            // Optional: Log the penalty application for auditability
            logPenaltyApplication(schedule, penalty);
        }
    }

    private void logPenaltyApplication(RepaymentSchedule schedule, BigDecimal penalty) {
        // Log penalty application details for auditability
        log.info("Penalty of {} applied to RepaymentSchedule ID: {}, Missed Payment Count: {}, Total Penalty: {}",
                penalty, schedule.getId(), schedule.getMissedPaymentCount(), schedule.getPenaltyAmount());
    }


    // Scheduled job to identify and process delinquent payments daily
    @Scheduled(cron = "0 0 0 * * ?") // Runs at midnight daily
    public void processMissedPayments() {
        List<RepaymentSchedule> overdueSchedules = repaymentScheduleRepository.findAllByDueDateBeforeAndIsPaid(LocalDate.now(), false);

        for (RepaymentSchedule schedule : overdueSchedules) {
            // Increment missed payment count
            schedule.setMissedPaymentCount(schedule.getMissedPaymentCount() + 1);
            repaymentScheduleRepository.save(schedule);

            // Notify the borrower
            notificationService.sendMissedPaymentNotification(schedule);
        }
    }
}
