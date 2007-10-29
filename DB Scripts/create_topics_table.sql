

USE zlo_storage;

-- create
CREATE TABLE topics (id INT UNIQUE PRIMARY KEY,
                        name CHAR(50)
                    );

-- insert
INSERT INTO topics (id, name) VALUES (0, 'Без темы');
INSERT INTO topics (id, name) VALUES (1, 'Учеба');
INSERT INTO topics (id, name) VALUES (2, 'Работа');
INSERT INTO topics (id, name) VALUES (3, 'Мурзилка');
INSERT INTO topics (id, name) VALUES (4, 'Обсуждение');
INSERT INTO topics (id, name) VALUES (5, 'Новости');
INSERT INTO topics (id, name) VALUES (6, 'Спорт');
INSERT INTO topics (id, name) VALUES (7, 'Развлечения');
INSERT INTO topics (id, name) VALUES (8, 'Движок борды');
INSERT INTO topics (id, name) VALUES (9, 'Программирование');
INSERT INTO topics (id, name) VALUES (10, 'Куплю');
INSERT INTO topics (id, name) VALUES (11, 'Продам');
INSERT INTO topics (id, name) VALUES (12, 'Услуги');
INSERT INTO topics (id, name) VALUES (13, 'Windows');
INSERT INTO topics (id, name) VALUES (14, 'BSD/Linux');
INSERT INTO topics (id, name) VALUES (15, 'Проблемы сети');
INSERT INTO topics (id, name) VALUES (16, 'Голосование');
INSERT INTO topics (id, name) VALUES (17, 'Потеряно/Найдено');
INSERT INTO topics (id, name) VALUES (18, 'Temp');