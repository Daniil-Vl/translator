package ru.tbank.translator.dto.controller_dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

public record TranslationHistoryRequest(
        @JsonProperty("from") OffsetDateTime from,
        @JsonProperty("to") OffsetDateTime to
) {
}
