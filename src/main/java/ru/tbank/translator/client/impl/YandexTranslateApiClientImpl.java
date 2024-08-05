package ru.tbank.translator.client.impl;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.tbank.translator.client.TranslateApiClient;
import ru.tbank.translator.configuration.ApplicationConfig;
import ru.tbank.translator.dto.yandex_translate.*;
import ru.tbank.translator.exceptions.LanguageNotDetectedException;
import ru.tbank.translator.exceptions.TranslateApiUnavailableException;

import java.net.URI;
import java.util.List;

@Component
public class YandexTranslateApiClientImpl implements TranslateApiClient {

    private final RestTemplate restTemplate;
    private final HttpHeaders httpHeaders;
    private final URI translateApiUrl;
    private final URI detectLanguageApiUrl;

    public YandexTranslateApiClientImpl(RestTemplate restTemplate, ApplicationConfig applicationConfig) {
        this.restTemplate = restTemplate;

        this.translateApiUrl = URI.create(applicationConfig.yandexTranslateUrl());
        this.detectLanguageApiUrl = URI.create(applicationConfig.yandexDetectLanguageUrl());

        httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set(HttpHeaders.AUTHORIZATION, "Api-Key " + applicationConfig.yandexApiKey());
    }

    @Override
    public TranslateResponse translateWord(String word, String sourceLanguage, String targetLanguage) {
        TranslateRequest bodyDto = new TranslateRequest(
                sourceLanguage,
                targetLanguage,
                List.of(word)
        );

        YandexTranslateResponseDto yandexTranslateResponseDto = getTranslatedWord(bodyDto);
        YandexTranslateResponseDto.TranslationResult translationResult = yandexTranslateResponseDto.translationResults().getFirst();

        if (sourceLanguage == null) {
            sourceLanguage = translationResult.detectedLanguageCode();
        }

        return new TranslateResponse(
                translationResult.text(),
                sourceLanguage,
                targetLanguage
        );
    }

    @Override
    public TranslateResponse translateWord(String word, String targetLanguage) {
        TranslateRequest bodyDto = new TranslateRequest(
                null,
                targetLanguage,
                List.of(word)
        );

        YandexTranslateResponseDto yandexTranslateResponseDto = getTranslatedWord(bodyDto);
        YandexTranslateResponseDto.TranslationResult translationResult = yandexTranslateResponseDto.translationResults().getFirst();

        return new TranslateResponse(
                translationResult.text(),
                translationResult.detectedLanguageCode(),
                targetLanguage
        );
    }

    @Override
    public String detectLanguage(String word) {
        DetectLanguageRequest bodyDto = new DetectLanguageRequest(word);

        HttpEntity<DetectLanguageRequest> httpEntity = new HttpEntity<>(bodyDto, httpHeaders);

        ResponseEntity<DetectLanguageResponse> result = restTemplate.exchange(
                detectLanguageApiUrl, HttpMethod.POST, httpEntity, DetectLanguageResponse.class
        );

        return result.getBody().languageCode();
    }

    private YandexTranslateResponseDto getTranslatedWord(TranslateRequest bodyDto) {
        HttpEntity<TranslateRequest> httpEntity = new HttpEntity<>(bodyDto, httpHeaders);

        // It is necessary to handle errors here, because it is impossible to receive an error message in ResponseErrorHandler
        try {
            return restTemplate
                    .exchange(translateApiUrl, HttpMethod.POST, httpEntity, YandexTranslateResponseDto.class)
                    .getBody();
        } catch (HttpClientErrorException exc) {
            if (exc.getStatusCode().is5xxServerError())
                throw new TranslateApiUnavailableException();

            if (exc.getStatusCode().equals(HttpStatus.BAD_REQUEST))
                throw getCause(exc.getMessage());

            throw new RuntimeException(exc);
        }
    }

    /**
     * Determines which of the languages was not found and throws an exception with the corresponding message.
     *
     * @param errorMessage - error message from yandex translate api
     * @return exception with corresponding message
     */
    private LanguageNotDetectedException getCause(String errorMessage) {
        /*
        Message from yandex api looks like this:
              400 Bad Request: "{<EOL> "code": 3,<EOL> "message": "unsupported target_language_code: fu",<EOL> ...
         or this:
              400 Bad Request: "{<EOL> "code": 3,<EOL> "message": "unsupported source_language_code: fu",<EOL> ...
        */

        String languageType = errorMessage.substring(65, 71);

        String exceptionMessage = switch (languageType) {
            case "target" -> LanguageNotDetectedException.TARGET_LANGUAGE_NOT_DETECTED_MESSAGE;
            case "source" -> LanguageNotDetectedException.SOURCE_LANGUAGE_NOT_DETECTED_MESSAGE;
            default -> throw new IllegalStateException("Unexpected value: " + languageType);
        };

        return new LanguageNotDetectedException(exceptionMessage);
    }
}
