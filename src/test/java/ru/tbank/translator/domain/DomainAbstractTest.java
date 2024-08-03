package ru.tbank.translator.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import ru.tbank.translator.TranslatorApplicationTests;
import ru.tbank.translator.dto.TranslationDto;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public abstract class DomainAbstractTest extends TranslatorApplicationTests {
    @Autowired
    protected JdbcClient jdbcClient;

    protected TranslationDto translationDto = new TranslationDto(
            "Hello world",
            "Привет мир",
            "en",
            "ru",
            "127.0.0.1/32",
            OffsetDateTime.of(2024, 1, 1, 1, 1, 1, 0, ZoneOffset.UTC));

    @DynamicPropertySource
    static void liquibaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.liquibase.enabled", () -> "true");
        registry.add("spring.liquibase.change-log", () -> "classpath:db/changelog/master.yaml");
    }

    @BeforeEach
    public void initDB() {
        jdbcClient
                .sql("INSERT INTO users VALUES ('127.0.0.1')")
                .update();

        jdbcClient
                .sql("INSERT INTO language (name, fullname) VALUES ('en', 'english'), ('ru', 'russian')")
                .update();

        jdbcClient
                .sql("""
                        INSERT INTO translation
                        (source_text, translated_text, source_language, target_language, user_ip, translation_date_time)
                        VALUES (?, ?, ?, ?, ?::cidr, ?)
                        """)
                .param(translationDto.sourceText())
                .param(translationDto.translatedText())
                .param(translationDto.sourceLanguage())
                .param(translationDto.targetLanguage())
                .param(translationDto.userIp())
                .param(translationDto.translationDateTime())
                .update();


    }

    @AfterEach
    public void resetDB() {
        jdbcClient.sql("DELETE FROM translation").update();
        jdbcClient.sql("DELETE FROM language").update();
        jdbcClient.sql("DELETE FROM users").update();
    }
}
