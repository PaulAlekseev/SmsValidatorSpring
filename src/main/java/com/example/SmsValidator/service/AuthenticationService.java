package com.example.SmsValidator.service;

import com.example.SmsValidator.auth.JwtTokenProvider;
import com.example.SmsValidator.bean.authentication.request.AuthenticationRequest;
import com.example.SmsValidator.bean.authentication.response.AuthenticationSuccessResponse;
import com.example.SmsValidator.bean.refreshtoken.request.RefreshTokenBaseRequest;
import com.example.SmsValidator.bean.refreshtoken.response.RefreshTokenSuccessResponse;
import com.example.SmsValidator.entity.User;
import com.example.SmsValidator.exception.customexceptions.user.UserNotFoundException;
import com.example.SmsValidator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);


    public AuthenticationSuccessResponse authenticate(AuthenticationRequest request) {
        logger.info(request.getEmail() + " trying to authenticate");
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = repository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String jwtToken = tokenProvider.createAuthToken(user.getUsername(), user.getRole().name());
        String jwtRefreshToken = tokenProvider.createRefreshToken(user.getUsername(), user.getRole().name());
        AuthenticationSuccessResponse response = new AuthenticationSuccessResponse();
        response.setRefreshToken(jwtRefreshToken);
        response.setAuthToken(jwtToken);
        logger.info(request.getEmail() + "successfully authenticated");
        return response;
    }

    public RefreshTokenSuccessResponse refreshToken(RefreshTokenBaseRequest request)
            throws UserNotFoundException {
        String refreshToken = request.getRefreshToken();
        String username = tokenProvider.getUserName(refreshToken);
        User user = repository.findByEmail(username).
                orElseThrow(() -> new UserNotFoundException("User not found", this.getClass()));
        String newAuthToken = tokenProvider.createAuthToken(username, user.getRole().name());
        logger.info(username + " successfully refreshed token");
        return new RefreshTokenSuccessResponse(newAuthToken);
    }
}
