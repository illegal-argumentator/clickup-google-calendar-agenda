package com.vincent_luracelli.clickup_google_calendar_agenda.common.exception.handler;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.config.MailProperties;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.exception.ExceptionResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.type.SourceType;
import com.vincent_luracelli.clickup_google_calendar_agenda.service.MailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExceptionHandlerService {

    private final MailSender mailSender;
    private final MailProperties mailProperties;

    private static final int MAX_MESSAGE_SIZE = 5_000;

    public ExceptionResponse handleInternalServerError(Exception e) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .source(SourceType.API)
                .body(e.getMessage())
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        report("Something went wrong on API side: %s".formatted(normalizeMessage(e.getMessage())), mailProperties.getTo());

        return exceptionResponse;
    }

    private String normalizeMessage(String message) {
        if (message.length() > MAX_MESSAGE_SIZE) return message.substring(0, MAX_MESSAGE_SIZE) + "...";
        return message;
    }

    private void report(String content, List<String> to) {
        for (String s : to) {
            mailSender.sendMail("Internal server error.", content, s);
        }
    }

}
