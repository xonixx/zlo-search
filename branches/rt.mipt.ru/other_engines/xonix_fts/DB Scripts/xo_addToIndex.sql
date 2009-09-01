DROP PROCEDURE IF EXISTS xo_addToIndex;
CREATE PROCEDURE xo_addToIndex (txt TEXT, index_id INT, field_id INT, document_id INT)
BEGIN

	DECLARE DELIM VARCHAR(1);
	DECLARE word VARCHAR(100);
	DECLARE curr_index INT;
	DECLARE next_index INT;
	DECLARE len INT;

	SET DELIM = ' ';
	SET txt = xo_prepare(txt);
	SET len = LENGTH(txt);

	SET curr_index = 0;

	WHILE curr_index >= 0 AND curr_index <> len DO
		SET next_index=LOCATE(DELIM, txt, curr_index + 1);

		IF next_index = 0 THEN
			SET next_index = len;
		END IF;

		SET word = SUBSTRING(txt, curr_index + 1, next_index - curr_index);

		select word, curr_index, next_index;

		INSERT INTO xo_index (word_base, index_id, field_id, document_id)
		VALUES (word, index_id, field_id, document_id);

		SET curr_index = next_index;
	END WHILE;
END
