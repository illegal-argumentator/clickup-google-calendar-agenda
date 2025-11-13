package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.builder;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;

public class CalendarClientBuilder {

    private static final NetHttpTransport netHttpTransport = new NetHttpTransport();

    public static Calendar buildCalendarClient(String token) {
        GoogleCredential credential = new GoogleCredential().setAccessToken(token);

        return new Calendar.Builder(
                netHttpTransport,
                GsonFactory.getDefaultInstance(),
                credential
        ).build();
    }

}
