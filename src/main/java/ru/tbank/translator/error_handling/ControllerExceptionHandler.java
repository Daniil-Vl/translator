package ru.tbank.translator.error_handling;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import ru.tbank.translator.dto.errors.ApiErrorResponse;

import java.time.OffsetDateTime;

@RestControllerAdvice
@Log4j2
public class ControllerExceptionHandler {

    private static final int YANDEX_UNSUPPORTED_LANGUAGE_CODE = 3;
    private static final String SOURCE_LANGUAGE_NOT_DETECTED_MESSAGE = "Язык исходного текста не найден";
    private static final String TARGET_LANGUAGE_NOT_DETECTED_MESSAGE = "Язык перевода не найден";
    private static final String UNDEFINED_LANGUAGE_NOT_DETECTED_MESSAGE = "Язык не найден";
    private static final String UNKNOWN_ERROR_MESSAGE = "Неопознанная ошибка при переводе";


    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiErrorResponse invalidArgument(MethodArgumentNotValidException exc) {

        log.info("Processing {} in invalidArgument exception handler", exc.toString());

        return new ApiErrorResponse(
                OffsetDateTime.now(),
                exc.getFieldError().getDefaultMessage(),
                HttpStatus.BAD_REQUEST.value()
        );

    }

    @ExceptionHandler(value = HttpClientErrorException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiErrorResponse httpClientError(HttpClientErrorException exc) {

        log.warn("Caught HttpClientErrorException - {}", exc.getMessage());
        YandexApiErrorMessage responseBody = exc.getResponseBodyAs(YandexApiErrorMessage.class);
        log.warn("Error response body: {}", responseBody);
        String errorMessage = getErrorMessage(responseBody);

        return new ApiErrorResponse(
                OffsetDateTime.now(),
                errorMessage,
                HttpStatus.BAD_REQUEST.value()
        );

    }

    private String getErrorMessage(YandexApiErrorMessage responseBody) {
        if (responseBody == null) {
            return UNKNOWN_ERROR_MESSAGE;
        }

        if (responseBody.code() == YANDEX_UNSUPPORTED_LANGUAGE_CODE) {
            String[] tokens = responseBody.message.split(" ");
            String languageType = tokens[1].substring(0, 6);
            return switch (languageType) {
                case "source" -> SOURCE_LANGUAGE_NOT_DETECTED_MESSAGE;
                case "target" -> TARGET_LANGUAGE_NOT_DETECTED_MESSAGE;
                default -> UNDEFINED_LANGUAGE_NOT_DETECTED_MESSAGE;
            };
        }

        return responseBody.message;
    }

    private record YandexApiErrorMessage(int code, String message) {
    }
}
