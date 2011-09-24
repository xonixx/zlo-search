

USE takeoff_storage;

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
INSERT INTO topics (id, name, isNew) VALUES (1, 'Тренировка',            1);
INSERT INTO topics (id, name, isNew) VALUES (2, 'Поход',           1);
INSERT INTO topics (id, name, isNew) VALUES (3, 'Соревнования',         1);
INSERT INTO topics (id, name, isNew) VALUES (4, 'Мурзилка',       1);
INSERT INTO topics (id, name, isNew) VALUES (5, 'Потеряно/найдено',          1);
INSERT INTO topics (id, name, isNew) VALUES (6, 'Продам',            1);
INSERT INTO topics (id, name, isNew) VALUES (7, 'Куплю',      1);
INSERT INTO topics (id, name, isNew) VALUES (8, 'Temp',     1);
INSERT INTO topics (id, name, isNew) VALUES (9, 'Склад', 1);
INSERT INTO topics (id, name, isNew) VALUES (10, 'Снаряжение',           1);
INSERT INTO topics (id, name, isNew) VALUES (11, 'Тупой вопрос',          1);
INSERT INTO topics (id, name, isNew) VALUES (12, 'Движок борды',          1);
INSERT INTO topics (id, name, isNew) VALUES (13, 'Видео',         1);

-- old
