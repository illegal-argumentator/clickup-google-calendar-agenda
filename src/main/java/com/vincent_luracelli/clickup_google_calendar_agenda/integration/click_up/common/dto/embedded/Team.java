package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Team(
        String id,
        String name,
        String color,
        String avatar,
        List<Member> members) {
}
