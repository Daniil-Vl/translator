package ru.tbank.translator.dto.controller_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record TranslateRequest(
        @JsonProperty("text")
        @NotBlank(message = "Поле с текстом для перевода не должно быть пустым")
        String text,

        @JsonProperty("sourceLanguage")
        String sourceLanguage,

        @JsonProperty("targetLanguage")
        @NotBlank(message = "Поле с языком перевода не должно быть пустым")
        String targetLanguage
) {
}
