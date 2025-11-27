package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.exception.ApiException;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.type.SourceType;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.util.OkHttpUtil;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.constants.ClickUpPaths;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.AccessTokenRequest;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.AccessTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClickUpOAuthClient {

    private final OkHttpUtil okHttpUtil;

    private final ObjectMapper objectMapper;

    public AccessTokenResponse getAccessToken(AccessTokenRequest accessTokenRequest) {
        String path = ClickUpPaths.OAUTH_TOKEN.getPath();

        try {
            String jsonBody = objectMapper.writeValueAsString(accessTokenRequest);
            Request request = new Request.Builder()
                    .url(path)
                    .post(RequestBody.create(jsonBody, MediaType.get(APPLICATION_JSON_VALUE)))
                    .build();

            return okHttpUtil.handleApiRequest(SourceType.CLICK_UP, request, AccessTokenResponse.class);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException: ", e);
            throw new ApiException("Couldn't parse request body for getting access token", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
