CREATE TABLE IF NOT EXISTS users
(
    id    bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name  varchar NOT NULL,
    email varchar NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS requests
(
    id           bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    description  text,
    requestor_id bigint NOT NULL REFERENCES users (id),
    created timestamp without time zone NOT NULL
);

CREATE TABLE IF NOT EXISTS items
(
    id           bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name         varchar NOT NULL,
    description  text    NOT NULL,
    is_available boolean NOT NULL,
    owner_id     bigint  NOT NULL REFERENCES users (id),
    request_id   bigint REFERENCES requests (id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    start_date timestamp without time zone NOT NULL,
    end_date   timestamp without time zone NOT NULL,
    item_id    bigint    NOT NULL REFERENCES items (id),
    booker_id  bigint    NOT NULL REFERENCES users (id),
    status     varchar   NOT NULL
);

CREATE TABLE IF NOT EXISTS comments
(
    id        bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text      text   NOT NULL,
    item_id   bigint NOT NULL REFERENCES items (id),
    author_id bigint NOT NULL REFERENCES users (id),
    created timestamp without time zone NOT NULL
);

