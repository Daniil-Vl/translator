package ru.tbank.translator.client.impl;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.tbank.translator.client.TranslateApiClient;
import ru.tbank.translator.configuration.ApplicationConfig;
import ru.tbank.translator.dto.yandex_translate.*;

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

    private YandexTranslateResponseDto getTranslatedWord(TranslateRequest bodyDto) {
        HttpEntity<TranslateRequest> httpEntity = new HttpEntity<>(bodyDto, httpHeaders);

        ResponseEntity<YandexTranslateResponseDto> result = restTemplate.exchange(
                translateApiUrl, HttpMethod.POST, httpEntity, YandexTranslateResponseDto.class);

        return result.getBody();
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
