package ru.tbank.translator.dao.repository.impl;

import org.junit.jupiter.api.BeforeEach;
import ru.tbank.translator.dao.repository.TranslationRepositoryTest;

public class TranslationRepositoryJdbcImplTest extends TranslationRepositoryTest {
    @Override
    @BeforeEach
    protected void initRepository() {
        this.translationRepository = new TranslationRepositoryJdbcImpl(jdbcClient);
    }
}
