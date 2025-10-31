package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto;

import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Team;

import java.util.List;

public record TeamsResponse(List<Team> teams) {
}
