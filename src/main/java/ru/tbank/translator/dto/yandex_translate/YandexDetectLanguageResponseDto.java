package ru.tbank.translator.dto.yandex_translate;

import com.fasterxml.jackson.annotation.JsonProperty;

public record YandexDetectLanguageResponseDto(
        @JsonProperty("languageCode") String languageCode
) {
}
