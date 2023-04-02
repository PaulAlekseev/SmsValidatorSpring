package com.example.SmsValidator.repository;

import com.example.SmsValidator.entity.ModemEntity;
import com.example.SmsValidator.entity.ModemProviderSessionEntity;
import com.example.SmsValidator.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface TaskEntityRepository extends CrudRepository<TaskEntity, Long>, JpaSpecificationExecutor<TaskEntity> {
    @Transactional
    @Modifying
    @Query("update TaskEntity t set t.success = ?1, t.done = ?2 where t.id = ?3")
    int updateSuccessAndDoneById(boolean success, boolean done, Long id);

    boolean existsByIdAndReservedTrue(Long id);

    @Transactional
    @Modifying
    @Query("update TaskEntity t set t.done = ?1 where t.id = ?2 and t.modemProviderSessionEntity = ?3")
    int updateDoneByIdAndModemProviderSessionEntity(boolean done, Long id, ModemProviderSessionEntity modemProviderSessionEntity);

    @Query("""
            select (count(t) > 0) from TaskEntity t
            where t.id = ?1 and t.modemEntity.IMSI = ?2 and t.modemEntity.ICCID = ?3""")
    boolean existsByIdAndModemEntity_IMSIAndModemEntity_ICCID(Long id, String IMSI, String ICCID);

    @Transactional
    @Modifying
    @Query("update TaskEntity t set t.modemEntity = ?1, t.modemProviderSessionEntity = ?2 where t.id = ?3")
    int updateModemEntityAndModemProviderSessionEntityById(ModemEntity modemEntity, ModemProviderSessionEntity modemProviderSessionEntity, Long id);

    @Transactional
    @Modifying
    @Query("update TaskEntity t set t.ready = ?1 where t.id = ?2")
    int updateReadyById(boolean ready, Long id);

    @Override
    Optional<TaskEntity> findById(Long aLong);
}