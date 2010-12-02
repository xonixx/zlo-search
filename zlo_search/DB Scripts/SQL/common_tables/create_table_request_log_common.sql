-- use mysql;
-- create database searcher_logs;
-- use searcher_logs;

-- drop table if exists request_log;

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

ALTER TABLE request_log
  ADD is_admin_req BOOL;

-- update request_log set is_admin_req = 0 where is_admin_req is null;  
    
--update request_log set is_rss_req=if(req_query_str like 'rss&%', 1, 0)