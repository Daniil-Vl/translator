package ru.tbank.translator.client.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.tbank.translator.client.TranslateApiClient;
import ru.tbank.translator.configuration.ApplicationConfig;
import ru.tbank.translator.dto.yandex_translate.*;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Log4j2
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

    @Async
    @Override
    public CompletableFuture<TranslateResponse> translateWord(String word, String sourceLanguage, String targetLanguage) {
        log.info("Translate word {} from {} language to {} language", word, sourceLanguage, targetLanguage);

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


        TranslateResponse translateResponse = new TranslateResponse(
                translationResult.text(),
                sourceLanguage,
                targetLanguage
        );

        return CompletableFuture.completedFuture(translateResponse);
    }

    @Async
    @Override
    public CompletableFuture<TranslateResponse> translateWord(String word, String targetLanguage) {
        log.info("Translate word {} to {} language", word, targetLanguage);

        TranslateRequest bodyDto = new TranslateRequest(
                null,
                targetLanguage,
                List.of(word)
        );

        YandexTranslateResponseDto yandexTranslateResponseDto = getTranslatedWord(bodyDto);
        YandexTranslateResponseDto.TranslationResult translationResult = yandexTranslateResponseDto.translationResults().getFirst();

        TranslateResponse translateResponse = new TranslateResponse(
                translationResult.text(),
                translationResult.detectedLanguageCode(),
                targetLanguage
        );

        return CompletableFuture.completedFuture(translateResponse);
    }

    @Async
    @Override
    public CompletableFuture<String> detectLanguage(String word) {
        log.info("Detect word's '{}' language", word);

        DetectLanguageRequest bodyDto = new DetectLanguageRequest(word);

        HttpEntity<DetectLanguageRequest> httpEntity = new HttpEntity<>(bodyDto, httpHeaders);

        ResponseEntity<DetectLanguageResponse> result = restTemplate.exchange(
                detectLanguageApiUrl, HttpMethod.POST, httpEntity, DetectLanguageResponse.class
        );

        String s = result.getBody().languageCode();
        return CompletableFuture.completedFuture(s);
    }

    private YandexTranslateResponseDto getTranslatedWord(TranslateRequest bodyDto) {
        HttpEntity<TranslateRequest> httpEntity = new HttpEntity<>(bodyDto, httpHeaders);
        return restTemplate
                .exchange(translateApiUrl, HttpMethod.POST, httpEntity, YandexTranslateResponseDto.class)
                .getBody();
    }
}
