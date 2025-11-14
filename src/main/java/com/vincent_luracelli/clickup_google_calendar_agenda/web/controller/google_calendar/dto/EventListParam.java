package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto;

import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.embedded.OrderBy;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventListParam {

    private String search;
    private Boolean singleEvents;
    private Boolean showDeleted;
    private OrderBy orderBy;
    private Integer maxResults;
    private String pageToken;

}
