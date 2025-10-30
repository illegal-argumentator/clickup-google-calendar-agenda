package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.common.dto.embedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkingLocationProperties {

    private String type;
    private CustomLocation customLocation;
    private Boolean homeOffice;
    private OfficeLocation officeLocation;

}
