package com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.service;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.exception.EntityAlreadyExistsException;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.exception.EntityNotFoundException;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void save(User user) {
        findByEmail(user.getEmail())
                .ifPresent(userResponse -> {throw new EntityAlreadyExistsException("User already exists.");});

        userRepository.save(user);
    }

    public void update(String email, User updateUser) {
        User user = findByEmailOrThrow(email);

        Optional.ofNullable(updateUser.getCalendarTokenId()).ifPresent(user::setCalendarTokenId);
        Optional.ofNullable(updateUser.getClickUpTokenId()).ifPresent(user::setClickUpTokenId);

        userRepository.save(user);
    }

    public Optional<User> findByEmail(@NotNull String email) {
        return userRepository.findByEmail(email);
    }

    public User findByEmailOrThrow(@NotNull String email) {
        return findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found."));
    }

    public List<User> findAllClickUpAuthorized() {
        Query query = new Query().addCriteria(
                Criteria.where("clickUpTokenId").exists(true).ne(null)
        );

        return userRepository.findBy(query);
    }
}
