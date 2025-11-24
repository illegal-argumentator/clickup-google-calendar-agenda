package com.vincent_luracelli.clickup_google_calendar_agenda.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RunnerConfig implements CommandLineRunner {

    @Value("${server.host}")
    private String SERVER_HOST;

    @Value("${server.port}")
    private Integer SERVER_PORT;

    @Value("${server.servlet.context-path:}")
    private String SERVER_CONTEXT_PATH;

    private static final String SERVER_URL_TEMPLATE = "http://%s:%d%s/swagger-ui/index.html";

    @Override
    public void run(String... args) {
        log.info("Swagger UI: {}", SERVER_URL_TEMPLATE.formatted(SERVER_HOST, SERVER_PORT, SERVER_CONTEXT_PATH));
    }

}
