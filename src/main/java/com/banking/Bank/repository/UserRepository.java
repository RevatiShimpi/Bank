package com.banking.Bank.repository;

import com.banking.Bank.entity.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByAccountNumber(String accnumber);
    User findByAccountNumber(String accnumber);
}
