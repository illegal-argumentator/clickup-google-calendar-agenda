package com.vincent_luracelli.clickup_google_calendar_agenda.security.utils;


import io.micrometer.common.util.StringUtils;

import static com.vincent_luracelli.clickup_google_calendar_agenda.security.common.constants.AuthConstants.BEARER_PREFIX;

public class JwtUtils {

    public static boolean isTokenFormatValid(String token) {
        return token != null && token.startsWith(BEARER_PREFIX);
    }

    public static String extractTokenWithoutBearer(String token) {
        if (StringUtils.isEmpty(token)) {
            throw new IllegalArgumentException("Token cannot be empty.");
        } else if (!token.startsWith(BEARER_PREFIX)) {
            throw new IllegalArgumentException("Token should start with bearer prefix.");
        }

        return token.substring(BEARER_PREFIX.length());
    }
}
