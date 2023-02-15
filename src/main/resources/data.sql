-- add users
INSERT INTO "users" ("name", "login", "email", "birthday") VALUES ('user1', 'login1', 'email1@example.com', '2000-01-01');
INSERT INTO "users" ("name", "login", "email", "birthday") VALUES ('user2', 'login2', 'email2@example.com', '1999-01-01');
INSERT INTO "users" ("name", "login", "email", "birthday") VALUES ('user3', 'login3', 'email3@example.com', '1998-01-01');
--add friendship
INSERT INTO "friendship_requests" ("user_id", "friend_id", "is_approved") VALUES (1, 2, 'TRUE');
INSERT INTO "friendship_requests" ("user_id", "friend_id", "is_approved") VALUES (2, 3, 'FALSE');
INSERT INTO "friendship_requests" ("user_id", "friend_id", "is_approved") VALUES (3, 1, 'TRUE');
-- add genres
INSERT INTO "genre" ("name") VALUES ('comedy');
INSERT INTO "genre" ("name") VALUES ('drama');
INSERT INTO "genre" ("name") VALUES ('anime');
-- add ratings
INSERT INTO "rating" ("name") VALUES ('G');
INSERT INTO "rating" ("name") VALUES ('PG');
INSERT INTO "rating" ("name") VALUES ('PG-13');
INSERT INTO "rating" ("name") VALUES ('R');
INSERT INTO "rating" ("name") VALUES ('NC-17');
-- add films data
INSERT INTO "films"  ("title", "description" , "release_date", "duration", "rating_id")
VALUES ('film1', 'description1', '2000-01-01', 120, 1);
INSERT INTO "films"  ("title", "description" , "release_date", "duration", "rating_id")
VALUES ('film2', 'description2', '2000-01-01', 180, 2);
INSERT INTO "films"  ("title", "description" , "release_date", "duration", "rating_id")
VALUES ('film3', 'description3', '2000-01-01', 240, 3);
-- add genres to films
INSERT INTO "film_genre" ("film_id", "genre_id") VALUES (1, 1);
INSERT INTO "film_genre" ("film_id", "genre_id") VALUES (1, 3);
INSERT INTO "film_genre" ("film_id", "genre_id") VALUES (2, 2);
INSERT INTO "film_genre" ("film_id", "genre_id") VALUES (3, 2);
INSERT INTO "film_genre" ("film_id", "genre_id") VALUES (3, 1);