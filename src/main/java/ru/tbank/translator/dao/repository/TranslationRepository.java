package ru.tbank.translator.dao.repository;

import ru.tbank.translator.dao.model.Translation;

import java.time.OffsetDateTime;
import java.util.List;

public interface TranslationRepository {
    /**
     * Add translation record to database
     *
     * @param translation - information about translation
     */
    void addTranslation(Translation translation);

    /**
     * Returns translation history
     *
     * @param userIp - user's ip
     * @return list of translation requests for given user
     */
    List<Translation> getTranslationHistoryByIp(String userIp, OffsetDateTime from, OffsetDateTime to);
}
