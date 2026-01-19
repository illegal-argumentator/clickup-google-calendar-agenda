package com.vincent_luracelli.clickup_google_calendar_agenda.common.util.tries;

public interface TryFunction<T,R> {

    R apply(T t) throws Exception;
}
