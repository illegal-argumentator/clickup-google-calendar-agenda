package com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
public class Event {

    @Id
    private String id;

    private String title;

    private String userEmail;

}
