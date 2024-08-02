--liquibase formatted sql

--changeset Daniil-Vl:2
CREATE TABLE language
(
    name     VARCHAR(3) PRIMARY KEY CHECK ( length(name) > 0 ),
    fullname VARCHAR(50) UNIQUE NOT NULL CHECK ( length(fullname) > 0 )
);
