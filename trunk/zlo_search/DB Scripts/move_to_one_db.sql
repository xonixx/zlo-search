
DROP DATABASE xonix_search;
CREATE DATABASE xonix_search;
USE xonix_search;

-- request_log
CREATE TABLE request_log (
    id INT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    site INT,
    host VARCHAR(100),
    user_agent VARCHAR(100),
    req_text VARCHAR(200),
    req_nick VARCHAR(100),
    req_host VARCHAR(100),
    req_query_str VARCHAR(400),
    req_query VARCHAR(200),
    referer VARCHAR(100),
    req_date DATETIME
);

ALTER TABLE request_log
 CHANGE user_agent user_agent VARCHAR(200);

ALTER TABLE request_log
    ADD is_rss_req BOOL;

ALTER TABLE request_log
  ADD INDEX req_date_idx (req_date);

-- INSERT INTO request_log SELECT * FROM searcher_logs.request_log;

--#################################################################

-- anime
CREATE TABLE anime_messages (num INT UNIQUE PRIMARY KEY,
                        parentNum INT,
                        host VARCHAR(100),
                        topicCode TINYINT,
                        title VARCHAR(255),
                        nick VARCHAR(100),
                        altName VARCHAR(100),
                        msgDate DATETIME,
                        reg BOOL,
                        body TEXT,
                        status TINYINT);

ALTER TABLE anime_messages ADD INDEX (msgDate); -- for statistics

INSERT INTO anime_messages SELECT * from anime_storage.messages;

-- create
CREATE TABLE anime_topics (id INT,
                        name CHAR(50),
                        isNew BOOL);

-- index
ALTER TABLE anime_topics ADD INDEX (id);

INSERT INTO anime_topics SELECT * FROM anime_storage.topics;

CREATE TABLE anime_db_dict (
    var_name VARCHAR(30) UNIQUE PRIMARY KEY,

    var_type INT UNSIGNED,

    int_val INT,
    txt_val TEXT,
    bool_val BOOL,
    date_val DATETIME,

    date_created DATETIME,
    date_changed DATETIME
);
INSERT INTO anime_db_dict SELECT * FROM anime_storage.db_dict;

CREATE TABLE anime_nickhost (
                        nick VARCHAR(100),
                        host VARCHAR(100),
                        reg BOOL,
                        cnt int);

-- idx
ALTER TABLE anime_nickhost ADD INDEX idx_nick (nick);
ALTER TABLE anime_nickhost ADD INDEX idx_host (host);

-- initial
insert into anime_nickhost (nick, host, reg, cnt)
select nick, host, reg, count(1) cnt from anime_messages
group by nick, host;

-- trigger
DROP TRIGGER IF EXISTS anime_trigger_nickhost;
CREATE TRIGGER anime_trigger_nickhost BEFORE INSERT
    ON anime_messages FOR EACH ROW
BEGIN
	IF (EXISTS (SELECT 1 FROM anime_nickhost WHERE nick=NEW.nick and host=NEW.host))
	THEN
		UPDATE anime_nickhost SET cnt=cnt+1, reg=NEW.reg WHERE nick=NEW.nick and host=NEW.host;
	ELSE
		INSERT INTO anime_nickhost (nick, host, reg, cnt) VALUES (NEW.nick, NEW.host, NEW.reg, 1);
	END IF;
END;
-- END anime

--#################################################################

-- velo
CREATE TABLE velo_messages (num INT UNIQUE PRIMARY KEY,
                        parentNum INT,
                        host VARCHAR(100),
                        topicCode TINYINT,
                        title VARCHAR(255),
                        nick VARCHAR(100),
                        altName VARCHAR(100),
                        msgDate DATETIME,
                        reg BOOL,
                        body TEXT,
                        status TINYINT);

ALTER TABLE velo_messages ADD INDEX (msgDate); -- for statistics

INSERT INTO velo_messages SELECT * from velo_storage.messages;

-- create
CREATE TABLE velo_topics (id INT,
                        name CHAR(50),
                        isNew BOOL);

-- index
ALTER TABLE velo_topics ADD INDEX (id);

