CREATE TABLE IF NOT EXISTS USERS (
  ID INTEGER PRIMARY KEY AUTO_INCREMENT,
  NAME VARCHAR(255) NOT NULL,
  LOGIN VARCHAR(255) NOT NULL,
  EMAIL VARCHAR(255) NOT NULL,
  BIRTHDAY DATE
);

CREATE TABLE IF NOT EXISTS FRIENDSHIP_REQUESTS (
  USER_ID INTEGER REFERENCES USERS (ID) NOT NULL,
  FRIEND_ID INTEGER REFERENCES USERS (ID) NOT NULL,
  IS_APPROVED BOOL,
  CONSTRAINT FRIENDSHIP_KEY PRIMARY KEY (USER_ID, FRIEND_ID)
);

CREATE TABLE IF NOT EXISTS GENRE (
  ID INTEGER PRIMARY KEY AUTO_INCREMENT,
  NAME VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS RATING (
  ID INTEGER PRIMARY KEY AUTO_INCREMENT,
  NAME VARCHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS FILMS (
  ID INTEGER PRIMARY KEY AUTO_INCREMENT,
  TITLE VARCHAR(255) NOT NULL,
  DESCRIPTION VARCHAR(1000) NOT NULL,
  RELEASE_DATE DATE NOT NULL,
  DURATION INTEGER NOT NULL,
  RATING_ID INTEGER REFERENCES RATING (ID)
);

CREATE TABLE IF NOT EXISTS FILM_GENRE (
  FILM_ID INTEGER REFERENCES FILMS (ID) NOT NULL,
  GENRE_ID INTEGER REFERENCES GENRE (ID) NOT NULL,
  CONSTRAINT FILM_GENRE_KEY PRIMARY KEY (FILM_ID, GENRE_ID)
);

CREATE TABLE IF NOT EXISTS LIKES (
  FILM_ID INTEGER REFERENCES FILMS (ID) NOT NULL,
  USER_ID INTEGER REFERENCES USERS (ID) NOT NULL,
  CONSTRAINT LIKES_KEY PRIMARY KEY (FILM_ID, USER_ID)
);

DELETE FROM FRIENDSHIP_REQUESTS;
DELETE FROM LIKES;
DELETE FROM FILM_GENRE;
DELETE FROM USERS;
ALTER TABLE USERS ALTER COLUMN ID RESTART WITH 1;
DELETE FROM FILMS;
ALTER TABLE FILMS ALTER COLUMN ID RESTART WITH 1;
DELETE FROM GENRE;
ALTER TABLE GENRE ALTER COLUMN ID RESTART WITH 1;
DELETE FROM RATING;
ALTER TABLE RATING ALTER COLUMN ID RESTART WITH 1;