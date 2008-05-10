DROP FUNCTION IF EXISTS xo_prepare;
CREATE FUNCTION xo_prepare (txt TEXT) RETURNS TEXT
BEGIN

	SET txt = REPLACE(txt, '.', ' ');
	SET txt = REPLACE(txt, '#', ' ');
	SET txt = REPLACE(txt, '?', ' ');
	SET txt = REPLACE(txt, '!', ' ');
	SET txt = REPLACE(txt, ',', ' ');

	WHILE LOCATE('  ', txt) > 0 DO
		SET txt = REPLACE(txt, '  ', ' ');
	END WHILE;

	RETURN txt;
END
