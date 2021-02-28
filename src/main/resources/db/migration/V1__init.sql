CREATE TABLE post
(
    user_id          int NOT NULL,
    id               int PRIMARY KEY,
    title            varchar(255),
    body             varchar(1000),
    version          int NOT NULL,
    modified_by_user boolean DEFAULT (FALSE)
);
