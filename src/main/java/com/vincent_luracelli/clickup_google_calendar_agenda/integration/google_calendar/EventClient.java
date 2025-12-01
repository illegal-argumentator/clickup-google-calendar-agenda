package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.exception.ApiException;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.type.SourceType;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.util.OkHttpUtil;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.calendar_token.model.CalendarToken;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.*;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.service.CalendarAuthService;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.EventListParam;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.EventParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.builder.EventsPathBuilder.*;
import static com.vincent_luracelli.clickup_google_calendar_agenda.security.common.constants.AuthConstants.AUTHORIZATION_HEADER;
import static com.vincent_luracelli.clickup_google_calendar_agenda.security.common.constants.AuthConstants.BEARER_PREFIX;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventClient {

    private final ObjectMapper objectMapper;

    private final OkHttpUtil okHttpUtil;

    private final CalendarAuthService calendarAuthService;

    public EventResponse insert(User user, EventParam eventParam, InsertEventRequest insertEventRequest) {
        CalendarToken token = calendarAuthService.requireAccessTokenByUser(user);
        String path = buildEventByPrimaryCalendarPath(eventParam);

        try {
            String jsonBody = objectMapper.writeValueAsString(insertEventRequest);

            Request request = new Request.Builder()
                    .addHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + token.getAccessToken())
                    .url(path)
                    .post(RequestBody.create(jsonBody, MediaType.get(APPLICATION_JSON_VALUE)))
                    .build();

            return okHttpUtil.handleApiRequest(SourceType.GOOGLE_CALENDAR, request, EventResponse.class);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException: ", e);
            throw new ApiException("Couldn't parse request body for event creation", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public void patch(User user, String eventId, PatchEventRequest patchEventRequest, EventParam eventParam) {
        CalendarToken token = calendarAuthService.requireAccessTokenByUser(user);
        String path = buildEventByIdPath(eventId, token.getUserEmail(), eventParam);

        try {
            String jsonBody = objectMapper.writeValueAsString(patchEventRequest);

            Request request = new Request.Builder()
                    .addHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + token.getAccessToken())
                    .url(path)
                    .patch(RequestBody.create(jsonBody, MediaType.get(APPLICATION_JSON_VALUE)))
                    .build();

            okHttpUtil.handleApiRequest(SourceType.GOOGLE_CALENDAR, request);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException: ", e);
            throw new ApiException("Couldn't parse request body for patching event", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public void delete(User user, String eventId, EventParam eventParam) {
        CalendarToken token = calendarAuthService.requireAccessTokenByUser(user);
        String path = buildEventByIdPath(eventId, token.getUserEmail(), eventParam);

        Request request = new Request.Builder()
                .addHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + token.getAccessToken())
                .url(path)
                .delete()
                .build();

        okHttpUtil.handleApiRequest(SourceType.GOOGLE_CALENDAR, request);
    }

    public EventListResponse list(User user, EventListParam eventListParam) {
        CalendarToken token = calendarAuthService.requireAccessTokenByUser(user);
        String path = buildEventListByPrimaryCalendarPath(eventListParam);

        Request request = new Request.Builder()
                .addHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + token.getAccessToken())
                .url(path)
                .get()
                .build();

        return okHttpUtil.handleApiRequest(SourceType.GOOGLE_CALENDAR, request, EventListResponse.class);
    }

    public GetEventResponse get(User user, String eventId) {
        CalendarToken token = calendarAuthService.requireAccessTokenByUser(user);
        String path = buildEventByPrimaryCalendarAndEventIdPath(eventId);

        Request request = new Request.Builder()
                .addHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + token.getAccessToken())
                .url(path)
                .get()
                .build();

        return okHttpUtil.handleApiRequest(SourceType.GOOGLE_CALENDAR, request, GetEventResponse.class);
    }
}
