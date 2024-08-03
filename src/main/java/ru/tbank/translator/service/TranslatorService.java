package ru.tbank.translator.service;

import java.util.concurrent.ExecutionException;

public interface TranslatorService {
    /**
     * Translate given text
     *
     * @param text           - the text to translate
     * @param sourceLanguage - the source language of the given text
     * @param targetLanguage - the language to be translated into
     * @return translated text
     */
    String translateSentence(String text, String sourceLanguage, String targetLanguage) throws ExecutionException, InterruptedException;
}
