package ru.tbank.translator.dto.yandex_translate;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TranslateRequest(
        @JsonProperty("sourceLanguageCode") String sourceLanguageCode,
        @JsonProperty("targetLanguageCode") String targetLanguageCode,
        @JsonProperty("texts") List<String> texts
) {
}
