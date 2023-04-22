package com.example.SmsValidator.config;

import com.example.SmsValidator.auth.JwtTokenFilter;
import com.example.SmsValidator.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtTokenFilter jwtTokenFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/v1/auth/**").permitAll();
                    auth.requestMatchers("/api/v1/admin/**").hasAuthority(Role.ADMIN.name());
                    auth.requestMatchers("/socket").hasAuthority(Role.MODEM_PROVIDER.name());
                    auth.requestMatchers("/api/v1/payment/coinRemitter/notify").permitAll();
                    auth.requestMatchers("/api/v1/auth/verify/**").permitAll();
                    auth.requestMatchers("/api/v1/auth/restorePassword/**").permitAll();
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(policy -> {
                    policy.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*")); // Set allowed origins
        configuration.setAllowedMethods(Arrays.asList("*")); // Set allowed HTTP methods
        configuration.setAllowedHeaders(Arrays.asList("*")); // Set allowed headers
        configuration.setExposedHeaders(Arrays.asList("*")); // Set exposed headers
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
