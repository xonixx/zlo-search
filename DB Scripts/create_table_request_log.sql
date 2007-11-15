

CREATE TABLE request_log (
    id INT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    host CHAR(100),
    user_agent CHAR(100),
    req_text CHAR(200),
    req_query CHAR(200),
    referer CHAR(100),
    req_date DATETIME
);