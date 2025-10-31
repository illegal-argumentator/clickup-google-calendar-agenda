package com.vincent_luracelli.clickup_google_calendar_agenda.web.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TaskFilterRequest {

    private Integer page;

    private String orderBy;

    private List<String> spaceIds;

    private List<String> projectIds;

    private List<String> listIds;

}
