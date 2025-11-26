package com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.common.type.UserRole;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.common.type.UserStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
@Builder(toBuilder = true)
public class User implements UserDetails {

    @Id
    private String id;

    private String calendarTokenId;

    private String clickUpTokenId;

    @NotNull(message = "Email is required.")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Please provide a valid email address in the format: username@domain.com.")
    private String email;

    @NotNull(message = "Role is required.")
    private UserRole role;

    @NotNull(message = "Status is required.")
    private UserStatus status;

    @NotNull(message = "Password is required.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$", message = "Please provide a valid password in the format: Qwerty1234@.")
    private String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(role);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

}

