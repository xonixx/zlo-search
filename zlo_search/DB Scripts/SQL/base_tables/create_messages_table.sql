
CREATE TABLE messages (
  num INT UNIQUE PRIMARY KEY,
  parentNum INT,
  host VARCHAR(100),
  topicCode TINYINT,
  title VARCHAR(255),
  nick VARCHAR(100),
  altName VARCHAR(100),
  msgDate DATETIME,
  reg BOOL,
  body TEXT,
  status TINYINT)
ENGINE=INNODB
DEFAULT CHARSET=cp1251;

ALTER TABLE messages ADD INDEX (msgDate); -- for statistics
