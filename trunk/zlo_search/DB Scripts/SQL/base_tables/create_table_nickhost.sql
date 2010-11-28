
DROP TABLE IF EXISTS nickhost;
CREATE TABLE nickhost (
  nick VARCHAR(100),
  host VARCHAR(100),
  reg BOOL,
  cnt int)
ENGINE=INNODB
DEFAULT CHARSET=cp1251;  

-- idx
ALTER TABLE nickhost ADD INDEX idx_nick (nick);
ALTER TABLE nickhost ADD INDEX idx_host (host);

-- initial
insert into nickhost (nick, host, reg, cnt)
select nick, host, reg, count(1) cnt from messages
group by nick, host;

-- trigger
DROP TRIGGER IF EXISTS trigger_nickhost;
CREATE TRIGGER trigger_nickhost BEFORE INSERT
    ON messages FOR EACH ROW
BEGIN
	IF (EXISTS (SELECT 1 FROM nickhost WHERE nick=NEW.nick and host=NEW.host))
	THEN
		UPDATE nickhost SET cnt=cnt+1, reg=NEW.reg WHERE nick=NEW.nick and host=NEW.host;
	ELSE
		INSERT INTO nickhost (nick, host, reg, cnt) VALUES (NEW.nick, NEW.host, New.reg, 1);
	END IF;
END;