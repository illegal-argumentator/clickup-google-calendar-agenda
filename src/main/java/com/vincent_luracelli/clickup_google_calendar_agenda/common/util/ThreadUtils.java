package com.vincent_luracelli.clickup_google_calendar_agenda.common.util;

import java.util.Random;

public class ThreadUtils {
    private static final Random RANDOM = new Random();
    private ThreadUtils() {
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sleep(long millis, long additionMs ) {
        try {
            Thread.sleep(millis + RANDOM.nextLong(additionMs));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static Runnable sleepRunnable(long millis) {
        return () -> sleep(millis);
    }
}
