package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Listing;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ListsResponse(@JsonProperty("lists") List<Listing> listings) {
}
