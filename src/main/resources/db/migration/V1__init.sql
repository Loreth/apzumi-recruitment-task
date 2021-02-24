CREATE TABLE post
(
    user_id int NOT NULL,
    id      int PRIMARY KEY,
    title   varchar(255),
    body    varchar(1000),
    version int NOT NULL,
    deleted bool NOT NULL DEFAULT (false)
);
