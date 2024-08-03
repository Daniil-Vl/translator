package ru.tbank.translator.dao.repository.impl;

import org.junit.jupiter.api.BeforeEach;
import ru.tbank.translator.dao.repository.LanguageRepositoryTest;

public class LanguageRepositoryJdbcImplTest extends LanguageRepositoryTest {
    @Override
    @BeforeEach
    protected void initRepository() {
        this.languageRepository = new LanguageRepositoryJdbcImpl(jdbcClient);
    }
}
