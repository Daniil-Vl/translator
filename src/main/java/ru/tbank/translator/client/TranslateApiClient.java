package ru.tbank.translator.client;

public interface TranslateApiClient {
    /**
     * Translate single word using underlying translate api
     *
     * @param word           - word to translate
     * @param sourceLanguage - the language of given word
     * @param targetLanguage - the language to translate the word into
     * @return translated word
     */
    String translateWord(String word, String sourceLanguage, String targetLanguage);

    /**
     * Detect source language and translated given word to target language
     *
     * @param word           - word to translate
     * @param targetLanguage - the language to translate the word into
     * @return translated word
     */
    String translateWord(String word, String targetLanguage);
}
