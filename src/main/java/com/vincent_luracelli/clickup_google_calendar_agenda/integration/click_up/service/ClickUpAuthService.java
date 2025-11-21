package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.service;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.exception.ApiException;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.click_up_token.model.ClickUpToken;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.click_up_token.service.ClickUpTokenService;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.MeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClickUpAuthService {

    private final ClickUpTokenService clickUpTokenService;

    public MeResponse me(String userEmail) {
        MeResponse meResponse = MeResponse.builder()
                .success(true)
                .message("Authorized.")
                .build();

        try {
            findClickUpTokenOrThrow(userEmail);
        } catch (ApiException e) {
            return meResponse.toBuilder().success(false).message(e.getMessage()).build();
        }

        return meResponse;
    }

    public ClickUpToken findClickUpTokenOrThrow(String userEmail) {
        Optional<ClickUpToken> clickUpTokenOptional = clickUpTokenService.findByUserEmail(userEmail);

        if (clickUpTokenOptional.isEmpty()) {
            throw new ApiException(
                    "Permission denied. Please finish OAuth flow to proceed.",
                    HttpStatus.FORBIDDEN.value()
            );
        }

        return clickUpTokenOptional.get();
    }

}
