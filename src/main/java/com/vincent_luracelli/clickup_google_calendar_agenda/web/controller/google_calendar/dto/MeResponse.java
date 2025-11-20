package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class MeResponse {

    private Boolean success;
    private String message;

}
