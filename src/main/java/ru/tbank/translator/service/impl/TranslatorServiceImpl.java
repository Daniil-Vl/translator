package ru.tbank.translator.service.impl;

import org.springframework.stereotype.Service;
import ru.tbank.translator.client.TranslateApiClient;
import ru.tbank.translator.configuration.ApplicationConfig;
import ru.tbank.translator.service.TranslatorService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class TranslatorServiceImpl implements TranslatorService {

    private final TranslateApiClient translateApiClient;
    private final ExecutorService executorService;

    public TranslatorServiceImpl(TranslateApiClient translateApiClient, ApplicationConfig applicationConfig) {
        this.translateApiClient = translateApiClient;
        this.executorService = Executors.newFixedThreadPool(applicationConfig.threadsNumber());
    }

    @Override
    public String translateSentence(String text, String sourceLanguage, String targetLanguage) throws ExecutionException, InterruptedException {
        String[] tokens = text.split(" ");
        StringBuilder translatedText = new StringBuilder();
        List<CompletableFuture<String>> tasks = new ArrayList<>();

        for (String token : tokens) {
            CompletableFuture<String> completableFuture = CompletableFuture
                    .supplyAsync(
                            () -> translateApiClient.translateWord(token, sourceLanguage, targetLanguage),
                            executorService
                    );
            tasks.add(completableFuture);
        }

        for (var task : tasks) {
            translatedText.append(task.get()).append(" ");
        }

        return translatedText.toString().stripTrailing();
    }
}
