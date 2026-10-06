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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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
    public void resetWebhooks() {
        var users = userRepository.findBy(createSearch(Set.of()));
        Set<String> discoveredWebhookIds = new HashSet<>();
        Set<String> deletedWebhookIds = new HashSet<>();

        for (User user : users) {
            var teamsResult = TryUtils.tryGet(() -> clickUpClient.findTeams(user), 3,
                    () -> ThreadUtils.sleep(30_000));
            if (teamsResult.isFailure()) {
                throw new IllegalStateException("Failed to list ClickUp teams for user " + user.getId(),
                        teamsResult.exception());
            }

            for (Team team : teamsResult.orElseThrow().teams()) {
                var webhooksResult = TryUtils.tryGet(() -> clickUpClient.getWebhooks(team.id(), user), 3,
                        () -> ThreadUtils.sleep(30_000));
                if (webhooksResult.isFailure()) {
                    throw new IllegalStateException("Failed to list ClickUp webhooks for team " + team.id(),
                            webhooksResult.exception());
                }

                for (ClickUpWebhook webhook : webhooksResult.orElseThrow()) {
                    if (!isApplicationWebhook(webhook)) {
                        continue;
                    }

                    discoveredWebhookIds.add(webhook.id());
                    if (deletedWebhookIds.contains(webhook.id())) {
                        continue;
                    }

                    var deleteResult = TryUtils.tryRun(() -> clickUpClient.deleteWebhooks(webhook.id(), user));
                    if (deleteResult.isSuccess()) {
                        deletedWebhookIds.add(webhook.id());
                        log.info("Deleted ClickUp webhook {} found for user {}", webhook.id(), user.getId());
                    } else {
                        log.warn("Could not delete ClickUp webhook {} with user {}: {}",
                                webhook.id(), user.getId(), deleteResult.exception().getMessage());
                    }
                }
            }
        }

        if (!deletedWebhookIds.containsAll(discoveredWebhookIds)) {
            var remainingIds = new HashSet<>(discoveredWebhookIds);
            remainingIds.removeAll(deletedWebhookIds);
            throw new IllegalStateException("Could not delete all application ClickUp webhooks: " + remainingIds);
        }

        webhookRepository.deleteAll();
        secretCacheManager.invalidateAll();
        log.info("Removed {} application ClickUp webhooks and cleared local mappings", deletedWebhookIds.size());
    }

    private boolean isApplicationWebhook(ClickUpWebhook webhook) {
        return webhook.endpoint().contains(webBackendProps.getDomain())
                && webhook.endpoint().contains(webBackendProps.getClickUpWebhookPath());
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
                var ownedWebhook = webhooks.stream()
                        .filter(this::isApplicationWebhook)
                        .filter(it -> webhookRepository.findById(it.id())
                                .map(entity -> Objects.equals(entity.getUserId(), user.getId()))
                                .orElse(false))
                        .findFirst();

                if (ownedWebhook.isPresent()) {
                    var existingWebhook = ownedWebhook.get();
                    log.info("Webhook already exists for user {}: id={}, endpoint={}",
                            user.getId(), existingWebhook.id(), existingWebhook.endpoint());
                    handleWebhook(existingWebhook, user.getId());
                    continue;
                }

                webhooks.stream()
                        .filter(this::isApplicationWebhook)
                        .filter(it -> webhookRepository.findById(it.id()).isPresent())
                        .forEach(it -> log.info("Webhook {} has a different registered owner; not reusing it for user {}",
                                it.id(), user.getId()));

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
        var existing = webhookRepository.findById(webhook.id());
        if (existing.isPresent() && !Objects.equals(existing.get().getUserId(), userId)) {
            log.warn("Webhook {} belongs to user {}; refusing to remap it to user {}",
                    webhook.id(), existing.get().getUserId(), userId);
            return;
        }

        secretCacheManager.put(webhook.id(), webhook.secret());
        var entity = new WebhookEntity(
                webhook.id(),
                userId,
                Instant.now()
        );
        var saved = webhookRepository.save(entity);
        log.info("Persisted ClickUp webhook mapping: webhookId={}, userId={}",
                saved.getWebhookId(), saved.getUserId());
    }

    @Override
    public void createWebhook(String teamId, User user) {
        var body = new ClickUpWebhookBody(
                "https://" +   webBackendProps.getDomain() + webBackendProps.getClickUpWebhookPath(),
                Arrays.stream(WebhookEvent.values()).map(WebhookEvent::getEvent).collect(Collectors.toSet())
        );

        log.info("Creating ClickUp webhook for user {} and team {} at {}",
                user.getId(), teamId, body.endpoint());

        TryUtils.tryGet(() -> {
                    var created = clickUpClient.createWebhooks(teamId, body, user.getClickUpTokenId());
                    if (created == null || created.webhook() == null) {
                        throw new IllegalStateException("ClickUp returned an empty webhook creation response");
                    }
                    return created;
                }, 3,
                        (attempt, error) -> {
                            log.warn("ClickUp webhook creation attempt {} failed for user {} and team {}: {}",
                                    attempt + 1, user.getId(), teamId, error.getMessage(), error);
                            ThreadUtils.sleep(30_000);
                        })
                .onFail(e -> log.error("Failed to create ClickUp webhook for user {} and team {} after retries",
                        user.getId(), teamId, e))
                .onSuccess(webhook -> {
                    log.info("Webhook created for user {}: id={}, endpoint={}",
                            user.getId(), webhook.webhook().id(), webhook.webhook().endpoint());
                    handleWebhook(webhook.webhook(), user.getId());
                });
    }
}
