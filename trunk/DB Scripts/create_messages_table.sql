USE mysql;

DROP DATABASE IF EXISTS `zlo_storage`;
CREATE DATABASE `zlo_storage` DEFAULT CHARACTER SET cp1251;

USE `zlo_storage`;

CREATE TABLE messages (num INT UNIQUE PRIMARY KEY,
                        parentNum INT,
                        host CHAR(100),
                        topicCode TINYINT,
                        title CHAR(255),
                        nick CHAR(100),
                        altName CHAR(100),
                        msgDate DATETIME,
                        reg BOOL,
                        body TEXT,
                        status TINYINT,
                        indexed BOOL DEFAULT 0);

-- if indexed column is absent                        
-- ALTER TABLE messages
-- ADD indexed BOOL DEFAULT 0;

