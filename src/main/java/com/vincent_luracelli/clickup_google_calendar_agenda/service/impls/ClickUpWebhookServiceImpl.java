package com.vincent_luracelli.clickup_google_calendar_agenda.service.impls;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.props.WebBackendProps;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.util.ThreadUtils;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.util.tries.TryUtils;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.repository.UserRepository;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.entities.WebhookEntity;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.repositories.WebhookRepository;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.ClickUpClient;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.clickup.ClickUpWebhook;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.clickup.ClickUpWebhookBody;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Team;
import com.vincent_luracelli.clickup_google_calendar_agenda.service.ClickUpWebhookService;
import com.vincent_luracelli.clickup_google_calendar_agenda.service.WebhookEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
class ClickUpWebhookServiceImpl implements ClickUpWebhookService {
    private final Cache<String, String> secretCacheManager = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();
    private final WebBackendProps webBackendProps;
    private final UserRepository userRepository;
    private final ClickUpClient clickUpClient;
    private final WebhookRepository webhookRepository;

    @Override
    public Optional<String> getSecret(String webhookId) {
        var secret = secretCacheManager.getIfPresent(webhookId);
        if (secret != null) {
            return Optional.of(secret);
        }
        var webhookEntity = webhookRepository.findById(webhookId);
        if (webhookEntity.isEmpty()) {
            return Optional.empty();
        }

        var user = userRepository.findById(webhookEntity.orElseThrow().getUserId());
        if (user.isEmpty()){
            return Optional.empty();
        }
        execute(List.of(user.orElseThrow()));

        return Optional.ofNullable(secretCacheManager.getIfPresent(webhookId));
    }

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
//                    .filter(it -> !CollectionUtils.isEmpty(it.teams()))
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
                    webhooks.stream().filter(it -> it.endpoint().contains(webBackendProps.getDomain()))
                            .findFirst()
                            .ifPresent(existingWebhook -> {
                                log.info("Webhook already exists for user {}: {}", user.getId(), existingWebhook.endpoint());
                                handleWebhook(existingWebhook, user.getId());
                                // TODO delete webhooks
                                clickUpClient.deleteWebhooks(existingWebhook.id(), user);
                            });
                    continue;
                }

                createWebhook(team.id(), user);
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

    private void handleWebhook(ClickUpWebhook webhook, String userId) {
        secretCacheManager.put(webhook.id(), webhook.secret());
        var entity = new WebhookEntity(
                webhook.id(),
                userId,
                Instant.now()
        );
        webhookRepository.save(entity);
    }

    @Override
    public void createWebhook(String teamId, User user) {
        var body = new ClickUpWebhookBody(
                "https://" +   webBackendProps.getDomain() + webBackendProps.getClickUpWebhookPath(),
                Arrays.stream(WebhookEvent.values()).map(WebhookEvent::getEvent).collect(Collectors.toSet())
        );

        TryUtils.tryGet(() -> clickUpClient.createWebhooks(teamId, body, user.getClickUpTokenId()), 3,
                        () -> ThreadUtils.sleep(30_000)
                ).onFail(e -> log.warn(e.getMessage(), e))
                .onSuccess(webhook -> {
                    log.info("Webhook created for user {}: {}", user.getId(), webhook.webhook().endpoint());
                    handleWebhook(webhook.webhook(), user.getId());
                });
    }
}
