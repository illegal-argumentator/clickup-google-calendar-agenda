package com.vincent_luracelli.clickup_google_calendar_agenda.common.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WaitUtil {

    public static void waitSafely(long mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            log.error("Error waiting ", e);
            Thread.currentThread().interrupt();
        }
    }
}
