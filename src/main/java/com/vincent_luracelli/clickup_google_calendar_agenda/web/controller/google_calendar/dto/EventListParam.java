package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto;

import com.google.api.client.util.DateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventListParam {

    private Boolean showDeleted;
    private String orderBy;
    private Integer maxResults;
    private DateTime startTime;
    private DateTime endTime;
    private String pageToken;

}
