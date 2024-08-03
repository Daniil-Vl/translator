package ru.tbank.translator.dao.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import ru.tbank.translator.dao.model.Language;
import ru.tbank.translator.dao.repository.LanguageRepository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LanguageRepositoryJdbcImpl implements LanguageRepository {
    private final JdbcClient jdbcClient;

    @Override
    public void addLanguage(Language language) {
        jdbcClient
                .sql("INSERT INTO language (name, fullname) VALUES (?, ?)")
                .param(language.name())
                .param(language.fullname())
                .update();
    }

    @Override
    public void removeLanguage(String name) {
        jdbcClient
                .sql("DELETE FROM language WHERE name = ?")
                .param(name)
                .update();
    }

    @Override
    public List<Language> getLanguages() {
        return jdbcClient
                .sql("SELECT * FROM language")
                .query(Language.class)
                .list();
    }
}
