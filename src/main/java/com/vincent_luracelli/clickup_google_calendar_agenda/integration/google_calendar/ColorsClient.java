package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.type.SourceType;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.util.OkHttpUtil;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.service.CalendarOAuthTokenService;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.ColorsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import static com.google.auth.http.AuthHttpConstants.AUTHORIZATION;
import static com.google.auth.http.AuthHttpConstants.BEARER;
import static com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.builder.ColorsPathBuilder.buildColorsPath;

@Slf4j
@Service
@RequiredArgsConstructor
public class ColorsClient {

    private final OkHttpUtil okHttpUtil;

    private final CalendarOAuthTokenService calendarOAuthTokenService;

    @Cacheable("calendar_colors")
    public ColorsResponse getColors() {
        String token = calendarOAuthTokenService.requireValidToken();
        String path = buildColorsPath();

        Request request = new Request.Builder()
                .addHeader(AUTHORIZATION, "%s %s".formatted(BEARER, token))
                .url(path)
                .get()
                .build();

        return okHttpUtil.handleApiRequest(SourceType.GOOGLE_CALENDAR, request, ColorsResponse.class);
    }

}
