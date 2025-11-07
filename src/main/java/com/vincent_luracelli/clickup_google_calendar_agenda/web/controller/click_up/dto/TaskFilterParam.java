package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.click_up.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TaskFilterParam {

    private Integer page;
    private String orderBy;
    private Boolean reverse;
    private Boolean includeClosed;
    private List<String> statuses;
    private List<String> spaceIds;
    private List<String> projectIds;
    private List<String> listIds;
    private List<String> assignees;
    private Integer dueDateGt;
    private Integer dueDateLt;

}
