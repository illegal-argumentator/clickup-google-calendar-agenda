package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.common.config.GoogleProps;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.common.dto.EventResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.common.dto.InsertEventsRequest;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.common.dto.UpdateEventRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.google.auth.http.AuthHttpConstants.AUTHORIZATION;
import static com.google.auth.http.AuthHttpConstants.BEARER;
import static com.vincent_luracelli.clickup_google_calendar_agenda.common.util.OkHttpUtil.buildResponseBodyOrThrow;
import static com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.common.builder.EventsPathBuilder.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventsClient {

    private final OkHttpClient okHttpClient;

    private final ObjectMapper objectMapper;

    private final GoogleProps googleProps;

    private final AuthService authService;

    public EventResponse insert(InsertEventsRequest insertEventsRequest) {
        try {
            String token = authService.getToken();
            String path = buildEventByCalendarIdPath(googleProps.getCalendarId());
            String jsonBody = objectMapper.writeValueAsString(insertEventsRequest);

            Request request = new Request.Builder()
                    .addHeader(AUTHORIZATION, "%s %s".formatted(BEARER, token))
                    .url(path)
                    .post(RequestBody.create(jsonBody, MediaType.get(APPLICATION_JSON_VALUE)))
                    .build();

            Response response = okHttpClient.newCall(request).execute();
            String jsonResponse = buildResponseBodyOrThrow(response, "Events client error: " + response.code() + " - " + response.message());

            response.close();
            return objectMapper.readValue(jsonResponse, EventResponse.class);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Exception while inserting event into google calendar");
        }
    }

    public void update(String eventId, UpdateEventRequest updateEventRequest) {
        try {
            String token = authService.getToken();
            String path = buildUpdateEventPath(eventId, googleProps.getCalendarId());
            String jsonBody = objectMapper.writeValueAsString(updateEventRequest);

            Request request = new Request.Builder()
                    .addHeader(AUTHORIZATION, "%s %s".formatted(BEARER, token))
                    .url(path)
                    .put(RequestBody.create(jsonBody, MediaType.get(APPLICATION_JSON_VALUE)))
                    .build();

            Response response = okHttpClient.newCall(request).execute();
            buildResponseBodyOrThrow(response, "Events client error: " + response.code() + " - " + response.message());
            response.close();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Exception while updating event in google calendar");
        }
    }
}
