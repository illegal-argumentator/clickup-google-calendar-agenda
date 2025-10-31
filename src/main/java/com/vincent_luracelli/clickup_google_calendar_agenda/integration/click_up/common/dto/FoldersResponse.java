package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto;

import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Folder;

import java.util.List;

public record FoldersResponse(List<Folder> folders) {
}
