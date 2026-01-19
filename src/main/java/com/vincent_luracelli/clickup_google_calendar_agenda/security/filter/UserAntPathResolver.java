package com.vincent_luracelli.clickup_google_calendar_agenda.security.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Arrays;

@Component
public class UserAntPathResolver implements AntPathResolver {

    @Value("${server.servlet.context-path:}")
    public String SERVER_CONTEXT_PATH;

    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public static final String[] PERMITTED_PATHS = new String[] {
            "/auth/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/api/click-up/webhook",
            "/click-up/webhook"
    };

    @Override
    public boolean isPermittedPath(String path) {
        return Arrays.stream(getPermittedPaths(true))
                .anyMatch(pr -> antPathMatcher.match(pr.formatted(SERVER_CONTEXT_PATH), path));
    }

    public String[] getPermittedPaths(boolean includeContextPath) {
        if (includeContextPath) {
            return Arrays.stream(PERMITTED_PATHS)
                    .map(path -> SERVER_CONTEXT_PATH.concat(path))
                    .toArray(String[]::new);
        }

        return PERMITTED_PATHS;
    }
}
