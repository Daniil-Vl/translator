package ru.tbank.translator.dao.repository.impl;

import org.junit.jupiter.api.BeforeEach;
import ru.tbank.translator.dao.repository.UserRepositoryTest;

public class UserRepositoryJdbcImplTest extends UserRepositoryTest {
    @Override
    @BeforeEach
    protected void initRepository() {
        this.userRepository = new UserRepositoryJdbcImpl(jdbcClient);
    }
}
