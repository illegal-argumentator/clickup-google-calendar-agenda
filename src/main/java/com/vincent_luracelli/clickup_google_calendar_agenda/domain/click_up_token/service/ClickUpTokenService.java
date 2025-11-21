package com.vincent_luracelli.clickup_google_calendar_agenda.domain.click_up_token.service;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.click_up_token.model.ClickUpToken;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.click_up_token.repository.ClickUpTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClickUpTokenService {

    private final ClickUpTokenRepository clickUpTokenRepository;

    public ClickUpToken save(ClickUpToken clickUpToken) {
        return findByUserEmail(clickUpToken.getUserEmail())
                .map(existing -> {
                    clickUpToken.setId(existing.getId());
                    return clickUpTokenRepository.save(clickUpToken);
                })
                .orElseGet(() -> clickUpTokenRepository.save(clickUpToken));
    }

    public Optional<ClickUpToken> findByUserEmail(String userEmail) {
        return clickUpTokenRepository.findByUserEmail(userEmail);
    }
}
