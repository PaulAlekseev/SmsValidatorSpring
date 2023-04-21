package com.example.SmsValidator.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class CountryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    private String imsiCode;
    private String name;
    private String countryCode;

    @OneToMany(cascade = CascadeType.DETACH, mappedBy = "countryEntity")
    private List<ModemEntity> modems;
}
