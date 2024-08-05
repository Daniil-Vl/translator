package ru.tbank.translator.controller.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.translator.controller.TranslatorController;
import ru.tbank.translator.dto.controller_dto.TranslateRequest;
import ru.tbank.translator.dto.controller_dto.TranslateResponse;
import ru.tbank.translator.service.TranslationService;

import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
public class TranslatorControllerImpl implements TranslatorController {

    private final TranslationService translationService;

    @Override
    public TranslateResponse translate(TranslateRequest requestBody, HttpServletRequest httpServletRequest) throws ExecutionException, InterruptedException {
        var translateResponse = translationService.translateSentence(
                requestBody.text(),
                requestBody.sourceLanguage(),
                requestBody.targetLanguage()
        );

        return new TranslateResponse(
                translateResponse.translatedText(),
                translateResponse.sourceLanguage(),
                translateResponse.targetLanguage()
        );
    }
}
