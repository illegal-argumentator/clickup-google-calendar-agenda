package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.click_up;

import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.service.ClickUpOAuthService;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.AuthorizeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/click-up/oauth")
@RequiredArgsConstructor
public class ClickUpOAuthController {

    private final ClickUpOAuthService clickUpOauthService;

    @GetMapping("/authorize")
    ResponseEntity<AuthorizeResponse> authorize() {
        AuthorizeResponse authorizeResponse = clickUpOauthService.authorize();
        return ResponseEntity.ok(authorizeResponse);
    }

    @PostMapping("/callback")
    void callback(@RequestParam String code) {
        clickUpOauthService.callback(code);
    }

}
