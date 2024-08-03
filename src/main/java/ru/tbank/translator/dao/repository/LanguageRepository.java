package ru.tbank.translator.dao.repository;

import ru.tbank.translator.dao.model.Language;

import java.util.List;

public interface LanguageRepository {
    /**
     * Adds new supported language to db
     * @param language - language to add
     */
    void addLanguage(Language language);

    /**
     * Stop supporting language
     *
     * @param name - language's short name
     */
    void removeLanguage(String name);

    /**
     * @return list of available languages
     */
    List<Language> getLanguages();
}
