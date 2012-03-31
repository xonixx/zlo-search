
CREATE TABLE messages (
  num INT UNIQUE PRIMARY KEY COMMENT 'message id',
  parentNum INT COMMENT 'parent message id',
  host VARCHAR(100),
  topicCode TINYINT,
  title VARCHAR(255),
  nick VARCHAR(100) COMMENT 'message username',
  user_id VARCHAR(255) NULL COMMENT 'the user_id for forming user profile link',
  altName VARCHAR(100),
  msgDate DATETIME,
  reg BOOL COMMENT 'reg/unreg',
  status TINYINT COMMENT 'ok/deleted',
  body TEXT,

  KEY (parentNum),
  KEY (msgDate) -- COMMENT 'for statistics'
)
ENGINE=INNODB
DEFAULT CHARSET=cp1251;
