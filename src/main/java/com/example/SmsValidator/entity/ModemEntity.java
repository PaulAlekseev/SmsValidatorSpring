package com.example.SmsValidator.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ModemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "phone", nullable = false)
    private String phoneNumber;
    @Column(name = "imsi", nullable = false)
    private String IMSI;
    @Column(name = "iccid", nullable = false)
    private String ICCID;
    @Column(name = "busy", nullable = false)
    private Boolean busy = false;
    @Column(name = "services", nullable = false)
    private String services = "";
    @Column(name = "reservedUntil", nullable = false)
    private Date reservedUntil = new Date();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserved_by")
    private User reservedBy;

    @OneToMany(cascade = CascadeType.DETACH, mappedBy = "modemEntity")
    private Set<TaskEntity> taskEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country")
    private CountryEntity countryEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modem_provider_session")
    private ModemProviderSessionEntity modemProviderSessionEntity;

    public String addService(String service) {
        System.out.println("SERVICES INSIDE METHOD: " + services);
        System.out.println("SERVICE INPUT: " + service);
        if (!services.contains(service)) services += service + ",";
        return services;
    }
}
