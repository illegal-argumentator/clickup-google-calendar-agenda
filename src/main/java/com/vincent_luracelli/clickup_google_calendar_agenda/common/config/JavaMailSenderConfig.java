package com.vincent_luracelli.clickup_google_calendar_agenda.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class JavaMailSenderConfig {

    private final MailProperties mailProperties;

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(mailProperties.getHost());
        mailSender.setPort(mailProperties.getPort());
        mailSender.setProtocol(mailProperties.getProperties().getProtocol());

        mailSender.setUsername(mailProperties.getUsername());
        mailSender.setPassword(mailProperties.getPassword());

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", mailProperties.getProperties().getAuth());
        properties.put("mail.smtp.starttls.enable", mailProperties.getProperties().getStarttls().getEnabled());
        properties.put("mail.smtp.starttls.required", true);
        mailSender.setJavaMailProperties(properties);

        return mailSender;
    }
}
