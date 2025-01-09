package com.example.usermoduleservice.repository;

import com.example.usermoduleservice.entity.LoanUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<LoanUser, Long> {
    LoanUser findUserByEmail(String email);

    @Query("SELECT u FROM LoanUser u ORDER BY u.createdAt DESC")
    Page<LoanUser> allUsers(Pageable pageable);
}


