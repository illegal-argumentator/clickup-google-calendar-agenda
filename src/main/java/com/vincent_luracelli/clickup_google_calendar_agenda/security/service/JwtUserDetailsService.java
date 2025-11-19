package com.vincent_luracelli.clickup_google_calendar_agenda.security.service;

import com.vincent_luracelli.clickup_google_calendar_agenda.security.common.dto.TokenPayload;
import com.vincent_luracelli.clickup_google_calendar_agenda.security.common.exception.AccessDeniedException;
import com.vincent_luracelli.clickup_google_calendar_agenda.security.common.helper.JwtHelper;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService {

    private final JwtHelper jwtHelper;

    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;

    public TokenPayload generateTokenPayload(UserDetails userDetails) {
        if (userDetails == null) {
            throw new IllegalArgumentException("User details is required.");
        }

        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return TokenPayload.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public UserDetails retrieveUserDetailsFromToken(String token) {
        Claims claims = jwtHelper.extractAllClaims(token);
        return userDetailsService.loadUserByUsername(claims.getSubject());
    }

    public UserDetails retrieveUserDetailsFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails userDetails)) {
            throw new AccessDeniedException("Unauthorized.");
        }

        return userDetailsService.loadUserByUsername(userDetails.getUsername());
    }
}
