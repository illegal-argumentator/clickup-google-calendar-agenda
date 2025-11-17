package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto;

import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.type.EventUpdates;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventParam {

    private Boolean supportsAttachments;

    private EventUpdates sendUpdates;

}
