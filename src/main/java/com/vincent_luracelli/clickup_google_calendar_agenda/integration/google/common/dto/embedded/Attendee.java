package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.common.dto.embedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Attendee {

    private Integer additionalGuests;
    private String comment;
    private String displayName;

    @NonNull
    private String email;

    private Boolean optional;
    private Boolean resource;
    private String responseStatus;

}
