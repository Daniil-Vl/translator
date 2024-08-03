package ru.tbank.translator.client.impl;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.tbank.translator.client.TranslateApiClient;
import ru.tbank.translator.dto.yandex_translate.YandexTranslateRequestBodyDto;
import ru.tbank.translator.dto.yandex_translate.YandexTranslationResultDto;

import java.net.URI;
import java.util.List;

@Component
public class YandexTranslateApiClientImpl implements TranslateApiClient {

    private final RestTemplate restTemplate;
    private final HttpHeaders httpHeaders;
    private final URI apiUrl;
    private final String folderId;

    public YandexTranslateApiClientImpl(RestTemplate restTemplate, String yandexTranslateUrl, String yandexApiKey, String yandexFolderId) {
        this.restTemplate = restTemplate;
        this.apiUrl = URI.create(yandexTranslateUrl);
        this.folderId = yandexFolderId;

        httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set(HttpHeaders.AUTHORIZATION, "Api-Key " + yandexApiKey);
    }

    @Override
    public String translateWord(String word, String sourceLanguage, String targetLanguage) {
        YandexTranslateRequestBodyDto bodyDto = new YandexTranslateRequestBodyDto(
                sourceLanguage,
                targetLanguage,
                folderId,
                List.of(word)
        );

        HttpEntity<YandexTranslateRequestBodyDto> httpEntity = new HttpEntity<>(bodyDto, httpHeaders);

        ResponseEntity<YandexTranslationResultDto> result = restTemplate.exchange(apiUrl, HttpMethod.POST, httpEntity, YandexTranslationResultDto.class);

        YandexTranslationResultDto resultBody = result.getBody();

        return resultBody.translationResults().getFirst().text();
    }
}
