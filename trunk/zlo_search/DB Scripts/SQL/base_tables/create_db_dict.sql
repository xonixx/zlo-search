
CREATE TABLE db_dict (
    var_name VARCHAR(30) UNIQUE PRIMARY KEY,

    var_type INT UNSIGNED,

    int_val INT,
    txt_val TEXT,
    bool_val BOOL,
    date_val DATETIME,

    date_created DATETIME,
    date_changed DATETIME
)
ENGINE=INNODB
DEFAULT CHARSET=cp1251;