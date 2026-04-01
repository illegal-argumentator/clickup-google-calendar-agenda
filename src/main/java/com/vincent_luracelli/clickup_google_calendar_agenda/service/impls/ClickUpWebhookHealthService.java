package com.vincent_luracelli.clickup_google_calendar_agenda.service.impls;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.service.UserService;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.ClickUpClient;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.TeamsResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.clickup.ClickUpWebhook;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Team;
import com.vincent_luracelli.clickup_google_calendar_agenda.service.ClickUpWebhookService;
import com.vincent_luracelli.clickup_google_calendar_agenda.service.WebhookHealthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClickUpWebhookHealthService implements WebhookHealthService {

    private final ClickUpWebhookService webhookService;

    private final ClickUpClient clickUpClient;

    private final UserService userService;

    @Override
    public void checkAndFix() {
        List<User> users = userService.findAllClickUpAuthorized();
        users.forEach(this::processForUser);
    }

    private void processForUser(User user) {

        TeamsResponse teams = clickUpClient.findTeams(user);
        teams.teams().forEach(team -> processUserForTeam(user, team));
    }

    private void processUserForTeam(User user, Team team) {
        List<ClickUpWebhook> webhooks = clickUpClient.getWebhooks(team.id(), user);
        System.out.println("-------------------------------------------------");
        webhooks.forEach(webhook -> processWebhook(user, webhook));
        System.out.println("-------------------------------------------------");
    }

    private void processWebhook(User user, ClickUpWebhook webhook) {
        ClickUpWebhook.Health health = webhook.health();
        System.out.println("user: " + user.getEmail() + ", health: " + health.status());

    }


}
