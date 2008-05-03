use mysql;
create database searcher_logs;
use searcher_logs;

drop table if exists request_log;

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

ALTER TABLE searcher_logs.request_log
 CHANGE user_agent user_agent VARCHAR(200);
