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
        List<Selected> selected,

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

    public static final String LOCATION_FIELD = "Location";
    public static final String GENODIGDEN_FIELD = "genodigden";
    public Location valueToLocation() {
        try {
            return ((Location) value);
        } catch (ClassCastException e) {
            return null;
        }
    }


    public static String getLocationFromField(CustomField field) {
        if (field == null || field.valueToLocation() == null) return null;
        return field.valueToLocation().formattedAddress;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Location(@JsonProperty("formatted_address") String formattedAddress) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Selected(String label) {
    }

}
