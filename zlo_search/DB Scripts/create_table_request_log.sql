

CREATE TABLE request_log (
    id INT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    host VARCHAR(100),
    user_agent VARCHAR(100),
    req_text VARCHAR(200),
    req_query VARCHAR(200),
    referer VARCHAR(100),
    req_date DATETIME
);

--ALTER TABLE `zlo_storage`.`request_log` CHANGE `host` host VARCHAR(100),
--  CHANGE `user_agent` user_agent VARCHAR(100),
--  CHANGE `req_text` req_text VARCHAR(200),
--  CHANGE `req_query` req_query VARCHAR(200),
--  CHANGE `referer` referer VARCHAR(100);
