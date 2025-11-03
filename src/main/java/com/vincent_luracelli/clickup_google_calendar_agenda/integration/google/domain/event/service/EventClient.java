package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.domain.event.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.exception.ApiRequestException;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.exception.ExceptionPayload;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.type.SourceType;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.util.OkHttpUtil;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.common.config.GoogleProps;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.domain.event.common.exception.CalendarEventException;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.domain.event.dto.EventResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.domain.event.dto.InsertEventRequest;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.domain.event.dto.UpdateEventRequest;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.InsertEventParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static com.google.auth.http.AuthHttpConstants.AUTHORIZATION;
import static com.google.auth.http.AuthHttpConstants.BEARER;
import static com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.domain.event.common.builder.EventsPathBuilder.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventClient {

    private final ObjectMapper objectMapper;

    private final GoogleProps googleProps;

    private final AuthService authService;

    private final OkHttpUtil okHttpUtil;

    public EventResponse insert(InsertEventParam insertEventParam, InsertEventRequest insertEventRequest) {
        String token = authService.getToken();
        String path = buildEventByCalendarIdPath(googleProps.getCalendarId(), insertEventParam);

        try {
            String jsonBody = objectMapper.writeValueAsString(insertEventRequest);

            Request request = new Request.Builder()
                    .addHeader(AUTHORIZATION, "%s %s".formatted(BEARER, token))
                    .url(path)
                    .post(RequestBody.create(jsonBody, MediaType.get(APPLICATION_JSON_VALUE)))
                    .build();

            return okHttpUtil.handleApiRequest(SourceType.GOOGLE_CALENDAR, request, EventResponse.class);
        } catch (ApiRequestException e) {
            log.error("ApiRequestException: ", e);
            throw new CalendarEventException(e.getExceptionPayload());
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException: ", e);
            throw new CalendarEventException(ExceptionPayload.builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body("Couldn't parse request body for event creation")
                    .source(SourceType.API)
                    .build());
        }
    }

    public void update(String eventId, UpdateEventRequest updateEventRequest) {
        String token = authService.getToken();
        String path = buildUpdateEventPath(eventId, googleProps.getCalendarId());

        try {
            String jsonBody = objectMapper.writeValueAsString(updateEventRequest);

            Request request = new Request.Builder()
                    .addHeader(AUTHORIZATION, "%s %s".formatted(BEARER, token))
                    .url(path)
                    .put(RequestBody.create(jsonBody, MediaType.get(APPLICATION_JSON_VALUE)))
                    .build();

            okHttpUtil.handleApiRequest(SourceType.GOOGLE_CALENDAR, request);
        } catch (ApiRequestException e) {
            log.error("ApiRequestException: ", e);
            throw new CalendarEventException(e.getExceptionPayload());
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException: ", e);
            throw new CalendarEventException(ExceptionPayload.builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body("Couldn't parse request body for updating event")
                    .source(SourceType.API)
                    .build());
        }
    }
}
