package com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.service;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.exception.UserAlreadyExistsException;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User save(User user) {
        findByEmail(user.getEmail()).ifPresent(userResponse -> {
            throw new UserAlreadyExistsException("User already exists.");
        });
        return userRepository.save(user);
    }

    public void updateUserCalendarToken(String email, String calendarTokenId) {
        User user = findByEmailOrThrow(email);
        user.setCalendarTokenId(calendarTokenId);
        userRepository.save(user);
    }

    public Optional<User> findByEmail(@NotNull String email) {
        return userRepository.findByEmail(email);
    }

    public User findByEmailOrThrow(@NotNull String email) {
        return findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found."));
    }
}
