package ru.tbank.translator.domain.liquibase;

import org.junit.jupiter.api.Test;
import ru.tbank.translator.domain.DomainAbstractTest;
import ru.tbank.translator.dto.TranslationDto;

import static org.assertj.core.api.Assertions.assertThat;

public class LiquibaseTest extends DomainAbstractTest {

    @Test
    public void testUsersLiquibaseSchema() {
        String actual = jdbcClient
                .sql("SELECT * FROM users")
                .query(String.class)
                .single();

        String expected = "127.0.0.1/32";

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testLanguageLiquibaseSchema() {
        String actual = jdbcClient
                .sql("SELECT fullname FROM language WHERE name = 'en'")
                .query(String.class)
                .single();

        String expected = "english";

        assertThat(actual).isEqualTo(expected);
    }


    @Test
    public void testTranslationLiquibaseSchema() {
        TranslationDto actual = jdbcClient
                .sql("SELECT * FROM translation WHERE user_ip = '127.0.0.1'")
                .query(TranslationDto.class)
                .single();

        assertThat(actual).isEqualTo(translationDto);
    }

}
