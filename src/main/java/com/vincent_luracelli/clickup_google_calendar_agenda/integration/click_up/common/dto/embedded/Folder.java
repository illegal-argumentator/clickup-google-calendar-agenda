package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Folder(
        String id,
        String name,
        List<FolderList> list

) {

    record FolderList(
            String id,
            String name
    ) {
    }
}
