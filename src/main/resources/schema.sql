CREATE TABLE IF NOT EXISTS "users" (
  "id" integer PRIMARY KEY AUTO_INCREMENT,
  "name" varchar(255) NOT NULL,
  "login" varchar(255) NOT NULL,
  "email" varchar(255) NOT NULL,
  "birthday" date
);
DELETE FROM "users";
ALTER TABLE "users" ALTER COLUMN "id" RESTART WITH 1;

CREATE TABLE IF NOT EXISTS "friendship_requests" (
  "user_id" integer REFERENCES "users" ("id") NOT NULL,
  "friend_id" integer REFERENCES "users" ("id") NOT NULL,
  "is_approved" bool,
  CONSTRAINT friendship_key PRIMARY KEY ("user_id", "friend_id")
);
DELETE FROM "friendship_requests";

CREATE TABLE IF NOT EXISTS "genre" (
  "id" integer PRIMARY KEY AUTO_INCREMENT,
  "name" varchar(255) NOT NULL
);
DELETE FROM "genre";
ALTER TABLE "genre" ALTER COLUMN "id" RESTART WITH 1;

CREATE TABLE IF NOT EXISTS "rating" (
  "id" integer PRIMARY KEY AUTO_INCREMENT,
  "name" varchar(10) NOT NULL
);
DELETE FROM "rating";
ALTER TABLE "rating" ALTER COLUMN "id" RESTART WITH 1;

CREATE TABLE IF NOT EXISTS "films" (
  "id" integer PRIMARY KEY AUTO_INCREMENT,
  "title" varchar(255) NOT NULL,
  "description" varchar(1000) NOT NULL,
  "release_date" date NOT NULL,
  "duration" integer NOT NULL,
  "rating_id" integer REFERENCES "rating" ("id")
);
DELETE FROM "films";
ALTER TABLE "films" ALTER COLUMN "id" RESTART WITH 1;

CREATE TABLE IF NOT EXISTS "film_genre" (
  "film_id" integer REFERENCES "films" ("id") NOT NULL,
  "genre_id" integer REFERENCES "genre" ("id") NOT NULL,
  CONSTRAINT film_genre_key PRIMARY KEY ("film_id", "genre_id")
);
DELETE FROM "film_genre";

CREATE TABLE IF NOT EXISTS "likes" (
  "film_id" integer REFERENCES "films" ("id") NOT NULL,
  "user_id" integer REFERENCES "users" ("id") NOT NULL,
  CONSTRAINT likes_key PRIMARY KEY ("film_id", "user_id")
);
DELETE FROM "likes";