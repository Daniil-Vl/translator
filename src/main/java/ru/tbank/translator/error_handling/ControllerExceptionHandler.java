package ru.tbank.translator.error_handling;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.tbank.translator.dto.errors.ApiErrorResponse;
import ru.tbank.translator.exceptions.LanguageNotDetectedException;

import java.time.OffsetDateTime;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiErrorResponse invalidArgument(MethodArgumentNotValidException exc) {

        return new ApiErrorResponse(
                OffsetDateTime.now(),
                exc.getFieldError().getDefaultMessage(),
                HttpStatus.BAD_REQUEST.value()
        );

    }

    @ExceptionHandler(value = {LanguageNotDetectedException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiErrorResponse languageNotDetected(LanguageNotDetectedException exc) {

        return new ApiErrorResponse(
                OffsetDateTime.now(),
                exc.getMessage(),
                HttpStatus.BAD_REQUEST.value()
        );

    }

}
