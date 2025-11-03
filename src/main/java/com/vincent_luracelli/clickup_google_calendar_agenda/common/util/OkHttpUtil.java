package com.vincent_luracelli.clickup_google_calendar_agenda.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.exception.ApiRequestException;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.exception.ExceptionPayload;
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
            ExceptionPayload exceptionPayload = ExceptionPayload.builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body(e.getMessage())
                    .source(sourceType)
                    .build();
            throw new ApiRequestException(exceptionPayload);
        }
    }

    public String handleApiRequest(SourceType sourceType, Request request) {
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new ApiRequestException(ExceptionPayload.builder()
                        .code(response.code())
                        // TODO make exception body handler both for click up and google to return structured response
                        .body(objectMapper.readValue(response.body().string(), Object.class).toString())
                        .source(sourceType)
                        .build());
            }

            return response.body().string();
        } catch (IOException e) {
            ExceptionPayload exceptionPayload = ExceptionPayload.builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body(e.getMessage())
                    .source(sourceType)
                    .build();
            throw new ApiRequestException(exceptionPayload);
        }
    }

}
