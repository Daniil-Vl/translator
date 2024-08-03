package ru.tbank.translator.dao.repository;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import ru.tbank.translator.DomainAbstractTest;
import ru.tbank.translator.dao.model.Language;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public abstract class LanguageRepositoryTest extends DomainAbstractTest {

    protected LanguageRepository languageRepository;

    protected abstract void initRepository();

    @Test
    void givenLanguage_whenRepositoryAddLanguage_thenLanguageSuccessfullyAdded() {
        Language language = new Language("es", "Spanish");
        languageRepository.addLanguage(language);

        Optional<Language> languageOptional = jdbcClient
                .sql("SELECT * FROM language WHERE name = ?")
                .param(language.name())
                .query(Language.class)
                .optional();

        assertThat(languageOptional).isPresent();
        assertThat(languageOptional.get()).isEqualTo(language);
    }

    @Test
    void givenExistingLanguage_whenRepositoryAddLanguage_thenExceptionThrown() {
        Language language = new Language("en", "English");

        assertThatThrownBy(() -> languageRepository.addLanguage(language)).isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    void givenExistingLanguage_whenRepositoryRemoveLanguage_thenLanguageSuccessfullyRemoved() {
        Language language = languageRepository.getLanguage("en").get();
        languageRepository.removeLanguage(language.name());

        Optional<Language> languageOptional = jdbcClient
                .sql("SELECT * FROM language WHERE name = ?")
                .param(language.name())
                .query(Language.class)
                .optional();

        assertThat(languageOptional).isEmpty();
    }

    @Test
    void givenExistingLanguage_whenRepositoryGetLanguage_thenReturnsLanguageSuccessfully() {
        Language language = languageRepository.getLanguage("en").get();
        Optional<Language> languageOptional = languageRepository.getLanguage(language.name());

        assertThat(languageOptional).isPresent();
        assertThat(languageOptional.get()).isEqualTo(language);
    }

    @Test
    void givenNonExistingLanguage_whenRepositoryGetLanguage_thenReturnsEmptyOptional() {
        Language language = languageRepository.getLanguage("en").get();

        languageRepository.removeLanguage("en");
        Optional<Language> languageOptional = languageRepository.getLanguage(language.name());

        assertThat(languageOptional).isEmpty();
    }

    @Test
    void whenRepositoryGetLanguages_thenReturnsAllAvailableLanguages() {
        Language ru = languageRepository.getLanguage("ru").get();
        Language en = languageRepository.getLanguage("en").get();

        List<Language> actualLanguages = languageRepository.getLanguages();

        assertThat(actualLanguages).containsExactlyInAnyOrder(ru, en);
    }
}