package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto;

import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Space;

import java.util.List;

public record SpacesResponse(List<Space> spaces) {
}
