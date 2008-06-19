

--use backup_storage;

DROP procedure IF EXISTS perform_backup;
create procedure perform_backup()
BEGIN

    DECLARE last_num int;

    SET last_num = (select if(max(num) is null, 0, max(num)) from messages_zlo);
    insert into messages_zlo
    select * from zlo_storage.messages m where m.num > last_num;
    select 'zlo', ROW_COUNT();

    SET last_num = (select if(max(num) is null, 0, max(num)) from messages_velo);
    insert into messages_velo
    select * from velo_storage.messages m where m.num > last_num;
    select 'velo', ROW_COUNT();

    SET last_num = (select if(max(num) is null, 0, max(num)) from messages_anime);
    insert into messages_anime
    select * from anime_storage.messages m where m.num > last_num;
    select 'anime', ROW_COUNT();

    SET last_num = (select if(max(num) is null, 0, max(num)) from messages_games);
    insert into messages_games
    select * from games_storage.messages m where m.num > last_num;
    select 'games', ROW_COUNT();

    SET last_num = (select if(max(num) is null, 0, max(num)) from messages_sport);
    insert into messages_sport
    select * from sport_storage.messages m where m.num > last_num;
    select 'sport', ROW_COUNT();

    SET last_num = (select if(max(num) is null, 0, max(num)) from messages_dev);
    insert into messages_dev
    select * from dev_storage.messages m where m.num > last_num;
    select 'dev', ROW_COUNT();

    SET last_num = (select if(max(num) is null, 0, max(num)) from messages_takeoff);
    insert into messages_takeoff
    select * from takeoff_storage.messages m where m.num > last_num;
    select 'takeoff', ROW_COUNT();

END