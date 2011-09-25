
DROP TABLE IF EXISTS autocomplete;
CREATE TABLE autocomplete (
  search_text VARCHAR(255),
  search_date NOT NULL DEFAULT NOW(),
  cnt INT NOT NULL DEFAULT 1
  )
ENGINE=INNODB
DEFAULT CHARSET=cp1251;