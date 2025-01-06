package com.example.usermodule.repository;

import com.example.usermodule.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByEmail(String email);

    User findUserByIdAndDeleted(Long userId, boolean deleted);

    @Query("SELECT u FROM User u ORDER BY u.createdAt DESC")
    Page<User> allUsers(Pageable pageable);
}


