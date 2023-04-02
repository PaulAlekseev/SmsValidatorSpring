package com.example.SmsValidator.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class ServiceTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String name;
    private String abbreviation;
    private Boolean active;

    private String messageRegex;
    private String senderRegex;
    private int allowedAmount;
    private int daysBetween;
    private Float cost;

    private Long timeSeconds;


    @OneToMany(cascade = CascadeType.DETACH, mappedBy = "serviceTypeEntity")
    private List<TaskEntity> taskEntity;
}
