

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

INSERT INTO request_log SELECT * FROM searcher_logs.request_log;

--################################################################

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
select nick, host, reg, count(*) cnt from anime_messages
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

--################################################################

-- dev
CREATE TABLE dev_messages (num INT UNIQUE PRIMARY KEY,
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

ALTER TABLE dev_messages ADD INDEX (msgDate); -- for statistics

INSERT INTO dev_messages SELECT * from dev_storage.messages;

-- create
CREATE TABLE dev_topics (id INT,
                        name CHAR(50),
                        isNew BOOL);

-- index
ALTER TABLE dev_topics ADD INDEX (id);

INSERT INTO dev_topics SELECT * FROM dev_storage.topics;

CREATE TABLE dev_db_dict (
    var_name VARCHAR(30) UNIQUE PRIMARY KEY,

    var_type INT UNSIGNED,

    int_val INT,
    txt_val TEXT,
    bool_val BOOL,
    date_val DATETIME,

    date_created DATETIME,
    date_changed DATETIME
);
INSERT INTO dev_db_dict SELECT * FROM dev_storage.db_dict;

CREATE TABLE dev_nickhost (
                        nick VARCHAR(100),
                        host VARCHAR(100),
                        reg BOOL,
                        cnt int);

-- idx
ALTER TABLE dev_nickhost ADD INDEX idx_nick (nick);
ALTER TABLE dev_nickhost ADD INDEX idx_host (host);

-- initial
insert into dev_nickhost (nick, host, reg, cnt)
select nick, host, reg, count(*) cnt from dev_messages
group by nick, host;

-- trigger
DROP TRIGGER IF EXISTS dev_trigger_nickhost;
CREATE TRIGGER dev_trigger_nickhost BEFORE INSERT
    ON dev_messages FOR EACH ROW
BEGIN
	IF (EXISTS (SELECT 1 FROM dev_nickhost WHERE nick=NEW.nick and host=NEW.host))
	THEN
		UPDATE dev_nickhost SET cnt=cnt+1, reg=NEW.reg WHERE nick=NEW.nick and host=NEW.host;
	ELSE
		INSERT INTO dev_nickhost (nick, host, reg, cnt) VALUES (NEW.nick, NEW.host, NEW.reg, 1);
	END IF;
END;
-- END dev            

--################################################################

-- games
CREATE TABLE games_messages (num INT UNIQUE PRIMARY KEY,
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

ALTER TABLE games_messages ADD INDEX (msgDate); -- for statistics

INSERT INTO games_messages SELECT * from games_storage.messages;

-- create
CREATE TABLE games_topics (id INT,
                        name CHAR(50),
                        isNew BOOL);

-- index
ALTER TABLE games_topics ADD INDEX (id);

INSERT INTO games_topics SELECT * FROM games_storage.topics;

CREATE TABLE games_db_dict (
    var_name VARCHAR(30) UNIQUE PRIMARY KEY,

    var_type INT UNSIGNED,

    int_val INT,
    txt_val TEXT,
    bool_val BOOL,
    date_val DATETIME,

    date_created DATETIME,
    date_changed DATETIME
);
INSERT INTO games_db_dict SELECT * FROM games_storage.db_dict;

CREATE TABLE games_nickhost (
                        nick VARCHAR(100),
                        host VARCHAR(100),
                        reg BOOL,
                        cnt int);

-- idx
ALTER TABLE games_nickhost ADD INDEX idx_nick (nick);
ALTER TABLE games_nickhost ADD INDEX idx_host (host);

-- initial
insert into games_nickhost (nick, host, reg, cnt)
select nick, host, reg, count(*) cnt from games_messages
group by nick, host;

-- trigger
DROP TRIGGER IF EXISTS games_trigger_nickhost;
CREATE TRIGGER games_trigger_nickhost BEFORE INSERT
    ON games_messages FOR EACH ROW
BEGIN
	IF (EXISTS (SELECT 1 FROM games_nickhost WHERE nick=NEW.nick and host=NEW.host))
	THEN
		UPDATE games_nickhost SET cnt=cnt+1, reg=NEW.reg WHERE nick=NEW.nick and host=NEW.host;
	ELSE
		INSERT INTO games_nickhost (nick, host, reg, cnt) VALUES (NEW.nick, NEW.host, NEW.reg, 1);
	END IF;
END;
-- END games            

--################################################################

-- np
CREATE TABLE np_messages (num INT UNIQUE PRIMARY KEY,
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

ALTER TABLE np_messages ADD INDEX (msgDate); -- for statistics

INSERT INTO np_messages SELECT * from np_storage.messages;

-- create
CREATE TABLE np_topics (id INT,
                        name CHAR(50),
                        isNew BOOL);

-- index
ALTER TABLE np_topics ADD INDEX (id);

INSERT INTO np_topics SELECT * FROM np_storage.topics;

CREATE TABLE np_db_dict (
    var_name VARCHAR(30) UNIQUE PRIMARY KEY,

    var_type INT UNSIGNED,

    int_val INT,
    txt_val TEXT,
    bool_val BOOL,
    date_val DATETIME,

    date_created DATETIME,
    date_changed DATETIME
);
INSERT INTO np_db_dict SELECT * FROM np_storage.db_dict;

CREATE TABLE np_nickhost (
                        nick VARCHAR(100),
                        host VARCHAR(100),
                        reg BOOL,
                        cnt int);

-- idx
ALTER TABLE np_nickhost ADD INDEX idx_nick (nick);
ALTER TABLE np_nickhost ADD INDEX idx_host (host);

-- initial
insert into np_nickhost (nick, host, reg, cnt)
select nick, host, reg, count(*) cnt from np_messages
group by nick, host;

-- trigger
DROP TRIGGER IF EXISTS np_trigger_nickhost;
CREATE TRIGGER np_trigger_nickhost BEFORE INSERT
    ON np_messages FOR EACH ROW
BEGIN
	IF (EXISTS (SELECT 1 FROM np_nickhost WHERE nick=NEW.nick and host=NEW.host))
	THEN
		UPDATE np_nickhost SET cnt=cnt+1, reg=NEW.reg WHERE nick=NEW.nick and host=NEW.host;
	ELSE
		INSERT INTO np_nickhost (nick, host, reg, cnt) VALUES (NEW.nick, NEW.host, NEW.reg, 1);
	END IF;
END;
-- END np            

--################################################################

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
select nick, host, reg, count(*) cnt from sport_messages
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

--################################################################

-- takeoff
CREATE TABLE takeoff_messages (num INT UNIQUE PRIMARY KEY,
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

ALTER TABLE takeoff_messages ADD INDEX (msgDate); -- for statistics

INSERT INTO takeoff_messages SELECT * from takeoff_storage.messages;

-- create
CREATE TABLE takeoff_topics (id INT,
                        name CHAR(50),
                        isNew BOOL);

-- index
ALTER TABLE takeoff_topics ADD INDEX (id);

INSERT INTO takeoff_topics SELECT * FROM takeoff_storage.topics;

CREATE TABLE takeoff_db_dict (
    var_name VARCHAR(30) UNIQUE PRIMARY KEY,

    var_type INT UNSIGNED,

    int_val INT,
    txt_val TEXT,
    bool_val BOOL,
    date_val DATETIME,

    date_created DATETIME,
    date_changed DATETIME
);
INSERT INTO takeoff_db_dict SELECT * FROM takeoff_storage.db_dict;

CREATE TABLE takeoff_nickhost (
                        nick VARCHAR(100),
                        host VARCHAR(100),
                        reg BOOL,
                        cnt int);

-- idx
ALTER TABLE takeoff_nickhost ADD INDEX idx_nick (nick);
ALTER TABLE takeoff_nickhost ADD INDEX idx_host (host);

-- initial
insert into takeoff_nickhost (nick, host, reg, cnt)
select nick, host, reg, count(*) cnt from takeoff_messages
group by nick, host;

-- trigger
DROP TRIGGER IF EXISTS takeoff_trigger_nickhost;
CREATE TRIGGER takeoff_trigger_nickhost BEFORE INSERT
    ON takeoff_messages FOR EACH ROW
BEGIN
	IF (EXISTS (SELECT 1 FROM takeoff_nickhost WHERE nick=NEW.nick and host=NEW.host))
	THEN
		UPDATE takeoff_nickhost SET cnt=cnt+1, reg=NEW.reg WHERE nick=NEW.nick and host=NEW.host;
	ELSE
		INSERT INTO takeoff_nickhost (nick, host, reg, cnt) VALUES (NEW.nick, NEW.host, NEW.reg, 1);
	END IF;
END;
-- END takeoff            

--################################################################

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
select nick, host, reg, count(*) cnt from velo_messages
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

--################################################################

-- zlo
CREATE TABLE zlo_messages (num INT UNIQUE PRIMARY KEY,
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

ALTER TABLE zlo_messages ADD INDEX (msgDate); -- for statistics

INSERT INTO zlo_messages SELECT * from zlo_storage.messages;

-- create
CREATE TABLE zlo_topics (id INT,
                        name CHAR(50),
                        isNew BOOL);

-- index
ALTER TABLE zlo_topics ADD INDEX (id);

INSERT INTO zlo_topics SELECT * FROM zlo_storage.topics;

CREATE TABLE zlo_db_dict (
    var_name VARCHAR(30) UNIQUE PRIMARY KEY,

    var_type INT UNSIGNED,

    int_val INT,
    txt_val TEXT,
    bool_val BOOL,
    date_val DATETIME,

    date_created DATETIME,
    date_changed DATETIME
);
INSERT INTO zlo_db_dict SELECT * FROM zlo_storage.db_dict;

CREATE TABLE zlo_nickhost (
                        nick VARCHAR(100),
                        host VARCHAR(100),
                        reg BOOL,
                        cnt int);

-- idx
ALTER TABLE zlo_nickhost ADD INDEX idx_nick (nick);
ALTER TABLE zlo_nickhost ADD INDEX idx_host (host);

-- initial
insert into zlo_nickhost (nick, host, reg, cnt)
select nick, host, reg, count(*) cnt from zlo_messages
group by nick, host;

-- trigger
DROP TRIGGER IF EXISTS zlo_trigger_nickhost;
CREATE TRIGGER zlo_trigger_nickhost BEFORE INSERT
    ON zlo_messages FOR EACH ROW
BEGIN
	IF (EXISTS (SELECT 1 FROM zlo_nickhost WHERE nick=NEW.nick and host=NEW.host))
	THEN
		UPDATE zlo_nickhost SET cnt=cnt+1, reg=NEW.reg WHERE nick=NEW.nick and host=NEW.host;
	ELSE
		INSERT INTO zlo_nickhost (nick, host, reg, cnt) VALUES (NEW.nick, NEW.host, NEW.reg, 1);
	END IF;
END;
-- END zlo            
