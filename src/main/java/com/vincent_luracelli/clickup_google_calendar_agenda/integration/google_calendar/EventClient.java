package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.exception.ApiException;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.type.SourceType;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.util.OkHttpUtil;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.config.GoogleProps;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.EventListResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.EventResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.InsertEventRequest;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.PatchEventRequest;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.service.CalendarOAuthTokenService;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.EventListParam;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.EventParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static com.google.auth.http.AuthHttpConstants.AUTHORIZATION;
import static com.google.auth.http.AuthHttpConstants.BEARER;
import static com.vincent_luracelli.clickup_google_calendar_agenda.common.util.WaitUtil.waitSafely;
import static com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.builder.EventsPathBuilder.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventClient {

    private final ObjectMapper objectMapper;

    private final GoogleProps googleProps;

    private final OkHttpUtil okHttpUtil;

    private final CalendarOAuthTokenService calendarOAuthTokenService;

    public EventResponse insert(EventParam eventParam, InsertEventRequest insertEventRequest) {
        String token = calendarOAuthTokenService.requireValidToken();
        String path = buildEventByPrimaryCalendarPath(eventParam);

        try {
            String jsonBody = objectMapper.writeValueAsString(insertEventRequest);

            Request request = new Request.Builder()
                    .addHeader(AUTHORIZATION, "%s %s".formatted(BEARER, token))
                    .url(path)
                    .post(RequestBody.create(jsonBody, MediaType.get(APPLICATION_JSON_VALUE)))
                    .build();

            try {
                return okHttpUtil.handleApiRequest(SourceType.GOOGLE_CALENDAR, request, EventResponse.class);
            } catch (ApiException e) {
                return handleGoogleNetworkError(request, EventResponse.class);
            }

        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException: ", e);
            throw new ApiException("Couldn't parse request body for event creation", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public void patch(String eventId, PatchEventRequest patchEventRequest, EventParam eventParam) {
        String token = calendarOAuthTokenService.requireValidToken();
        String path = buildEventByIdPath(eventId, googleProps.getCalendarId(), eventParam);

        try {
            String jsonBody = objectMapper.writeValueAsString(patchEventRequest);

            Request request = new Request.Builder()
                    .addHeader(AUTHORIZATION, "%s %s".formatted(BEARER, token))
                    .url(path)
                    .patch(RequestBody.create(jsonBody, MediaType.get(APPLICATION_JSON_VALUE)))
                    .build();

            try {
                okHttpUtil.handleApiRequest(SourceType.GOOGLE_CALENDAR, request);
            } catch (ApiException e) {
                handleGoogleNetworkError(request);
            }

            okHttpUtil.handleApiRequest(SourceType.GOOGLE_CALENDAR, request);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException: ", e);
            throw new ApiException("Couldn't parse request body for patching event", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public void delete(String eventId, EventParam eventParam) {
        String token = calendarOAuthTokenService.requireValidToken();
        String path = buildEventByIdPath(eventId, googleProps.getCalendarId(), eventParam);

        Request request = new Request.Builder()
                .addHeader(AUTHORIZATION, "%s %s".formatted(BEARER, token))
                .url(path)
                .delete()
                .build();

        try {
            okHttpUtil.handleApiRequest(SourceType.GOOGLE_CALENDAR, request);
        } catch (ApiException e) {
            handleGoogleNetworkError(request);
        }
    }

    public EventListResponse list(EventListParam eventListParam) {
        String token = calendarOAuthTokenService.requireValidToken();
        String path = buildEventListByPrimaryCalendarPath(eventListParam);

        Request request = new Request.Builder()
                .addHeader(AUTHORIZATION, "%s %s".formatted(BEARER, token))
                .url(path)
                .get()
                .build();

        try {
            return okHttpUtil.handleApiRequest(SourceType.GOOGLE_CALENDAR, request, EventListResponse.class);
        } catch (ApiException e) {
            return handleGoogleNetworkError(request,EventListResponse.class);
        }
    }

    private <T> T handleGoogleNetworkError(Request request, Class<T> responseTarget) {
        int maxRetries = 10;
        int retryDelay = 5000;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                if (responseTarget == null) {
                    okHttpUtil.handleApiRequest(SourceType.GOOGLE_CALENDAR, request);
                    return null;
                }

                return okHttpUtil.handleApiRequest(SourceType.GOOGLE_CALENDAR, request, responseTarget);

            } catch (ApiException e) {
                boolean isNetworkIssue = e.getCode() == 500 && e.getMessage().contains("Network");
                if (isNetworkIssue && attempt < maxRetries) {
                    log.warn("Network unreachable (attempt {}/{}). Retrying…", attempt, maxRetries);
                    waitSafely(retryDelay);
                    continue;
                }

                throw e;
            }
        }

        throw new ApiException("Network is unreachable.", HttpStatus.INTERNAL_SERVER_ERROR.value(), SourceType.GOOGLE_CALENDAR);
    }

    private void handleGoogleNetworkError(Request request) {
        handleGoogleNetworkError(request, null);
    }
}
