package ru.tbank.translator.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tbank.translator.client.TranslateApiClient;
import ru.tbank.translator.configuration.ApplicationConfig;

import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TranslationServiceImplTest {

    @Mock
    private TranslateApiClient translateApiClient;

    @Mock
    private ApplicationConfig applicationConfig;

    private TranslationServiceImpl translationServiceImpl;

    void initMockedTranslateMethod() {
        Mockito.when(translateApiClient.translateWord("Hello", "en", "ru")).thenReturn("Привет");
        Mockito.when(translateApiClient.translateWord("world", "en", "ru")).thenReturn("мир");
    }

    void initMockedDetectLanguageMethod() {
        Mockito.when(translateApiClient.detectLanguage("Hello world")).thenReturn("en");
    }

    void initMockedApplicationConfig() {
        Mockito.when(applicationConfig.threadsNumber()).thenReturn(10);
    }

    @BeforeEach
    void initTranslationService() {
        initMockedTranslateMethod();
        initMockedApplicationConfig();
        translationServiceImpl = new TranslationServiceImpl(translateApiClient, applicationConfig);
    }

    @Test
    void givenTextAndBothLanguages_whenTranslateSentence_thenTranslateTextSuccessfully() throws ExecutionException, InterruptedException {
        String text = "Hello world";
        String expectedTranslatedText = "Привет мир";
        String sourceLanguage = "en";
        String targetLanguage = "ru";

        String translatedText = translationServiceImpl.translateSentence(text, sourceLanguage, targetLanguage);
        assertThat(translatedText).isEqualTo(expectedTranslatedText);
    }

    @Test
    void givenTextAndTargetLanguage_whenTranslateSentence_thenDetectLanguageAndTranslateSuccessfully() throws ExecutionException, InterruptedException {
        initMockedDetectLanguageMethod();

        String text = "Hello world";
        String expectedTranslatedText = "Привет мир";
        String targetLanguage = "ru";

        String translatedText = translationServiceImpl.translateSentence(text, targetLanguage);
        assertThat(translatedText).isEqualTo(expectedTranslatedText);
    }
}