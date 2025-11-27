package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.service;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.exception.ApiException;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.click_up_token.model.ClickUpToken;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.click_up_token.service.ClickUpTokenService;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.MeResponse;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClickUpAuthService {

    private final ClickUpTokenService clickUpTokenService;

    public MeResponse me(User user) {
        MeResponse meResponse = MeResponse.builder()
                .success(true)
                .message("ClickUp account successfully authorized.")
                .build();

        try {
            findClickUpTokenOrThrow(user.getClickUpTokenId());
        } catch (ApiException e) {
            return meResponse.toBuilder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        }

        return meResponse;
    }

    public ClickUpToken findClickUpTokenOrThrow(String id) {
        ApiException apiException = new ApiException(
                "Please finish OAuth flow to proceed.",
                HttpStatus.FORBIDDEN.value()
        );

        if (StringUtils.isEmpty(id)) {
            throw apiException;
        }

        Optional<ClickUpToken> clickUpTokenOptional = clickUpTokenService.findById(id);
        if (clickUpTokenOptional.isEmpty()) {
            throw apiException;
        }

        return clickUpTokenOptional.get();
    }

}
