package ru.tbank.translator.dto.yandex_translate;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record YandexTranslateRequestBodyDto(
        @JsonProperty("sourceLanguageCode") String sourceLanguageCode,
        @JsonProperty("targetLanguageCode") String targetLanguageCode,
        @JsonProperty("folderId") String folderId,
        @JsonProperty("texts") List<String> texts
) {
}
