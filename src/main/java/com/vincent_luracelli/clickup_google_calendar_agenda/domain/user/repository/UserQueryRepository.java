package com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.repository;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserQueryRepository  {

   List<User> findBy(Query query);

}
