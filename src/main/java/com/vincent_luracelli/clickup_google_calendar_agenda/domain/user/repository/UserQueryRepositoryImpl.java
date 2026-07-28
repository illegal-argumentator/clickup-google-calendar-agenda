package com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.repository;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class UserQueryRepositoryImpl implements UserQueryRepository {

    private final MongoTemplate mongoTemplate;

    public UserQueryRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<User> findBy(Query query) {
        return mongoTemplate.find(query, User.class);
    }
}
