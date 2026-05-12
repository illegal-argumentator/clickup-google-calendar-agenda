package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
public record CustomField(
        String id,
        String name,
        String type,

        Object value,

        @JsonProperty("type_config")
        TypeConfig typeConfig) {

    public List<String> valueToList() {
        if (value instanceof List<?> list) {
            return list.stream()
                    .map(String::valueOf)
                    .toList();
        }
        return List.of();
    }

}
