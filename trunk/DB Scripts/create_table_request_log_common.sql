use mysql;
create database searcher_logs;
use searcher_logs;

CREATE TABLE request_log (
    id INT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    site INT,
    host VARCHAR(100),
    user_agent VARCHAR(100),
    req_text VARCHAR(200),
    req_query VARCHAR(200),
    referer VARCHAR(100),
    req_date DATETIME
);