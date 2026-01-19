package com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.repositories;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.entities.WebhookEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WebhookRepository extends MongoRepository<WebhookEntity, String> {
}
