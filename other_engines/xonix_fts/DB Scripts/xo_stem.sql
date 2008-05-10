DROP FUNCTION IF EXISTS xo_stem;
CREATE FUNCTION xo_stem(word varchar(100)) RETURNS varchar(100) CHARSET cp1251
BEGIN
	-- by now return word itself
	RETURN word;
END;
