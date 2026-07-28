package com.vincent_luracelli.clickup_google_calendar_agenda.service.impls;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.config.MailProperties;
import com.vincent_luracelli.clickup_google_calendar_agenda.service.MailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SimpleMailSender implements MailSender {

    private final MailProperties mailProperties;

    private final JavaMailSender javaMailSender;

    @Override
    public void sendMail(String subject, String to, String text) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setSubject(subject);
        message.setText(text);
        message.setTo(to);
        message.setFrom(mailProperties.getFrom());
        message.setReplyTo(mailProperties.getReplyTo());

        javaMailSender.send(message);
    }

}
