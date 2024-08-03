package ru.tbank.translator.dao.repository;

import ru.tbank.translator.dao.model.Translation;
import ru.tbank.translator.dto.TranslationDto;

import java.util.List;

public interface TranslationRepository {
    /**
     * Add translation record to database
     *
     * @param translationDto - information about translation
     * @return translation info
     */
    Translation addTranslation(Translation translationDto);

    /**
     * Returns translation history
     *
     * @param userIp - user's ip
     * @return list of translation requests for given user
     */
    List<Translation> getTranslationHistoryByIp(String userIp);
}
