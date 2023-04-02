package com.example.SmsValidator.repository;

import com.example.SmsValidator.entity.ModemEntity;
import com.example.SmsValidator.entity.ModemProviderSessionEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ModemEntityRepository extends CrudRepository<ModemEntity, Long>, JpaSpecificationExecutor<ModemEntity> {
    @Transactional
    @Modifying
    @Query("""
            update ModemEntity m set m.modemProviderSessionEntity = ?1, m.busy = ?2
            where m.phoneNumber in ?3 and m.modemProviderSessionEntity is null""")
    int updateModemProviderSessionEntityAndBusyByPhoneNumberInAndModemProviderSessionEntityNull(ModemProviderSessionEntity modemProviderSessionEntity, Boolean busy, Collection<String> phoneNumbers);

    @Transactional
    @Modifying
    @Query("update ModemEntity m set m.modemProviderSessionEntity = ?1, m.busy = ?2 where m.IMSI = ?3 and m.ICCID = ?4")
    int updateModemProviderSessionEntityAndBusyByIMSIAndICCID(ModemProviderSessionEntity modemProviderSessionEntity, Boolean busy, String IMSI, String ICCID);

    Optional<ModemEntity> findByIdAndBusyFalse(Long id);

    @Transactional
    @Modifying
    @Query("""
            update ModemEntity m set m.modemProviderSessionEntity = ?1
            where m.modemProviderSessionEntity = ?2 and m.IMSI in ?3""")
    int updateModemProviderSessionEntityByModemProviderSessionEntityAndIMSIIn(ModemProviderSessionEntity modemProviderSessionEntity, ModemProviderSessionEntity modemProviderSessionEntity1, Collection<String> IMSIS);

    @Transactional
    @Modifying
    @Query("""
            update ModemEntity m set m.modemProviderSessionEntity = ?1
            where m.modemProviderSessionEntity = ?2 and m.busy = false""")
    int updateModemProviderSessionEntityByModemProviderSessionEntityAndBusyFalse(ModemProviderSessionEntity modemProviderSessionEntity, ModemProviderSessionEntity modemProviderSessionEntity1);

    @Transactional
    @Modifying
    @Query("update ModemEntity m set m.modemProviderSessionEntity = ?1 where m.id = ?2")
    int updateModemProviderSessionEntityById(ModemProviderSessionEntity modemProviderSessionEntity, Long id);


    ModemEntity findByTaskEntity_IdAndModemProviderSessionEntity_BusyTrue(Long id);

    @Query("""
            select m from ModemEntity m
            where m.reservedBy.id = ?1 and m.reservedUntil >= ?2
            order by m.reservedUntil DESC""")
    List<ModemEntity> findByReservedBy_IdAndReservedUntilGreaterThanEqualOrderByReservedUntilDesc(Long id, Date reservedUntil);

    ModemEntity findByIMSIAndICCID(String IMSI, String ICCID);

    @Transactional
    @Modifying
    @Query("""
            update ModemEntity m set m.modemProviderSessionEntity = ?1
            where m.modemProviderSessionEntity = ?2 and m.IMSI = ?3""")
    int updateModemProviderSessionEntityByModemProviderSessionEntityAndIMSI(ModemProviderSessionEntity modemProviderSessionEntity, ModemProviderSessionEntity modemProviderSessionEntity1, String IMSI);

    List<ModemEntity> findByIMSIIn(Collection<String> IMSIS);

    @Transactional
    @Modifying
    @Query("update ModemEntity m set m.busy = ?1 where m.phoneNumber in ?2")
    int updateBusyByPhoneNumberIn(Boolean busy, Collection<String> phoneNumbers);

    @Transactional
    @Modifying
    @Query("update ModemEntity m set m.modemProviderSessionEntity = ?1 where m.modemProviderSessionEntity = ?2")
    int updateModemProviderSessionEntityByModemProviderSessionEntity(ModemProviderSessionEntity modemProviderSessionEntity, ModemProviderSessionEntity modemProviderSessionEntity1);

    @Transactional
    @Modifying
    @Query("update ModemEntity m set m.modemProviderSessionEntity = ?1 where m.modemProviderSessionEntity is not null")
    int updateModemProviderSessionEntityByModemProviderSessionEntityNotNull(ModemProviderSessionEntity modemProviderSessionEntity);

    @Transactional
    @Modifying
    @Query("update ModemEntity m set m.busy = ?1 where m.phoneNumber = ?2 and m.modemProviderSessionEntity = ?3")
    int updateBusyByPhoneNumberAndModemProviderSessionEntity(Boolean busy, String phoneNumber, ModemProviderSessionEntity modemProviderSessionEntity);

    List<ModemEntity> findByPhoneNumberIn(Collection<String> phoneNumbers);

    @Override
    Optional<ModemEntity> findById(Long aLong);

}