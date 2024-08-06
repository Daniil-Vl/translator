package ru.tbank.translator.controller.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.translator.controller.TranslatorController;
import ru.tbank.translator.dao.model.Translation;
import ru.tbank.translator.dto.TranslationDto;
import ru.tbank.translator.dto.controller_dto.TranslateRequest;
import ru.tbank.translator.dto.controller_dto.TranslateResponse;
import ru.tbank.translator.dto.controller_dto.TranslationHistoryRequest;
import ru.tbank.translator.service.TranslationService;
import ru.tbank.translator.service.UsersService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@Log4j2
public class TranslatorControllerImpl implements TranslatorController {

    private final TranslationService translationService;
    private final UsersService usersService;

    @Override
    public TranslateResponse translate(TranslateRequest requestBody, HttpServletRequest httpServletRequest) throws ExecutionException, InterruptedException {
        String userIp = httpServletRequest.getRemoteAddr();

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
                userIp,
                OffsetDateTime.now()
        );

        usersService.addUser(userIp);

        translationService.saveTranslation(translation);

        return new TranslateResponse(
                translateResponse.translatedText(),
                translateResponse.sourceLanguage(),
                translateResponse.targetLanguage()
        );
    }

    @Override
    public List<TranslationDto> getTranslationHistoryByIp(TranslationHistoryRequest requestBody, HttpServletRequest request) {
        String userIp = request.getRemoteAddr();

        log.info("Getting translation history for user with ip = {}", userIp);

        OffsetDateTime from = requestBody.from() == null ? OffsetDateTime.MIN : requestBody.from();
        OffsetDateTime to = requestBody.to() == null ? OffsetDateTime.MAX : requestBody.to();

        return translationService
                .getTranslationHistoryByIp(userIp, from, to)
                .stream()
                .map(translation -> new TranslationDto(
                        translation.sourceText(),
                        translation.translatedText(),
                        translation.sourceLanguage(),
                        translation.targetLanguage(),
                        translation.userIp(),
                        translation.translationDateTime()
                ))
                .toList();
    }
}
