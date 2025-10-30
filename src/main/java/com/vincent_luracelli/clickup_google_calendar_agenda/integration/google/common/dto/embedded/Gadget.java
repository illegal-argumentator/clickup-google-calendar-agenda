package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.common.dto.embedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Gadget {

    private String display; // "icon", "chip"
    private Integer height;
    private String iconLink;
    private String link;
    private Map<String, String> preferences;
    private String title;
    private String type;
    private Integer width;

}
