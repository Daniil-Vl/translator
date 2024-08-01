package ru.tbank.translator;

import org.springframework.boot.SpringApplication;

public class TestTranslatorApplication {

    public static void main(String[] args) {
        SpringApplication.from(TranslatorApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
