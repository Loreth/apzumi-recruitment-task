TRUNCATE TABLE POST;

INSERT INTO POST(USER_ID, ID, TITLE, BODY, VERSION, DELETED)
VALUES (1, 1, 'great', 'indeed great', 1, 0),
       (1, 2, 'even better', 'or not', 2, 0),
       (1, 3, 'it''s gone', 'yes it is', 1, 1),
       (2, 4, 'another one', 'bites the dust', 4, 1),
       (3, 5, 'Hello there!', 'General Kenobi...', 1, 0);
