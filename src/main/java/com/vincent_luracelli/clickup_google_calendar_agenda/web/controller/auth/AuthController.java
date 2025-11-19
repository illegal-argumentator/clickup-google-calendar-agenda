package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.auth;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.service.AuthService;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.auth.dto.AuthRequest;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.auth.dto.AuthResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.auth.dto.RefreshRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-in")
    ResponseEntity<AuthResponse> signIn(@Valid @RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(authService.signIn(authRequest));
    }

    @PostMapping("/sign-up")
    void signUp(@Valid @RequestBody AuthRequest authRequest) {
        authService.signUp(authRequest);
    }

    @PostMapping("/refresh")
    ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest refreshRequest) {
        return ResponseEntity.ok(authService.refresh(refreshRequest));
    }

}
