package com.vincent_luracelli.clickup_google_calendar_agenda.domain.click_up_token.repository;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.click_up_token.model.ClickUpToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ClickUpTokenRepository extends MongoRepository<ClickUpToken, String> {

    Optional<ClickUpToken> findByUserEmail(String userEmail);

}
