package com.example.SmsValidator.repository;

import com.example.SmsValidator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Transactional
    @Modifying
    @Query("update User u set u.password = ?1, u.updated = ?2 where u.id = ?3")
    int updatePasswordAndUpdatedById(String password, Date updated, Long id);

    Optional<User> findByEmailAndEnabledTrue(String email);

    Optional<User> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE User SET balance = balance + :amount WHERE id = :id")
    int topUpBalance(long id, Float amount);

    @Transactional
    @Modifying
    @Query("UPDATE User SET balance = balance - :amount WHERE id = :id")
    int decreaseBalance(long id, Float amount);
}
