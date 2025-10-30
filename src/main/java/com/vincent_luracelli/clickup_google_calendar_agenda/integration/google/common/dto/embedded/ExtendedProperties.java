package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.common.dto.embedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExtendedProperties {

    @JsonProperty("private")
    private Map<String, String> privateProps;

    private Map<String, String> shared;

}
