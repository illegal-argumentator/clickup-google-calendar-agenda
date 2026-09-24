package com.vincent_luracelli.clickup_google_calendar_agenda.service.impls;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.service.UserService;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.ClickUpClient;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.TeamsResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.clickup.ClickUpWebhook;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Team;
import com.vincent_luracelli.clickup_google_calendar_agenda.service.ClickUpWebhookService;
import com.vincent_luracelli.clickup_google_calendar_agenda.service.WebhookHealth;
import com.vincent_luracelli.clickup_google_calendar_agenda.service.WebhookHealthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClickUpWebhookHealthService implements WebhookHealthService {

    private final UserService userService;

    private final ClickUpWebhookService webhookService;
    private final ClickUpClient clickUpClient;

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
        webhooks.forEach(webhook -> processWebhook(team.id(), user, webhook));
    }

    private void processWebhook(String teamId, User user, ClickUpWebhook webhook) {
        ClickUpWebhook.Health health = webhook.health();
        if (!WebhookHealth.isActive(health.status())) fixWebhook(webhook.id(), teamId, user);
    }

    private void fixWebhook(String webhookId, String teamId, User user) {
        log.warn("Found failing webhook for user: {}. Fixing...", user.getEmail());
        clickUpClient.deleteWebhooks(webhookId, user);
        webhookService.createWebhook(teamId, user);
    }
}
