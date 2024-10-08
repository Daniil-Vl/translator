package ru.tbank.translator.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.tbank.translator.client.TranslateApiClient;
import ru.tbank.translator.configuration.ApplicationConfig;
import ru.tbank.translator.dao.repository.TranslationRepository;
import ru.tbank.translator.dto.yandex_translate.TranslateResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TranslationServiceImplTest {

    @Mock
    private TranslateApiClient translateApiClient;

    @Mock
    private ApplicationConfig applicationConfig;

    @Mock
    private TranslationRepository translationRepository;

    private TranslationServiceImpl translationServiceImpl;

    void initMockedTranslateMethod() {
        Mockito.when(
                translateApiClient.translateWord("Hello", "en", "ru")
        ).thenReturn(
                CompletableFuture.completedFuture(
                        new TranslateResponse("Привет", "en", "ru")
                )
        );

        Mockito.when(
                translateApiClient.translateWord("world", "en", "ru")
        ).thenReturn(
                CompletableFuture.completedFuture(
                        new TranslateResponse("мир", "en", "ru")
                )
        );

        Mockito.when(
                translateApiClient.translateWord("Hello", null, "ru")
        ).thenReturn(
                CompletableFuture.completedFuture(
                        new TranslateResponse("Привет", "en", "ru")
                )
        );

        Mockito.when(
                translateApiClient.translateWord("world", null, "ru")
        ).thenReturn(
                CompletableFuture.completedFuture(
                        new TranslateResponse("мир", "en", "ru")
                )
        );
    }

    void initMockedDetectLanguageMethod() {
        Mockito.when(
                translateApiClient.detectLanguage("Hello world")
        ).thenReturn(
                CompletableFuture.completedFuture("en")
        );
    }

    void initMockedApplicationConfig() {
        Mockito.when(applicationConfig.threadsNumber()).thenReturn(10);
    }

    @BeforeEach
    void initTranslationService() {
        initMockedTranslateMethod();
        initMockedApplicationConfig();
        translationServiceImpl = new TranslationServiceImpl(translateApiClient, translationRepository);
    }

    @Test
    void givenTextAndBothLanguages_whenTranslateSentence_thenTranslateTextSuccessfully() throws ExecutionException, InterruptedException {
        String text = "Hello world";
        String expectedTranslatedText = "Привет мир";
        String sourceLanguage = "en";
        String targetLanguage = "ru";

        TranslateResponse translatedText = translationServiceImpl.translateSentence(text, sourceLanguage, targetLanguage);
        assertThat(translatedText.translatedText()).isEqualTo(expectedTranslatedText);
    }

    @Test
    void givenTextAndTargetLanguage_whenTranslateSentence_thenDetectLanguageAndTranslateSuccessfully() throws ExecutionException, InterruptedException {
        initMockedDetectLanguageMethod();

        String text = "Hello world";
        String expectedTranslatedText = "Привет мир";
        String targetLanguage = "ru";
        String expectedSourceLanguage = "en";

        TranslateResponse translatedText = translationServiceImpl.translateSentence(text, targetLanguage);
        assertThat(translatedText.translatedText()).isEqualTo(expectedTranslatedText);
        assertThat(translatedText.sourceLanguage()).isEqualTo(expectedSourceLanguage);
    }
}