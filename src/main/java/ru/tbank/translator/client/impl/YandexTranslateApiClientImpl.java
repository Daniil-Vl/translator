package ru.tbank.translator.client.impl;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.tbank.translator.client.TranslateApiClient;
import ru.tbank.translator.configuration.ApplicationConfig;
import ru.tbank.translator.dto.yandex_translate.DetectLanguageRequest;
import ru.tbank.translator.dto.yandex_translate.DetectLanguageResponse;
import ru.tbank.translator.dto.yandex_translate.TranslateRequest;
import ru.tbank.translator.dto.yandex_translate.TranslateResponse;

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
    public String translateWord(String word, String sourceLanguage, String targetLanguage) {
        TranslateRequest bodyDto = new TranslateRequest(
                sourceLanguage,
                targetLanguage,
                List.of(word)
        );

        return getTranslatedWord(bodyDto);
    }

    @Override
    public String translateWord(String word, String targetLanguage) {
        TranslateRequest bodyDto = new TranslateRequest(
                null,
                targetLanguage,
                List.of(word)
        );

        return getTranslatedWord(bodyDto);
    }

    private String getTranslatedWord(TranslateRequest bodyDto) {
        HttpEntity<TranslateRequest> httpEntity = new HttpEntity<>(bodyDto, httpHeaders);

        ResponseEntity<TranslateResponse> result = restTemplate.exchange(
                translateApiUrl, HttpMethod.POST, httpEntity, TranslateResponse.class);

        TranslateResponse resultBody = result.getBody();

        return resultBody.translationResults().getFirst().text();
    }

    @Override
    public String detectLanguage(String word) {
        DetectLanguageRequest bodyDto = new DetectLanguageRequest(word);

        HttpEntity<DetectLanguageRequest> httpEntity = new HttpEntity<>(bodyDto, httpHeaders);

        ResponseEntity<DetectLanguageResponse> result = restTemplate.exchange(
                detectLanguageApiUrl, HttpMethod.POST, httpEntity, DetectLanguageResponse.class);

        return result.getBody().languageCode();
    }
}
