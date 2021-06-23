-- Table: prod.stock

-- DROP TABLE prod.stock;

CREATE TABLE IF NOT EXISTS prod.stock
(
    ticket character varying(255) COLLATE pg_catalog."default",
    date date,
    open double precision,
    high double precision,
    low double precision,
    close double precision,
    volume double precision
    )

    TABLESPACE pg_default;

ALTER TABLE prod.stock
    OWNER to postgres;