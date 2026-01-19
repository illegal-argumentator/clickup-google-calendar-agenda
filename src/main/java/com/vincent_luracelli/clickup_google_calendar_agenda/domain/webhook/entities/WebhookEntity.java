package com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebhookEntity {
    @Id
    private String webhookId;
    private String userId;
    @CreatedDate
    private OffsetDateTime createdAt;
}
