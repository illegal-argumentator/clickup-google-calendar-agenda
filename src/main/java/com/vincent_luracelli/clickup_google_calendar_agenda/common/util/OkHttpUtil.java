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
//        log.info("API response: {}", responseContent);

        try {
            return objectMapper.readValue(responseContent, responseTarget);
        } catch (IOException e) {
            logOkHttpUtilError(e.getMessage());
            throw new ApiException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), sourceType);
        }
    }

    public String handleApiRequest(SourceType sourceType, Request request) {
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String message = objectMapper.readValue(response.body().string(), Object.class).toString();
                logOkHttpUtilError(message);
                throw new ApiException(message, HttpStatus.INTERNAL_SERVER_ERROR.value(), sourceType);
            }

            return response.body().string();
        } catch (IOException e) {
            logOkHttpUtilError(e.getMessage());
            throw new ApiException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), sourceType);
        }
    }

    private void logOkHttpUtilError(String message) {
        log.error("OkHttpUtil: {}", message);
    }

}
