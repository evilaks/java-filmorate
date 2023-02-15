CREATE TABLE IF NOT EXISTS "users" (
  "id" integer PRIMARY KEY AUTO_INCREMENT,
  "name" varchar(255) NOT NULL,
  "login" varchar(255) NOT NULL,
  "email" varchar(255) NOT NULL,
  "birthday" date
);

CREATE TABLE IF NOT EXISTS "friendship_requests" (
  "user_id" integer REFERENCES "users" ("id") NOT NULL,
  "friend_id" integer REFERENCES "users" ("id") NOT NULL,
  "is_approved" bool
);

CREATE TABLE IF NOT EXISTS "genre" (
  "id" integer PRIMARY KEY AUTO_INCREMENT,
  "name" varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS "rating" (
  "id" integer PRIMARY KEY AUTO_INCREMENT,
  "name" varchar(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS "films" (
  "id" integer PRIMARY KEY AUTO_INCREMENT,
  "title" varchar(255) NOT NULL,
  "description" varchar(1000) NOT NULL,
  "release_date" date NOT NULL,
  "duration" integer NOT NULL,
  "rating_id" integer REFERENCES "rating" ("id")
);

CREATE TABLE IF NOT EXISTS "film_genre" (
  "film_id" integer REFERENCES "films" ("id") NOT NULL,
  "genre_id" integer REFERENCES "genre" ("id") NOT NULL
);

CREATE TABLE IF NOT EXISTS "likes" (
  "film_id" integer REFERENCES "films" ("id") NOT NULL,
  "user_id" integer REFERENCES "users" ("id") NOT NULL
);