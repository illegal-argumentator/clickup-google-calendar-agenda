package com.vincent_luracelli.clickup_google_calendar_agenda.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.exception.ApiException;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.type.SourceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OkHttpUtil {

    private final ObjectMapper objectMapper;

    private final OkHttpClient okHttpClient;

    public <T> T handleApiRequest(SourceType sourceType, Request request, Class<T> responseTarget) {
        String responseContent = handleApiRequest(sourceType, request);

        try {
            return objectMapper.readValue(responseContent, responseTarget);
        } catch (IOException e) {
            throw new ApiException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), sourceType);
        }
    }

    public String handleApiRequest(SourceType sourceType, Request request) {
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                // TODO make exception body handler both for click up and google to return structured response
                String message = objectMapper.readValue(response.body().string(), Object.class).toString();
                throw new ApiException(message, HttpStatus.INTERNAL_SERVER_ERROR.value(), sourceType);
            }

            return response.body().string();
        } catch (IOException e) {
            throw new ApiException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), sourceType);
        }
    }

}
