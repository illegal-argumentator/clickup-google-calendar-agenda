package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.type.SourceType;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.util.OkHttpUtil;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.calendar_token.model.CalendarToken;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.service.CalendarAuthService;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.ColorsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import static com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.builder.ColorsPathBuilder.buildColorsPath;
import static com.vincent_luracelli.clickup_google_calendar_agenda.security.common.constants.AuthConstants.AUTHORIZATION_HEADER;
import static com.vincent_luracelli.clickup_google_calendar_agenda.security.common.constants.AuthConstants.BEARER_PREFIX;

@Slf4j
@Service
@RequiredArgsConstructor
public class ColorsClient {

    private final OkHttpUtil okHttpUtil;

    private final CalendarAuthService calendarAuthService;

    @Cacheable("calendar_colors")
    public ColorsResponse getColors(User user) {
        CalendarToken token = calendarAuthService.requireAccessTokenByUser(user);
        String path = buildColorsPath();

        Request request = new Request.Builder()
                .addHeader(AUTHORIZATION_HEADER, "%s %s".formatted(BEARER_PREFIX, token.getAccessToken()))
                .url(path)
                .get()
                .build();

        return okHttpUtil.handleApiRequest(SourceType.GOOGLE_CALENDAR, request, ColorsResponse.class);
    }
}
