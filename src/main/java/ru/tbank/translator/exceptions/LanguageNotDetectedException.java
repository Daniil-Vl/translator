package ru.tbank.translator.exceptions;

public class LanguageNotDetectedException extends RuntimeException {
    public static final String SOURCE_LANGUAGE_NOT_DETECTED_MESSAGE = "Язык исходного текста не найден";
    public static final String TARGET_LANGUAGE_NOT_DETECTED_MESSAGE = "Язык перевода не найден";

    public LanguageNotDetectedException(String message) {
        super(message);
    }
}
