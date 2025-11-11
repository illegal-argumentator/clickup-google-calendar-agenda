package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventListParam {

    private Boolean showDeleted;

}
