package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.domain.event.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventResponse {

    private String id;
    private String status;
    private String htmlLink;
    private LocalDateTime created;
    private LocalDateTime updated;

}
