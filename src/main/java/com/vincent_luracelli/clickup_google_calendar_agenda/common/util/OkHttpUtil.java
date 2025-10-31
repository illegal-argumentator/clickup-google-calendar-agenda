package com.vincent_luracelli.clickup_google_calendar_agenda.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.exception.ApiRequestException;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.exception.ExceptionPayload;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.type.SourceType;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.exception.ClickUpErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OkHttpUtil {

    private final ObjectMapper objectMapper;

    private final OkHttpClient okHttpClient;

    @Deprecated
    public static String buildResponseBodyOrThrow(Response response, String message) throws IOException {
        String responseBody = response.body().string();
        if (!response.isSuccessful()) {
            log.error("Failed to proceed request: {} - {}, Body: {}", response.code(), response.message(), responseBody);
            throw new IOException(message);
        }
        return responseBody;
    }

    public <T> T handleApiRequest(SourceType sourceType, Request request, Class<T> responseTarget) {
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                ClickUpErrorResponse clickUpErrorResponse = objectMapper.readValue(response.body().string(), ClickUpErrorResponse.class);
                throw new ApiRequestException(ExceptionPayload.builder()
                        .code(response.code())
                        .message(clickUpErrorResponse.err())
                        .source(sourceType)
                        .build());
            }

            return objectMapper.readValue(response.body().string(), responseTarget);
        } catch (IOException e) {
            ExceptionPayload exceptionPayload = ExceptionPayload.builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(e.getMessage())
                    .source(sourceType)
                    .build();
            throw new ApiRequestException(exceptionPayload);
        }
    }

}
