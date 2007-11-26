USE mysql;

DROP DATABASE IF EXISTS `zlo_storage`;
CREATE DATABASE `zlo_storage` DEFAULT CHARACTER SET cp1251;

USE `zlo_storage`;

CREATE TABLE messages (num INT UNIQUE PRIMARY KEY,
                        parentNum INT,
                        host VARCHAR(100),
                        topicCode TINYINT,
                        title VARCHAR(255),
                        nick VARCHAR(100),
                        altName VARCHAR(100),
                        msgDate DATETIME,
                        reg BOOL,
                        body TEXT,
                        status TINYINT,
                        indexed BOOL DEFAULT 0);

ALTER TABLE zlo_storage.messages
  ADD INDEX indexed (indexed);

-- if indexed column is absent                        
-- ALTER TABLE messages
-- ADD indexed BOOL DEFAULT 0;

--ALTER TABLE `zlo_storage`.`messages` CHANGE `host` host VARCHAR(100),
--  CHANGE `title` title VARCHAR(255),
--  CHANGE `nick` nick VARCHAR(100),
--  CHANGE `altName` altName VARCHAR(100);


