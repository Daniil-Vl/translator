package ru.tbank.translator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.tbank.translator.configuration.ApplicationConfig;

@EnableConfigurationProperties(ApplicationConfig.class)
@SpringBootApplication
public class TranslatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(TranslatorApplication.class, args);
    }

}
