package ru.tbank.translator.client.impl;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import ru.tbank.translator.client.TranslateApiClient;
import ru.tbank.translator.configuration.ApplicationConfig;
import ru.tbank.translator.dto.yandex_translate.*;
import ru.tbank.translator.exceptions.LanguageNotDetectedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


class YandexTranslateApiClientImplTest {

    private static final Path TEST_FILES_PATH = Path.of("src", "test", "resources", "client", "yandex");

    private static final String TEXT = "Hello";
    private static final String SOURCE_LANGUAGE = "en";
    private static final String TARGET_LANGUAGE = "ru";
    private static final String INVALID_LANGUAGE = "fu";
    private static final String TRANSLATED_TEXT = "Привет";

    private static final String TRANSLATE_URL = "/translate-test";
    private static final String DETECT_URL = "/detect-test";

    private static WireMockServer wireMockServer;

    private static ApplicationConfig mockedApplicationConfig;
    private static TranslateApiClient yandexTranslateApiClient;

    static void initServerStubs() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        // Stub for request with both valid languages
        TranslateRequest translateRequest = new TranslateRequest(SOURCE_LANGUAGE, TARGET_LANGUAGE, List.of(TEXT));
        YandexTranslateResponseDto yandexTranslateResponseDto = new YandexTranslateResponseDto(List.of(
                new YandexTranslateResponseDto.TranslationResult(TRANSLATED_TEXT, null)));
        createStub(
                TRANSLATE_URL,
                objectMapper.writeValueAsString(translateRequest),
                HttpStatus.OK,
                objectMapper.writeValueAsString(yandexTranslateResponseDto)
        );

        // Stub for valid target language
        translateRequest = new TranslateRequest(null, TARGET_LANGUAGE, List.of(TEXT));
        yandexTranslateResponseDto = new YandexTranslateResponseDto(List.of(
                new YandexTranslateResponseDto.TranslationResult(TRANSLATED_TEXT, SOURCE_LANGUAGE)));
        createStub(
                TRANSLATE_URL,
                objectMapper.writeValueAsString(translateRequest),
                HttpStatus.OK,
                objectMapper.writeValueAsString(yandexTranslateResponseDto)
        );

        // Stub for detect language request
        DetectLanguageRequest detectLanguageRequest = new DetectLanguageRequest(TEXT);
        DetectLanguageResponse detectLanguageResponse = new DetectLanguageResponse(SOURCE_LANGUAGE);
        createStub(
                DETECT_URL,
                objectMapper.writeValueAsString(detectLanguageRequest),
                HttpStatus.OK,
                objectMapper.writeValueAsString(detectLanguageResponse)
        );

        // Stub for request with invalid source language
        translateRequest = new TranslateRequest(INVALID_LANGUAGE, TARGET_LANGUAGE, List.of(TEXT));
        createStub(
                TRANSLATE_URL,
                objectMapper.writeValueAsString(translateRequest),
                HttpStatus.BAD_REQUEST,
                Files.readString(TEST_FILES_PATH.resolve("error_response_with_invalid_source_language.txt"))
        );

        // Stub for request with invalid target language
        translateRequest = new TranslateRequest(SOURCE_LANGUAGE, INVALID_LANGUAGE, List.of(TEXT));
        createStub(
                TRANSLATE_URL,
                objectMapper.writeValueAsString(translateRequest),
                HttpStatus.BAD_REQUEST,
                Files.readString(TEST_FILES_PATH.resolve("error_response_with_invalid_target_language.txt"))
        );
    }

    private static void createStub(String url, String requestBody, HttpStatus responseHttpStatus, String responseBody) {
        wireMockServer.stubFor(
                post(url)
                        .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON_VALUE))
                        .withHeader(AUTHORIZATION, equalTo("Api-Key " + mockedApplicationConfig.yandexApiKey()))
                        .withRequestBody(equalToJson(requestBody))
                        .willReturn(
                                aResponse()
                                        .withStatus(responseHttpStatus.value())
                                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                        .withBody(responseBody)
                        )
        );
    }

    @BeforeAll
    static void initServerAndClient() throws IOException {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();

        mockedApplicationConfig = new ApplicationConfig(
                wireMockServer.url(TRANSLATE_URL),
                wireMockServer.url(DETECT_URL),
                "api-key",
                1
        );

        yandexTranslateApiClient = new YandexTranslateApiClientImpl(
                new RestTemplate(),
                mockedApplicationConfig
        );

        initServerStubs();
    }

    @Test
    void givenTextAndBothLanguages_whenTranslateWord_thenSuccessfullyTranslated() {
        TranslateResponse translateResponse = yandexTranslateApiClient.translateWord(TEXT, SOURCE_LANGUAGE, TARGET_LANGUAGE);

        assertThat(translateResponse.translatedText()).isEqualTo(TRANSLATED_TEXT);
    }

    @Test
    void givenTextAndTargetLanguage_whenTranslateWord_thenSuccessfullyTranslatedAndDetectSourceLanguage() {
        TranslateResponse translateResponse = yandexTranslateApiClient.translateWord(TEXT, TARGET_LANGUAGE);

        assertThat(translateResponse.translatedText()).isEqualTo(TRANSLATED_TEXT);
        assertThat(translateResponse.sourceLanguage()).isEqualTo(SOURCE_LANGUAGE);
    }

    @Test
    void givenText_whenDetectLanguage_thenSuccessfullyDetectsLanguage() {
        String detectedLanguage = yandexTranslateApiClient.detectLanguage(TEXT);

        assertThat(detectedLanguage).isEqualTo(SOURCE_LANGUAGE);
    }

    @Test
    void givenInvalidLanguage_whenDetectLanguage_thenSuccessfullyDetectsLanguage() {
        LanguageNotDetectedException sourceLanguageNotDetectedExc = catchThrowableOfType(
                () -> yandexTranslateApiClient.translateWord(TEXT, INVALID_LANGUAGE, TARGET_LANGUAGE),
                LanguageNotDetectedException.class
        );
        LanguageNotDetectedException targetLanguageNotDetectedExc = catchThrowableOfType(
                () -> yandexTranslateApiClient.translateWord(TEXT, SOURCE_LANGUAGE, INVALID_LANGUAGE),
                LanguageNotDetectedException.class
        );

        assertThat(sourceLanguageNotDetectedExc.getMessage()).isEqualTo(LanguageNotDetectedException.SOURCE_LANGUAGE_NOT_DETECTED_MESSAGE);
        assertThat(targetLanguageNotDetectedExc.getMessage()).isEqualTo(LanguageNotDetectedException.TARGET_LANGUAGE_NOT_DETECTED_MESSAGE);
    }
}