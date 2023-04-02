package com.example.SmsValidator.repository;

import com.example.SmsValidator.entity.ServiceTypeEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceTypeEntityRepository extends CrudRepository<ServiceTypeEntity, Long> {
    List<ServiceTypeEntity> findByActiveTrue();

    Optional<ServiceTypeEntity> findById(Long id);
}