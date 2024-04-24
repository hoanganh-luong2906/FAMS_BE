package com.example.FAMS.services;

import org.springframework.security.core.userdetails.UserDetails;

public interface JWTService {
    String extractUserEmail(String token);

    boolean isTokenValid(String token, UserDetails userDetails);

    String generateToken(UserDetails userDetails);

    String generateRefreshToken(UserDetails userDetails);

    boolean isTokenExpired(String token);
}
