package com.vincent_luracelli.clickup_google_calendar_agenda.common.util.tries;

public interface TrySupplier<T> {

    T get() throws Exception;
}
