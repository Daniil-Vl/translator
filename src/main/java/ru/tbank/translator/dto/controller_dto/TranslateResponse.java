package ru.tbank.translator.dto.controller_dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TranslateResponse(
        @JsonProperty("translatedText") String translatedText,
        @JsonProperty("sourceLanguage") String sourceLanguage,
        @JsonProperty("targetLanguage") String targetLanguage
) {
}
