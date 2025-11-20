package com.vincent_luracelli.clickup_google_calendar_agenda.domain.calendar_token.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder(toBuilder = true)
@Document
public class CalendarToken {

    @Id
    private String id;

    @Indexed(unique = true)
    private String calendarId;

    private Long accessExpiration;

    private String accessToken;

    private String refreshToken;

}
