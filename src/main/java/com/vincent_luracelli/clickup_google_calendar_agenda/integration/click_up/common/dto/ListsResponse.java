package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.ClickUpList;

import java.util.List;

public record ListsResponse(@JsonProperty("lists") List<ClickUpList> clickUpLists) {
}
