package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CustomField(
        String id,
        String name,
        String type,
        Value value
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Value(@JsonProperty("formatted_address") String formattedAddress){
    }
}
