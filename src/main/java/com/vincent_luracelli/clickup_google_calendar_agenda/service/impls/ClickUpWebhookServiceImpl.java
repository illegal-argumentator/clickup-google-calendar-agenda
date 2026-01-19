package com.vincent_luracelli.clickup_google_calendar_agenda.service.impls;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.props.WebBackendProps;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.util.ThreadUtils;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.util.tries.TryUtils;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.repository.UserRepository;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.ClickUpClient;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.clickup.ClickUpWebhookBody;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Team;
import com.vincent_luracelli.clickup_google_calendar_agenda.service.ClickUpWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
class ClickUpWebhookServiceImpl implements ClickUpWebhookService {
    private final WebBackendProps webBackendProps;
    private final UserRepository userRepository;
    private final ClickUpClient clickUpClient;


    @Override
    public void setupWebhook(String... userIds) {
        var users = userRepository.findBy(createSearch(Set.of(userIds)));
        if (users.isEmpty()) {
            return;
        }
        execute(users);
    }

    @Override
    public void setupWebhook() {
        var users = userRepository.findBy(createSearch(Set.of()));
        if (users.isEmpty()) {
            return;
        }
        execute(users);


    }

    private void execute(List<User> users) {
        for (User user : users) {
            var result = TryUtils.tryGet(() -> clickUpClient.findTeams(user), 3, () -> ThreadUtils.sleep(30_000))
                    .filter(it -> !CollectionUtils.isEmpty(it.teams()))
                    .onFail(e -> log.warn(e.getMessage(), e));
            if (result.isFailure()){
                continue;
            }
            var teams = result.orElseThrow().teams();
            for (Team team : teams) {
                var webhookResult = TryUtils.tryGet(() -> clickUpClient.getWebhooks(team.id(), user), 3,
                        () -> ThreadUtils.sleep(30_000)
                ).onFail(e -> log.warn(e.getMessage(), e));
                if (webhookResult.isFailure()) {
                    continue;
                }
                var webhooks = webhookResult.orElseThrow();
                if (webhooks.stream().anyMatch(it -> it.endpoint().contains(webBackendProps.getDomain()))) {
                    continue;
                }
                var body = new ClickUpWebhookBody(
                        "https://" +   webBackendProps.getDomain() + webBackendProps.getClickUpWebhookPath(),
                        Set.of("taskUpdated", "taskDueDateUpdated")
                );
                TryUtils.tryGet(() -> clickUpClient.createWebhooks(team.id(), body, user), 3,
                        () -> ThreadUtils.sleep(30_000)
                ).onFail(e -> log.warn(e.getMessage(), e))
                        .onSuccess(webhook -> log.info("Webhook created for user {}: {}", user.getId(), webhook.webhook().endpoint()));
            }
        }
    }

    private Query createSearch(Set<String> userIds) {
        var query = new Query().addCriteria(
                Criteria.where("clickUpTokenId").exists(true).ne(null)
        );

        if (!CollectionUtils.isEmpty(userIds)) {
            query.addCriteria(Criteria.where("id").in(userIds));
        }

        return query;
    }
}
