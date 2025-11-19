package com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.repository;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

}
