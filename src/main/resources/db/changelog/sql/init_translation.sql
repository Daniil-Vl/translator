--liquibase formatted sql

--changeset Daniil-Vl:3
CREATE TABLE translation
(
    id                    BIGSERIAL PRIMARY KEY,

    source_text           TEXT                     NOT NULL CHECK ( length(source_text) > 0 ),
    translated_text       TEXT                     NOT NULL CHECK ( length(translated_text) > 0 ),

    source_language       VARCHAR(3)               NOT NULL,
    target_language       VARCHAR(3)               NOT NULL,

    user_ip               CIDR                     NOT NULL REFERENCES users (ip) ON DELETE CASCADE,
    translation_date_time TIMESTAMP WITH TIME ZONE NOT NULL
)
