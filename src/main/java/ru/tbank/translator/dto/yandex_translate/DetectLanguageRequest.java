package ru.tbank.translator.dto.yandex_translate;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DetectLanguageRequest(
        @JsonProperty("text") String text
) {
}
