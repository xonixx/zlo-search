
DROP FUNCTION IF EXISTS unescapeHtml;

CREATE FUNCTION unescapeHtml (s varchar(500))
RETURNS varchar(500)

BEGIN

	SET s = REPLACE(s, '&lt;', '<');
	SET s = REPLACE(s, '&gt;', '>');
	SET s = REPLACE(s, '&amp;', '&');
	SET s = REPLACE(s, '&quot;', '"');

	RETURN s;
END
