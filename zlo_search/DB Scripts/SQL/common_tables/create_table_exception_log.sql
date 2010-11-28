
-- String exception, String stackTrace, String msg, String source, String category

CREATE TABLE exception_log (
    id INT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    exception_string VARCHAR(512) NOT NULL,
    stack_trace TEXT NULL,
    msg VARCHAR(1024) NULL,
    source VARCHAR(100) NULL,
    category VARCHAR(25) NOT NULL,
    date_created DATETIME NOT NULL
)
ENGINE=INNODB
DEFAULT CHARSET=cp1251;
