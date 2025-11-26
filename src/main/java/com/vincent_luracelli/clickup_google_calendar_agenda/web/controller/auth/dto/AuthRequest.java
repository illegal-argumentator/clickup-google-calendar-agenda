package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.auth.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record AuthRequest(
        @NotNull(message = "Email is required.")
        @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Please provide a valid email address in the format: username@domain.com.")
        String email,

        @NotNull(message = "Password is required.")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$", message = "Please provide a valid password in the format: Qwerty1234@.")
        String password) {
}
