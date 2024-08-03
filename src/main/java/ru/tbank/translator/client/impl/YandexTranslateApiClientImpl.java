package ru.tbank.translator.client.impl;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.tbank.translator.client.TranslateApiClient;
import ru.tbank.translator.configuration.ApplicationConfig;
import ru.tbank.translator.dto.yandex_translate.YandexDetectLanguageRequestDto;
import ru.tbank.translator.dto.yandex_translate.YandexDetectLanguageResponseDto;
import ru.tbank.translator.dto.yandex_translate.YandexTranslateRequestBodyDto;
import ru.tbank.translator.dto.yandex_translate.YandexTranslationResultDto;

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
        YandexTranslateRequestBodyDto bodyDto = new YandexTranslateRequestBodyDto(
                sourceLanguage,
                targetLanguage,
                List.of(word)
        );

        HttpEntity<YandexTranslateRequestBodyDto> httpEntity = new HttpEntity<>(bodyDto, httpHeaders);

        ResponseEntity<YandexTranslationResultDto> result = restTemplate.exchange(
                translateApiUrl, HttpMethod.POST, httpEntity, YandexTranslationResultDto.class);

        YandexTranslationResultDto resultBody = result.getBody();

        return resultBody.translationResults().getFirst().text();
    }

    private String detectLanguage(String word) {
        YandexDetectLanguageRequestDto bodyDto = new YandexDetectLanguageRequestDto(word);

        HttpEntity<YandexDetectLanguageRequestDto> httpEntity = new HttpEntity<>(bodyDto, httpHeaders);

        ResponseEntity<YandexDetectLanguageResponseDto> result = restTemplate.exchange(
                detectLanguageApiUrl, HttpMethod.POST, httpEntity, YandexDetectLanguageResponseDto.class);

        return result.getBody().languageCode();
    }

    @Override
    public String translateWord(String word, String targetLanguage) {
        String sourceLanguage = detectLanguage(word);
        return translateWord(word, sourceLanguage, targetLanguage);
    }
}
