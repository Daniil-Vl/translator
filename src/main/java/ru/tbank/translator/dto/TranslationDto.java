package ru.tbank.translator.dto;

import java.time.OffsetDateTime;

public record TranslationDto(
        String sourceText,
        String translatedText,
        String sourceLanguage,
        String targetLanguage,
        String userIp,
        OffsetDateTime translationDateTime
) {
}
