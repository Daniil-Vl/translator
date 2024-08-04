package ru.tbank.translator.service.impl;

import org.springframework.stereotype.Service;
import ru.tbank.translator.client.TranslateApiClient;
import ru.tbank.translator.configuration.ApplicationConfig;
import ru.tbank.translator.dto.yandex_translate.TranslateResponse;
import ru.tbank.translator.service.TranslationService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class TranslationServiceImpl implements TranslationService {

    private final TranslateApiClient translateApiClient;
    private final ExecutorService executorService;

    public TranslationServiceImpl(TranslateApiClient translateApiClient, ApplicationConfig applicationConfig) {
        this.translateApiClient = translateApiClient;
        this.executorService = Executors.newFixedThreadPool(applicationConfig.threadsNumber());
    }

    @Override
    public String translateSentence(String text, String sourceLanguage, String targetLanguage) throws ExecutionException, InterruptedException {
        String[] tokens = text.split(" ");
        StringBuilder translatedText = new StringBuilder();
        List<CompletableFuture<TranslateResponse>> tasks = new ArrayList<>();

        for (String token : tokens) {
            CompletableFuture<TranslateResponse> completableFuture = CompletableFuture
                    .supplyAsync(
                            () -> translateApiClient.translateWord(token, sourceLanguage, targetLanguage),
                            executorService
                    );
            tasks.add(completableFuture);
        }

        for (var task : tasks) {
            translatedText.append(task.get().translatedText()).append(" ");
        }

        return translatedText.toString().stripTrailing();
    }

    @Override
    public String translateSentence(String text, String targetLanguage) throws ExecutionException, InterruptedException {
        String sourceLanguage = translateApiClient.detectLanguage(text);
        return translateSentence(text, sourceLanguage, targetLanguage);
    }
}
