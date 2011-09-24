


USE np_storage;

-- create
CREATE TABLE topics (id INT,
                        name CHAR(50),
                        isNew BOOL
                    );

-- index
ALTER TABLE topics
  ADD INDEX (id);

-- insert
INSERT INTO topics (id, name, isNew) VALUES (0, 'без темы',         1);
INSERT INTO topics (id, name, isNew) VALUES (1, 'Кто здесь?',           1);

-- old