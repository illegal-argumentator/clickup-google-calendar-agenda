package com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook;

import com.github.benmanes.caffeine.cache.Cache;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.model.Event;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.repository.EventRepository;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.modification.factory.EventModificationFactory;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.modification.strategy.EventModificationStrategy;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.modification.type.ModificationType;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.util.EventUtils;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.ClickUpClient;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.clickup.ClickUpWebhookPayload;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
@RequiredArgsConstructor
public final class EventModificationService {

    private final Cache<String, ReentrantLock> lockCache;
    private final EventModificationFactory factory;
    private final EventRepository eventRepository;
    private final ClickUpClient clickUpClient;

    public void process(User user, ClickUpWebhookPayload payload) {
        ReentrantLock reentrantLock = lockCache.get(user.getId(), k -> new ReentrantLock(true));

        Objects.requireNonNull(reentrantLock).lock();

        try {
            log.info("Processing event modification for user: {}.", user.getEmail());
            modifyEvent(user, payload);
        } catch (Exception e) {
            log.error("Exception while processing event modification: {}.", e.getMessage());
        } finally {
            log.info("Finished event processing for user: {}.", user.getEmail());
            Objects.requireNonNull(reentrantLock).unlock();
        }
    }

    public void modifyEvent(User user, ClickUpWebhookPayload payload) {
        List<Event> events = eventRepository.findByTaskIdAndUserEmail(payload.taskId(), user.getEmail());
        Task task = clickUpClient.findTask(user.getClickUpTokenId(), payload.taskId());
        Set<ModificationType> requestModifications = EventUtils.getAllModificationTypes(payload.historyItems());

        for (ModificationType modification : requestModifications) {
            EventModificationStrategy strategy = factory.getStrategy(modification);
            log.info("Processing modification for type: {}, for user: {}.", strategy.getType(), user.getEmail());
            strategy.modify(user, payload, events, task);
        }
    }
}
