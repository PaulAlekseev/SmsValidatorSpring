package com.example.SmsValidator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User implements UserDetails {

    @Id
    @GeneratedValue
    private Long id;
    private String email;
    private String username;
    private String password;
    private Long telegramId;
    private Boolean enabled = Boolean.FALSE;
    private Date created = new Date();
    private Date updated = new Date();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<InvoiceEntity> invoices;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<ModemProviderSessionEntity> providerSessions;

    @Column(precision = 2, columnDefinition = "FLOAT UNSIGNED")
    private double balance;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(cascade = CascadeType.DETACH, mappedBy = "reservedBy")
    private List<ModemEntity> reservedModems;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
