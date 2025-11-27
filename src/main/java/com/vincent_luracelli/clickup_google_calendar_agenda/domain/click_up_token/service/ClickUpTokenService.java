package com.vincent_luracelli.clickup_google_calendar_agenda.domain.click_up_token.service;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.exception.EntityAlreadyExistsException;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.exception.EntityNotFoundException;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.calendar_token.model.CalendarToken;
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
        Optional<ClickUpToken> optionalClickUpToken = findByUserEmail(clickUpToken.getUserEmail());

        if (optionalClickUpToken.isPresent()) {
            throw new EntityAlreadyExistsException("ClickUp token already exists.");
        }

        return clickUpTokenRepository.save(clickUpToken);
    }

    public ClickUpToken saveOrUpdateIfExists(ClickUpToken clickUpToken) {
        try {
            return save(clickUpToken);
        } catch (EntityAlreadyExistsException e) {
            return update(clickUpToken);
        }
    }

    public ClickUpToken update(ClickUpToken updateClickUpToken) {
        ClickUpToken clickUpToken = clickUpTokenRepository.findByUserEmail(updateClickUpToken.getUserEmail())
                .orElseThrow(() -> new EntityNotFoundException("ClickUp token not found."));

        Optional.ofNullable(updateClickUpToken.getAccessToken()).ifPresent(clickUpToken::setAccessToken);
        Optional.ofNullable(updateClickUpToken.getUserEmail()).ifPresent(clickUpToken::setUserEmail);

        return clickUpTokenRepository.save(clickUpToken);
    }

    public Optional<ClickUpToken> findByUserEmail(String userEmail) {
        return clickUpTokenRepository.findByUserEmail(userEmail);
    }

    public Optional<ClickUpToken> findById(String id) {
        return clickUpTokenRepository.findById(id);
    }
}
