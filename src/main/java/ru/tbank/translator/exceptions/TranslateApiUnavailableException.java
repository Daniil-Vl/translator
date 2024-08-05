package ru.tbank.translator.exceptions;

public class TranslateApiUnavailableException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Ошибка доступа к сервису перевода";

    public TranslateApiUnavailableException() {
        super(DEFAULT_MESSAGE);
    }
}
