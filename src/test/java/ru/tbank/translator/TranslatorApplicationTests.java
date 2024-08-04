package ru.tbank.translator;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
public class TranslatorApplicationTests {
    @DynamicPropertySource
    static void yandexMockConfig(DynamicPropertyRegistry registry) {
        registry.add("app.yandex-translate-url", () -> "");
        registry.add("app.yandex-detect-language-url", () -> "");
        registry.add("app.yandex-api-key", () -> "");
    }
}
