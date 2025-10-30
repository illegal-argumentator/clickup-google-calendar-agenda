package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.constants.ClickUpPaths;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.TeamsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.google.auth.http.AuthHttpConstants.AUTHORIZATION;
import static com.vincent_luracelli.clickup_google_calendar_agenda.common.util.OkHttpUtil.buildResponseBodyOrThrow;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClickUpClient {

    @Value("${clickup.api_key}")
    private String CLICKUP_API_KEY;

    private final OkHttpClient okHttpClient;

    private final ObjectMapper objectMapper;

    public TeamsResponse findTeams() {
        try {
            String path = ClickUpPaths.TEAMS.getPath();

            Request request = new Request.Builder()
                    .addHeader(AUTHORIZATION, CLICKUP_API_KEY)
                    .url(path)
                    .build();

            Response response = okHttpClient.newCall(request).execute();
            String jsonResponse = buildResponseBodyOrThrow(response, "Click up client error: " + response.code() + " - " + response.message());

            response.close();
            return objectMapper.readValue(jsonResponse, TeamsResponse.class);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Exception while retrieving teams from click up");
        }
    }
}
