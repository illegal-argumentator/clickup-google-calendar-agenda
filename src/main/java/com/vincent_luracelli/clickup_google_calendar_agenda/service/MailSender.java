package com.vincent_luracelli.clickup_google_calendar_agenda.service;

public interface MailSender {

    void sendMail(String subject, String to, String text);

}
