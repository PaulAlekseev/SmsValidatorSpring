package com.example.SmsValidator.auth;

import com.example.SmsValidator.entity.User;
import com.example.SmsValidator.service.UserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final UserService userService;

    @Value("${auth.token.secret}")
    private String SECRET_KEY;

    @Value("${auth.token.auth-expiration}")
    private Long AUTH_EXPIRATION;

    @Value("${auth.token.refresh-expiration}")
    private Long REFRESH_EXPIRATION;

    public String createAuthToken(String userName, String role) {
        Claims claims = Jwts.claims().setSubject(userName);
        claims.put("role", role);
        Date now = new Date();
        Date valid = new Date(now.getTime() + AUTH_EXPIRATION);
        return Jwts
                .builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(valid)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    public String createRefreshToken(String userName, String role) {
        Claims claims = Jwts.claims().setSubject(userName);
        claims.put("role", role);
        Date now = new Date();
        Date valid = new Date(now.getTime() + REFRESH_EXPIRATION);
        return Jwts
                .builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(valid)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts
                    .parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build().parseClaimsJws(token);
            return claimsJws
                    .getBody()
                    .getExpiration()
                    .after(new Date());
        } catch (ExpiredJwtException e) {
            log.error(e.getLocalizedMessage());
        }
        return false;
    }

    public Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getUserName(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String resolveToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        Pattern pattern = Pattern.compile("Bearer\s(.*)");
        Matcher matcher = pattern.matcher(authHeader);
        if (matcher.find()) return matcher.group(1);
        return null;
    }

    public Authentication getAuthentication(String token) {
        User appUser = userService.loadByUsername(getUserName(token));
        return new UsernamePasswordAuthenticationToken(appUser, appUser.getPassword(), appUser.getAuthorities());
    }
}
