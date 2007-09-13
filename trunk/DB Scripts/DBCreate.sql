USE mysql;

DROP DATABASE IF EXISTS zlostorage;
CREATE DATABASE `zlostorage` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin;

USE zlostorage;

CREATE TABLE messages (num INT UNIQUE PRIMARY KEY,
		       host CHAR(255) NOT NULL,
                       topic CHAR(255) NOT NULL,
                       title CHAR(255) NOT NULL,
                       nick CHAR(255) NOT NULL,
                       date TIMESTAMP NOT NULL,
		       reg BOOL,
                       body TEXT);
