package com.vincent_luracelli.clickup_google_calendar_agenda.security.service;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.common.type.UserStatus;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.security.common.config.JwtProperties;
import com.vincent_luracelli.clickup_google_calendar_agenda.security.common.helper.JwtHelper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.vincent_luracelli.clickup_google_calendar_agenda.security.common.type.TokenClaim.ROLES_CLAIM;
import static com.vincent_luracelli.clickup_google_calendar_agenda.security.common.type.TokenClaim.STATUS_CLAIM;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtHelper jwtHelper;

    private final JwtProperties jwtProperties;

    public String generateAccessToken(UserDetails userDetails) {
        List<String> roles = jwtHelper.getRoles(userDetails);
        UserStatus status = ((User) userDetails).getStatus();

        return generateAccessToken(userDetails, Map.of(
                ROLES_CLAIM.getClaim(), roles,
                STATUS_CLAIM.getClaim(), status
        ));
    }

    public String generateAccessToken(UserDetails userDetails, Map<String, Object> claims) {
        return buildToken(userDetails, claims, jwtProperties.getAccessExpirationTime());
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(userDetails, Map.of(), jwtProperties.getRefreshExpirationTime());
    }

    private String buildToken(UserDetails userDetails, Map<String, Object> claims, int expiration) {
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(jwtHelper.getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
