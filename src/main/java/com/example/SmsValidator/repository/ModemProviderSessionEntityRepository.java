package com.example.SmsValidator.repository;

import com.example.SmsValidator.entity.ModemProviderSessionEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ModemProviderSessionEntityRepository extends CrudRepository<ModemProviderSessionEntity, Long> {
    @Transactional
    @Modifying
    @Query("update ModemProviderSessionEntity m set m.busy = ?1 where m.active = true and m.socketId = ?2")
    int updateBusyByActiveTrueAndSocketId(Boolean busy, String socketId);

    ModemProviderSessionEntity findByModemEntityList_Id(Long id);

    @Transactional
    @Modifying
    @Query("update ModemProviderSessionEntity m set m.active = ?1 where m.socketId = ?2 and m.active = true")
    int updateActiveBySocketIdAndActiveTrue(Boolean active, String socketId);

    @Query("select (count(m) > 0) from ModemProviderSessionEntity m where m.socketId = ?1 and m.active = true")
    boolean existsBySocketIdAndActiveTrue(String socketId);

    @Transactional
    @Modifying
    @Query("update ModemProviderSessionEntity m set m.busy = ?1 where m.socketId = ?2 and m.active = true")
    int updateBusyBySocketIdAndActiveTrue(Boolean busy, String socketId);

    Optional<ModemProviderSessionEntity> findBySocketIdAndActiveTrue(String socketId);

    @Transactional
    Optional<ModemProviderSessionEntity> findByUser_EmailAndActiveTrue(String name);
}