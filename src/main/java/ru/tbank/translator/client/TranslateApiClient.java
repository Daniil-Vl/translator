package ru.tbank.translator.client;

import ru.tbank.translator.dto.yandex_translate.TranslateResponse;

import java.util.concurrent.CompletableFuture;

public interface TranslateApiClient {
    /**
     * Translate single word using underlying translate api
     *
     * @param word           - word to translate
     * @param sourceLanguage - the language of given word
     * @param targetLanguage - the language to translate the word into
     * @return translated word
     */
    CompletableFuture<TranslateResponse> translateWord(String word, String sourceLanguage, String targetLanguage);

    /**
     * Detects word's source language and translated it to target language using underlying translate api
     *
     * @param word           - word to translate
     * @param targetLanguage - the language to translate the word into
     * @return translated word
     */
    CompletableFuture<TranslateResponse> translateWord(String word, String targetLanguage);

    /**
     * Detect text's language
     *
     * @param text - the text whose language you need to get
     * @return text's language
     */
    CompletableFuture<String> detectLanguage(String text);
}
