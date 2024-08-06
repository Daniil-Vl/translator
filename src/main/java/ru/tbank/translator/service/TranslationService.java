package ru.tbank.translator.service;

import ru.tbank.translator.dao.model.Translation;
import ru.tbank.translator.dto.yandex_translate.TranslateResponse;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface TranslationService {
    /**
     * Translate given text
     *
     * @param text           - the text to translate
     * @param sourceLanguage - the source language of the given text
     * @param targetLanguage - the language to be translated into
     * @return translated text
     */
    TranslateResponse translateSentence(String text, String sourceLanguage, String targetLanguage) throws ExecutionException, InterruptedException;

    /**
     * Detects text source language and translated it to target language
     *
     * @param text           - the text to translate
     * @param targetLanguage - the language to be translated into
     * @return translated text
     */
    TranslateResponse translateSentence(String text, String targetLanguage) throws ExecutionException, InterruptedException;

    /**
     * Saves record about translation result in database
     *
     * @param translation - response to be saved
     */
    void saveTranslation(Translation translation);

    /**
     * Get translation history for given user in certain time period
     *
     * @param userIp - user to get translation history for
     * @param from   - start time
     * @param to     - end time
     * @return list of translations
     */
    List<Translation> getTranslationHistoryByIp(String userIp, OffsetDateTime from, OffsetDateTime to);
}
