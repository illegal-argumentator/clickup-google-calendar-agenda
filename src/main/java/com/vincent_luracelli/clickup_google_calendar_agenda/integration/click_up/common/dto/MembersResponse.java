package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto;

import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembersResponse {

    private Set<Member> members;

}
