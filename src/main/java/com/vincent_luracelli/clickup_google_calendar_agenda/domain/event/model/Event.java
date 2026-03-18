package com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.model;

import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.embedded.Attendee;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@Document
public class Event {

    @Id
    private String id;

    private String title;

    private String userEmail;

    private String taskId;

    private String calendarTokenId;

    List<Attendee> attendees;

}
