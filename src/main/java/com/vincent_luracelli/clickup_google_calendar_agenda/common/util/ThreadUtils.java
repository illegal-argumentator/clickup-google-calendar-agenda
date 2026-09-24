package com.vincent_luracelli.clickup_google_calendar_agenda.common.util;

public class ThreadUtils {
    private ThreadUtils() {
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
}
