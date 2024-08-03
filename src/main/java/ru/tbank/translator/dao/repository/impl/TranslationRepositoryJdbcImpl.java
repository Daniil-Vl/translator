package ru.tbank.translator.dao.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import ru.tbank.translator.dao.model.Translation;
import ru.tbank.translator.dao.repository.TranslationRepository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TranslationRepositoryJdbcImpl implements TranslationRepository {

    private final JdbcClient jdbcClient;

    @Override
    public void addTranslation(Translation translation) {
        jdbcClient
                .sql("""
                        INSERT INTO translation (source_text, translated_text, source_language, target_language, user_ip, translation_date_time)
                        VALUES (?, ?, ?, ?, ?::cidr ,?)
                        """)
                .param(translation.sourceText())
                .param(translation.translatedText())
                .param(translation.sourceLanguage())
                .param(translation.targetLanguage())
                .param(translation.userIp())
                .param(translation.translationDateTime())
                .update();
    }

    @Override
    public List<Translation> getTranslationHistoryByIp(String userIp) {
        return jdbcClient
                .sql("SELECT * FROM translation WHERE user_ip = ?::cidr")
                .param(userIp)
                .query(Translation.class)
                .list();
    }
}
