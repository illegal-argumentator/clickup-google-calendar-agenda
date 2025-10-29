package com.vincent_luracelli.clickup_google_calendar_agenda.common.util;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;

import java.io.IOException;

@Slf4j
public class OkHttpUtil {

    public static String buildResponseBodyOrThrow(Response response, String message) throws IOException {
        String responseBody = response.body().string();
        if (!response.isSuccessful()) {
            log.error("Failed to proceed request: {} - {}, Body: {}", response.code(), response.message(), responseBody);
            throw new IOException(message);
        }
        return responseBody;
    }

}
