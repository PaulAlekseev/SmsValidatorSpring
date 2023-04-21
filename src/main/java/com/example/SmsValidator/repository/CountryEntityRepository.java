package com.example.SmsValidator.repository;

import com.example.SmsValidator.entity.CountryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CountryEntityRepository extends JpaRepository<CountryEntity, Long>, JpaSpecificationExecutor<CountryEntity> {
}