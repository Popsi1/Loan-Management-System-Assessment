package com.example.loanmodule.repository;

import com.example.loanmodule.entity.RepaymentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RepaymentScheduleRepository extends JpaRepository<RepaymentSchedule, Long> {
    List<RepaymentSchedule> findByLoanApplicationIdAndIsPaidFalse(Long loanApplicationId);
    List<RepaymentSchedule> findAllByDueDateBeforeAndIsPaid(LocalDate dueDate, Boolean isPaid);
}
