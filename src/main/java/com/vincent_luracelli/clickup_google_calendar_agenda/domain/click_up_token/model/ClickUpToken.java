package com.vincent_luracelli.clickup_google_calendar_agenda.domain.click_up_token.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder(toBuilder = true)
@Document
public class ClickUpToken {

    @Id
    private String id;

    @Indexed(unique = true)
    private String userEmail;

    private String accessToken;

}
