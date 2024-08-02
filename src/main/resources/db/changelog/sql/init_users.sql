--liquibase formatted sql

--changeset Daniil-Vl:1
CREATE TABLE users
(
    ip CIDR PRIMARY KEY
);
