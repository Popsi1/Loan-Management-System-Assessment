package com.example.loanmoduleservice.scheduler;

import com.example.loanmoduleservice.entity.RepaymentSchedule;
import com.example.loanmoduleservice.repository.RepaymentScheduleRepository;
import com.example.loanmoduleservice.service.NotificationService;
import com.example.loanmoduleservice.service.RepaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
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
        List<RepaymentSchedule> overdueSchedules = repaymentScheduleRepository.findAllByDueDateBeforeAndIsPaid(LocalDate.now(), false);

        for (RepaymentSchedule schedule : overdueSchedules) {
            schedule.setMissedPaymentCount(schedule.getMissedPaymentCount() + 1);

            // Calculate and apply penalty
            Double penalty = repaymentService.calculatePenalty(schedule);
            schedule.setPenaltyAmount(schedule.getPenaltyAmount() + penalty);

            repaymentScheduleRepository.save(schedule);

            // Notify borrower
            notificationService.sendMissedPaymentNotificationWithPenalty(schedule, penalty);
        }
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
