
update messages set nick=trim(unescapeHtml(nick));

delete from nickhost;

insert into nickhost (nick, host, reg, cnt)
select nick, host, reg, count(1) cnt from messages
group by nick, host, reg;