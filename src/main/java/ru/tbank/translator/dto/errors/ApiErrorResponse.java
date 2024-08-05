package ru.tbank.translator.dto.errors;

import java.time.OffsetDateTime;

public record ApiErrorResponse(
        OffsetDateTime timestamp,
        String errorMessage,
        Integer statusCode
) {
}
