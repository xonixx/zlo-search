USE mysql;

DROP DATABASE IF EXISTS zlostorage;
CREATE DATABASE `zlostorage` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin;

USE zlostorage;

CREATE TABLE messages (id INT UNIQUE PRIMARY KEY,
                       topic CHAR(255) NOT NULL,
                       title CHAR(255) NOT NULL,
                       nick CHAR(255) NOT NULL,
                       mdate TIMESTAMP NOT NULL,
                       body TEXT);       

