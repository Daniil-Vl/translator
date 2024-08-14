package ru.tbank.translator.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.tbank.translator.client.TranslateApiClient;
import ru.tbank.translator.dao.model.Translation;
import ru.tbank.translator.dao.repository.TranslationRepository;
import ru.tbank.translator.dto.yandex_translate.TranslateResponse;
import ru.tbank.translator.service.TranslationService;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Log4j2
public class TranslationServiceImpl implements TranslationService {

    private final TranslateApiClient translateApiClient;
    private final TranslationRepository translationRepository;

    public TranslationServiceImpl(TranslateApiClient translateApiClient, TranslationRepository translationRepository) {
        this.translateApiClient = translateApiClient;
        this.translationRepository = translationRepository;
    }

    @Override
    public TranslateResponse translateSentence(String text, String sourceLanguage, String targetLanguage) throws ExecutionException, InterruptedException {
        String[] tokens = text.split(" ");
        StringBuilder translatedText = new StringBuilder();
        List<CompletableFuture<TranslateResponse>> tasks = new ArrayList<>();

        for (String token : tokens) {
            CompletableFuture<TranslateResponse> response =
                    translateApiClient.translateWord(token, sourceLanguage, targetLanguage);
            tasks.add(response);
        }

        for (var task : tasks) {
            translatedText.append(task.get().translatedText()).append(" ");
        }

        String resultSourceLanguage = sourceLanguage;
        if (resultSourceLanguage == null) {
            resultSourceLanguage = tasks.getFirst().get().sourceLanguage();
            log.info("Detect language: {}. For text with first word: {}", resultSourceLanguage, tokens[0]);
        }

        return new TranslateResponse(
                translatedText.toString().stripTrailing(),
                resultSourceLanguage,
                targetLanguage
        );
    }

    @Override
    public TranslateResponse translateSentence(String text, String targetLanguage) throws ExecutionException, InterruptedException {
        return translateSentence(text, null, targetLanguage);
    }

    @Override
    public void saveTranslation(Translation translation) {
        translationRepository.addTranslation(translation);
    }

    @Override
    public List<Translation> getTranslationHistoryByIp(String userIp, OffsetDateTime from, OffsetDateTime to) {
        return translationRepository.getTranslationHistoryByIp(userIp, from, to);
    }
}
