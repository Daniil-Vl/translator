package ru.tbank.translator.dto.yandex_translate;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record YandexTranslateResponseDto(
        @JsonProperty("translations")
        List<TranslationResult> translationResults
) {
    @Override
    public String toString() {
        StringBuilder results = new StringBuilder();

        results.append('\n');
        for (TranslationResult result : translationResults) {
            results.append("\t").append(result.toString()).append('\n');
        }

        return "YandexTranslationResultDto{" +
                "translationResults=" + results +
                '}';
    }

    public record TranslationResult(
            @JsonProperty("text")
            String text,

            @JsonProperty("detectedLanguageCode")
            String detectedLanguageCode
    ) {
    }
}
