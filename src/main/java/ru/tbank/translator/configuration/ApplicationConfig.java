package ru.tbank.translator.configuration;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
        @Bean @NotNull String yandexTranslateUrl,
        @Bean @NotNull String yandexDetectLanguageUrl,
        @Bean @NotNull String yandexApiKey,
        @NotNull Integer threadsNumber
) {
}
