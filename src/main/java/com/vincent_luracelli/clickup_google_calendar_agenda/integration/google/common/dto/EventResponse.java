package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.common.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class EventResponse {

    private String kind;
    private String etag;
    private String id;
    private String status;
    private String htmlLink;
    private LocalDateTime created;
    private LocalDateTime updated;
    private String summary;
    private String description;
    private String location;
    private String colorId;
    private Creator creator;
    private Organizer organizer;
    private EventDateTime start;
    private EventDateTime end;

    @Data
    public static class Creator {
        private String id;
        private String email;
        private String displayName;
        private boolean self;
    }

    @Data
    public static class Organizer {
        private String id;
        private String email;
        private String displayName;
        private boolean self;
    }

    @Data
    public static class EventDateTime {
        private Date date;
        private Date dateTime;
        private String timeZone;
    }
}
