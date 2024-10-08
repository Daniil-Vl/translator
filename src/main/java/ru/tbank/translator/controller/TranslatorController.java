package ru.tbank.translator.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.tbank.translator.dto.TranslationDto;
import ru.tbank.translator.dto.controller_dto.TranslateRequest;
import ru.tbank.translator.dto.controller_dto.TranslateResponse;
import ru.tbank.translator.dto.controller_dto.TranslationHistoryRequest;
import ru.tbank.translator.dto.errors.ApiErrorResponse;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public interface TranslatorController {

    @Operation(summary = "Перевести данный текст")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Текст успешно переведен",
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TranslateResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            Не определен исходный язык или язык перевода / \n
                            Тело запроса содержит пустые строки (текст или один из языков) / \n
                            Ошибка доступа к ресурсу перевода
                            """,
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @PostMapping(
            path = "/translate",
            consumes = {APPLICATION_JSON_VALUE},
            produces = {APPLICATION_JSON_VALUE}
    )
    TranslateResponse translate(
            @Valid @RequestBody TranslateRequest requestBody,
            HttpServletRequest httpServletRequest
    ) throws ExecutionException, InterruptedException;


    @Operation(summary = "Получить история переводов за определенный период времени")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "История переводов успешно получена"
            )
    })
    @GetMapping("/translation-history")
    List<TranslationDto> getTranslationHistoryByIp(@RequestBody TranslationHistoryRequest requestBody, HttpServletRequest request);
}
