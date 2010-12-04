

-- create
CREATE TABLE dolgopa_topics (
  id INT NOT NULL PRIMARY KEY,
  name CHAR(50),
  isNew BOOL
  )
ENGINE=INNODB
DEFAULT CHARSET=cp1251;

-- index
-- ALTER TABLE x_topics
--  ADD INDEX (id);

-- insert
INSERT INTO dolgopa_topics (id, name, isNew) VALUES (0, 'без темы',         1);
INSERT INTO dolgopa_topics (id, name, isNew) VALUES (1, 'Новости',            1);
INSERT INTO dolgopa_topics (id, name, isNew) VALUES (2, 'Вопрос',           1);