INSERT INTO velo_topics SELECT * FROM velo_storage.topics;

CREATE TABLE velo_db_dict (
    var_name VARCHAR(30) UNIQUE PRIMARY KEY,

    var_type INT UNSIGNED,

    int_val INT,
    txt_val TEXT,
    bool_val BOOL,
    date_val DATETIME,

    date_created DATETIME,
    date_changed DATETIME
);
INSERT INTO velo_db_dict SELECT * FROM velo_storage.db_dict;

CREATE TABLE velo_nickhost (
                        nick VARCHAR(100),
                        host VARCHAR(100),
                        reg BOOL,
                        cnt int);

-- idx
ALTER TABLE velo_nickhost ADD INDEX idx_nick (nick);
ALTER TABLE velo_nickhost ADD INDEX idx_host (host);

-- initial
insert into velo_nickhost (nick, host, reg, cnt)
select nick, host, reg, count(1) cnt from velo_messages
group by nick, host;

-- trigger
DROP TRIGGER IF EXISTS velo_trigger_nickhost;
CREATE TRIGGER velo_trigger_nickhost BEFORE INSERT
    ON velo_messages FOR EACH ROW
BEGIN
	IF (EXISTS (SELECT 1 FROM velo_nickhost WHERE nick=NEW.nick and host=NEW.host))
	THEN
		UPDATE velo_nickhost SET cnt=cnt+1, reg=NEW.reg WHERE nick=NEW.nick and host=NEW.host;
	ELSE
		INSERT INTO velo_nickhost (nick, host, reg, cnt) VALUES (NEW.nick, NEW.host, NEW.reg, 1);
	END IF;
END;
-- END velo

--#################################################################

-- sport
CREATE TABLE sport_messages (num INT UNIQUE PRIMARY KEY,
                        parentNum INT,
                        host VARCHAR(100),
                        topicCode TINYINT,
                        title VARCHAR(255),
                        nick VARCHAR(100),
                        altName VARCHAR(100),
                        msgDate DATETIME,
                        reg BOOL,
                        body TEXT,
                        status TINYINT);

ALTER TABLE sport_messages ADD INDEX (msgDate); -- for statistics

INSERT INTO sport_messages SELECT * from sport_storage.messages;

-- create
CREATE TABLE sport_topics (id INT,
                        name CHAR(50),
                        isNew BOOL);

-- index
ALTER TABLE sport_topics ADD INDEX (id);

INSERT INTO sport_topics SELECT * FROM sport_storage.topics;

CREATE TABLE sport_db_dict (
    var_name VARCHAR(30) UNIQUE PRIMARY KEY,

    var_type INT UNSIGNED,

    int_val INT,
    txt_val TEXT,
    bool_val BOOL,
    date_val DATETIME,

    date_created DATETIME,
    date_changed DATETIME
);
INSERT INTO sport_db_dict SELECT * FROM sport_storage.db_dict;

CREATE TABLE sport_nickhost (
                        nick VARCHAR(100),
                        host VARCHAR(100),
                        reg BOOL,
                        cnt int);

-- idx
ALTER TABLE sport_nickhost ADD INDEX idx_nick (nick);
ALTER TABLE sport_nickhost ADD INDEX idx_host (host);

-- initial
insert into sport_nickhost (nick, host, reg, cnt)
select nick, host, reg, count(1) cnt from sport_messages
group by nick, host;

-- trigger
DROP TRIGGER IF EXISTS sport_trigger_nickhost;
CREATE TRIGGER sport_trigger_nickhost BEFORE INSERT
    ON sport_messages FOR EACH ROW
BEGIN
	IF (EXISTS (SELECT 1 FROM sport_nickhost WHERE nick=NEW.nick and host=NEW.host))
	THEN
		UPDATE sport_nickhost SET cnt=cnt+1, reg=NEW.reg WHERE nick=NEW.nick and host=NEW.host;
	ELSE
		INSERT INTO sport_nickhost (nick, host, reg, cnt) VALUES (NEW.nick, NEW.host, NEW.reg, 1);
	END IF;
END;
-- END sport

--#################################################################