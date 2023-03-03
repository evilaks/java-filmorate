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
    NAME VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS RATING (
    ID INTEGER PRIMARY KEY AUTO_INCREMENT,
    NAME VARCHAR(10) NOT NULL UNIQUE
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

CREATE TABLE IF NOT EXISTS REVIEWS (
    ID INTEGER PRIMARY KEY AUTO_INCREMENT,
    CONTENT VARCHAR(2500),
    IS_POSITIVE BOOL,
    USER_ID INTEGER,
    FILM_ID INTEGER
);

CREATE TABLE IF NOT EXISTS REVIEW_MARKS (
    REVIEW_ID INTEGER,
    USER_ID INTEGER,
    IS_USEFUL BOOL,
    CONSTRAINT REVIEWS_MARKS_KEY PRIMARY KEY (REVIEW_ID, USER_ID)
);

CREATE TABLE IF NOT EXISTS EVENT_FEED (
   TIME timestamp NOT NULL,
   USER_ID int REFERENCES USERS (ID) NOT NULL,
   EVENT_TYPE VARCHAR(50) NOT NULL,
   OPERATION VARCHAR(50) NOT NULL,
   EVENT_ID int NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
   ENTITY_ID int NOT NULL,
   CONSTRAINT pk_EVENT_FEED PRIMARY KEY (EVENT_ID)
);
