package com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.service;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.common.type.UserRole;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.common.type.UserStatus;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.exception.UserAlreadyExistsException;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.security.common.dto.TokenPayload;
import com.vincent_luracelli.clickup_google_calendar_agenda.security.common.exception.AccessDeniedException;
import com.vincent_luracelli.clickup_google_calendar_agenda.security.common.exception.InvalidTokenException;
import com.vincent_luracelli.clickup_google_calendar_agenda.security.service.JwtService;
import com.vincent_luracelli.clickup_google_calendar_agenda.security.service.JwtUserDetailsService;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.auth.dto.AuthRequest;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.auth.dto.AuthResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.auth.dto.RefreshRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.vincent_luracelli.clickup_google_calendar_agenda.security.common.constants.AuthConstants.BEARER_PREFIX;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    private final JwtUserDetailsService jwtUserDetailsService;

    public AuthResponse signIn(AuthRequest authRequest) {
        User user = userService.findByEmailOrThrow(authRequest.email());

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new AccessDeniedException("User is unactive.");
        }
        matchPasswordsOrThrow(authRequest.password(), user.getPassword());

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public void signUp(AuthRequest authRequest) {
        userService.findByEmail(authRequest.email()).ifPresent(user -> {
            throw new UserAlreadyExistsException("User already exists.");
        });

        String encodedPassword = passwordEncoder.encode(authRequest.password());

        userService.save(User.builder()
                .email(authRequest.email())
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .password(encodedPassword)
                .build());
    }

    public AuthResponse refresh(RefreshRequest refreshRequest) {
        if (!refreshRequest.refreshToken().startsWith(BEARER_PREFIX)) {
            throw new InvalidTokenException("Invalid token.");
        }
        String token = refreshRequest.refreshToken().substring(BEARER_PREFIX.length());

        UserDetails userDetails = jwtUserDetailsService.getUserDetailsFromToken(token);
        TokenPayload tokenPayload = jwtUserDetailsService.generateTokenPayload(userDetails);

        return AuthResponse.builder()
                .accessToken(tokenPayload.getAccessToken())
                .refreshToken(tokenPayload.getRefreshToken())
                .build();
    }

    private void matchPasswordsOrThrow(String rawPassword, String encodedPassword) {
        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
        if (!matches) {
            throw new BadCredentialsException("Wrong credentials");
        }
    }
}
