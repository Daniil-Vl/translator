package ru.tbank.translator.dao.model;

import java.time.OffsetDateTime;

public record Translation(
        String sourceText,
        String translatedText,
        String sourceLanguage,
        String targetLanguage,
        String userIp,
        OffsetDateTime translationDateTime
) {
}
