package ru.tbank.translator.dao.repository;

import org.junit.jupiter.api.Test;
import ru.tbank.translator.DomainAbstractTest;
import ru.tbank.translator.dao.model.Translation;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class TranslationRepositoryTest extends DomainAbstractTest {

    private final Translation translation = new Translation(
            translationDto.sourceText(),
            translationDto.translatedText(),
            translationDto.sourceLanguage(),
            translationDto.targetLanguage(),
            translationDto.userIp(),
            translationDto.translationDateTime()
    );

    protected TranslationRepository translationRepository;

    protected abstract void initRepository();

    @Test
    void givenTranslation_whenRepositoryAddTranslation_thenTranslationSuccessfullySaved() {
        Optional<Translation> optionalTranslation = jdbcClient
                .sql("SELECT * FROM translation WHERE user_ip = ?::cidr")
                .param(translation.userIp())
                .query(Translation.class)
                .optional();

        assertThat(optionalTranslation).isPresent();
        assertThat(optionalTranslation.get()).isEqualTo(translation);
    }

    @Test
    void givenUserIp_whenRepositoryGetTranslationHistoryByIp_thenReturnEntireHistorySuccessfully() {
        Translation first = new Translation(
                "first",
                "первый",
                "en",
                "ru",
                "127.0.0.10/32",
                OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)
        );
        Translation second = new Translation(
                "второй",
                "second",
                "ru",
                "en",
                "127.0.0.10/32",
                OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)
        );
        Translation third = new Translation(
                "third",
                "третий",
                "en",
                "ru",
                "127.0.0.20/32",
                OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)
        );

        jdbcClient.sql("INSERT INTO users VALUES ('127.0.0.10'), ('127.0.0.20')").update();

        translationRepository.addTranslation(first);
        translationRepository.addTranslation(second);
        translationRepository.addTranslation(third);

        List<Translation> actual = translationRepository.getTranslationHistoryByIp("127.0.0.10/32");

        assertThat(actual).containsExactlyInAnyOrder(first, second);
    }
}