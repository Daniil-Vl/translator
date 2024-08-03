package ru.tbank.translator.dao.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import ru.tbank.translator.dao.repository.UserRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryJdbcImpl implements UserRepository {

    private final JdbcClient jdbcClient;

    @Override
    public void addUser(String userIp) {
        jdbcClient
                .sql("INSERT INTO users (ip) VALUES (?:cidr)")
                .param(userIp)
                .update();
    }

    @Override
    public boolean isExists(String userIp) {
        Optional<String> optionalUser = jdbcClient
                .sql("SELECT * FROM users WHERE ip = ?:cidr")
                .param(userIp)
                .query(String.class)
                .optional();

        return optionalUser.isPresent();
    }
}
