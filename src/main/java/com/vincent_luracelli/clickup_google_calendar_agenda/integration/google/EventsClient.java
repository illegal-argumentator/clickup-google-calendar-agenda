package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.common.config.GoogleProps;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.common.dto.EventResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.common.dto.EventsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.vincent_luracelli.clickup_google_calendar_agenda.common.util.OkHttpUtil.buildResponseBodyOrThrow;
import static com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.common.builder.EventsPathBuilder.buildGetEventPath;
import static com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.common.builder.EventsPathBuilder.buildGetEventsPath;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventsClient {

    private final OkHttpClient okHttpClient;

    private final ObjectMapper objectMapper;

    private final GoogleProps googleProps;

    private final AuthService authService;

    public EventResponse get(String eventId) {
        Request request = new Request.Builder()
                .url(buildGetEventPath(googleProps.getCalendarId(), eventId))
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            String responseBody = buildResponseBodyOrThrow(response, "Events client error: " + response.code() + " - " + response.message());
            return objectMapper.readValue(responseBody, EventResponse.class);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Exception while getting events from google calendar");
        }
    }

    public EventsResponse getAll() {
        String token = authService.getToken();

        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + token)
                .url(buildGetEventsPath(googleProps.getCalendarId()))
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            String responseBody = buildResponseBodyOrThrow(response, "Events client error: " + response.code() + " - " + response.message());
            return objectMapper.readValue(responseBody, EventsResponse.class);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Exception while getting events from google calendar");
        }
    }
}
