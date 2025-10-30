package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up;

import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.TeamsResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestRunner implements CommandLineRunner {

    private final ClickUpClient clickUpClient;

    @Override
    public void run(String... args) {
        TeamsResponse teams = clickUpClient.findTeams();
        for (Team team : teams.teams()) {
            System.out.println(team.name());
        }
    }
}
