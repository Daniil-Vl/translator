package ru.tbank.translator.client.impl;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import ru.tbank.translator.client.TranslateApiClient;
import ru.tbank.translator.configuration.ApplicationConfig;
import ru.tbank.translator.dto.yandex_translate.DetectLanguageRequest;
import ru.tbank.translator.dto.yandex_translate.DetectLanguageResponse;
import ru.tbank.translator.dto.yandex_translate.TranslateRequest;
import ru.tbank.translator.dto.yandex_translate.TranslateResponse;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


class YandexTranslateApiClientImplTest {

    private static TranslateApiClient yandexTranslateApiClient;

    private static TranslateRequest translateRequest;
    private static TranslateResponse translateResponse;

    private static DetectLanguageRequest detectLanguageRequest;
    private static DetectLanguageResponse detectLanguageResponse;

    static void initJsons() {
        translateRequest = new TranslateRequest("en", "ru", List.of("Hello"));

        translateResponse = new TranslateResponse(List.of(
                new TranslateResponse.TranslationResult("Привет")
        ));

        detectLanguageRequest = new DetectLanguageRequest("Hello");

        detectLanguageResponse = new DetectLanguageResponse("en");
    }

    @BeforeAll
    static void initServerAndClient() throws JsonProcessingException {
        initJsons();

        WireMockServer wireMockServer = new WireMockServer(8089);
        wireMockServer.start();

        ObjectMapper objectMapper = new ObjectMapper();
        RestTemplate restTemplate = new RestTemplate();

        ApplicationConfig mockedApplicationConfig = new ApplicationConfig(
                wireMockServer.url("/translate-test"),
                wireMockServer.url("/detect-test"),
                "api-key",
                1
        );

        yandexTranslateApiClient = new YandexTranslateApiClientImpl(restTemplate, mockedApplicationConfig);

        // POST /translate with translateRequestBody returns translateResponseBody
        wireMockServer.stubFor(
                post("/translate-test")
                        .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON_VALUE))
                        .withHeader(AUTHORIZATION, equalTo("Api-Key " + mockedApplicationConfig.yandexApiKey()))
                        .withRequestBody(equalToJson(objectMapper.writeValueAsString(translateRequest)))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.OK.value())
                                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                        .withBody(objectMapper.writeValueAsString(translateResponse))

                        )
        );

        // POST /translate with translateRequestBodyWithoutSourceLanguage returns translateResponseBody
        TranslateRequest translateRequestBodyWithoutSourceLanguage =
                new TranslateRequest(null, translateRequest.targetLanguageCode(), translateRequest.texts());

        wireMockServer.stubFor(
                post("/translate-test")
                        .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON_VALUE))
                        .withHeader(AUTHORIZATION, equalTo("Api-Key " + mockedApplicationConfig.yandexApiKey()))
                        .withRequestBody(equalToJson(objectMapper.writeValueAsString(translateRequestBodyWithoutSourceLanguage)))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.OK.value())
                                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                        .withBody(objectMapper.writeValueAsString(translateResponse))

                        )
        );

        // POST /translate with detectLanguageRequestBody returns detectLanguageResponseBody
        wireMockServer.stubFor(
                post("/detect-test")
                        .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON_VALUE))
                        .withHeader(AUTHORIZATION, equalTo("Api-Key " + mockedApplicationConfig.yandexApiKey()))
                        .withRequestBody(equalToJson(objectMapper.writeValueAsString(detectLanguageRequest)))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.OK.value())
                                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                        .withBody(objectMapper.writeValueAsString(detectLanguageResponse))

                        )
        );
    }


    @Test
    void givenTextAndBothLanguages_whenTranslateWord_thenSuccessfullyTranslated() {
        String text = "Hello";
        String expected = "Привет";
        String sourceLanguage = "en";
        String targetLanguage = "ru";

        String translatedWord = yandexTranslateApiClient.translateWord(text, sourceLanguage, targetLanguage);

        assertThat(translatedWord).isEqualTo(expected);
    }

    @Test
    void givenTextAndTargetLanguage_whenTranslateWord_thenSuccessfullyTranslated() {
        String text = "Hello";
        String expected = "Привет";
        String targetLanguage = "ru";

        String translatedWord = yandexTranslateApiClient.translateWord(text, targetLanguage);

        assertThat(translatedWord).isEqualTo(expected);
    }

    @Test
    void givenText_whenDetectLanguage_thenSuccessfullyDetectsLanguage() {
        String text = "Hello";
        String expected = "en";

        String detectedLanguage = yandexTranslateApiClient.detectLanguage(text);

        assertThat(detectedLanguage).isEqualTo(expected);
    }
}