package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.click_up;

import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.service.ClickUpAuthService;
import com.vincent_luracelli.clickup_google_calendar_agenda.security.service.JwtUserDetailsService;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.MeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/click-up/auth")
public class ClickUpAuthController {

    private final JwtUserDetailsService jwtUserDetailsService;

    private final ClickUpAuthService clickUpAuthService;

    @GetMapping("/me")
    ResponseEntity<MeResponse> me() {
        String username = jwtUserDetailsService.getUserFromContext().getUsername();
        return ResponseEntity.ok(clickUpAuthService.me(username));
    }

}
