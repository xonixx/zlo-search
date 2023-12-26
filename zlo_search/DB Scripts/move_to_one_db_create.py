
out = open("move_to_one_db_generated.sql", "w")
#rnm_old = open("rename_old_dbs.sql", 'w')
#rnm_old_back = open("rename_old_dbs_back.sql", 'w')

sites = 'anime dev games np sport takeoff velo zlo'.split()

out.write('''

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
''')

# not supported
#rnm_old.write('RENAME DATABASE searcher_logs TO old_searcher_logs;\n')
#rnm_old_back.write('RENAME DATABASE old_searcher_logs TO searcher_logs;\n')

for site in sites:
#    rnm_old.write('RENAME DATABASE %(site)s_storage TO old_%(site)s_storage;\n' % vars())
#    rnm_old_back.write('RENAME DATABASE old_%(site)s_storage TO %(site)s_storage;\n' % vars())
    out.write('''
--################################################################

-- %(site)s
CREATE TABLE %(site)s_messages (num INT UNIQUE PRIMARY KEY,
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

ALTER TABLE %(site)s_messages ADD INDEX (msgDate); -- for statistics

INSERT INTO %(site)s_messages SELECT * from %(site)s_storage.messages;

-- create
CREATE TABLE %(site)s_topics (id INT,
                        name CHAR(50),
                        isNew BOOL);

-- index
ALTER TABLE %(site)s_topics ADD INDEX (id);

INSERT INTO %(site)s_topics SELECT * FROM %(site)s_storage.topics;

CREATE TABLE %(site)s_db_dict (
    var_name VARCHAR(30) UNIQUE PRIMARY KEY,

    var_type INT UNSIGNED,

    int_val INT,
    txt_val TEXT,
    bool_val BOOL,
    date_val DATETIME,

    date_created DATETIME,
    date_changed DATETIME
);
INSERT INTO %(site)s_db_dict SELECT * FROM %(site)s_storage.db_dict;

CREATE TABLE %(site)s_nickhost (
                        nick VARCHAR(100),
                        host VARCHAR(100),
                        reg BOOL,
                        cnt int);

-- idx
ALTER TABLE %(site)s_nickhost ADD INDEX idx_nick (nick);
ALTER TABLE %(site)s_nickhost ADD INDEX idx_host (host);

-- initial
insert into %(site)s_nickhost (nick, host, reg, cnt)
select nick, host, reg, count(*) cnt from %(site)s_messages
group by nick, host, reg;

-- trigger
DROP TRIGGER IF EXISTS %(site)s_trigger_nickhost;
CREATE TRIGGER %(site)s_trigger_nickhost BEFORE INSERT
    ON %(site)s_messages FOR EACH ROW
BEGIN
	IF (EXISTS (SELECT 1 FROM %(site)s_nickhost WHERE nick=NEW.nick and host=NEW.host))
	THEN
		UPDATE %(site)s_nickhost SET cnt=cnt+1, reg=NEW.reg WHERE nick=NEW.nick and host=NEW.host;
	ELSE
		INSERT INTO %(site)s_nickhost (nick, host, reg, cnt) VALUES (NEW.nick, NEW.host, NEW.reg, 1);
	END IF;
END;
-- END %(site)s            
''' % vars())

out.close()

