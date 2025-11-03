package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.domain.event.dto;

import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.domain.event.dto.embedded.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventRequest {

    @NotNull(message = "Start date is required")
    private EventDateTime start;

    @NotNull(message = "End date is required")
    private EventDateTime end;

    private Boolean anyoneCanAddSelf;
    private List<Attachment> attachments;
    private List<Attendee> attendees;
    private Boolean attendeesOmitted;
    private String colorId;
    private Object conferenceData;
    private String description;

    private ExtendedProperties extendedProperties;
    private Object focusTimeProperties;
    private Gadget gadget;

    private Boolean guestsCanInviteOthers;
    private Boolean guestsCanModify;
    private Boolean guestsCanSeeOtherGuests;

    private String location;

    private EventDateTime originalStartTime;

    private Object outOfOfficeProperties;
    private List<String> recurrence;

    private Reminders reminders;

    private Integer sequence;
    private Source source;
    private String status;
    private String summary;
    private String transparency;
    private String visibility;

    private WorkingLocationProperties workingLocationProperties;

}
