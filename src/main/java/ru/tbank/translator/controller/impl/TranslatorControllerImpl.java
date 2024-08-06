package ru.tbank.translator.controller.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.translator.controller.TranslatorController;
import ru.tbank.translator.dao.model.Translation;
import ru.tbank.translator.dao.repository.TranslationRepository;
import ru.tbank.translator.dto.TranslationDto;
import ru.tbank.translator.dto.controller_dto.TranslateRequest;
import ru.tbank.translator.dto.controller_dto.TranslateResponse;
import ru.tbank.translator.service.TranslationService;

import java.time.OffsetDateTime;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
public class TranslatorControllerImpl implements TranslatorController {

    private final TranslationService translationService;
    private final TranslationRepository translationRepository;

    @Override
    public TranslateResponse translate(TranslateRequest requestBody, HttpServletRequest httpServletRequest) throws ExecutionException, InterruptedException {
        var translateResponse = translationService.translateSentence(
                requestBody.text(),
                requestBody.sourceLanguage(),
                requestBody.targetLanguage()
        );

        Translation translation = new Translation(
                requestBody.text(),
                translateResponse.translatedText(),
                translateResponse.sourceLanguage(),
                requestBody.targetLanguage(),
                httpServletRequest.getRemoteAddr(),
                OffsetDateTime.now()
        );
        translationRepository.addTranslation(translation);

        return new TranslateResponse(
                translateResponse.translatedText(),
                translateResponse.sourceLanguage(),
                translateResponse.targetLanguage()
        );
    }
}
