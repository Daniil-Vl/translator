package ru.tbank.translator.dto;

public record TranslationDto(
        String sourceText,
        String translatedText,
        String sourceLanguage,
        String targetLanguage,
        String userIp
) {
}
