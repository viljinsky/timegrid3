-- дни недели
delete from day_list;

insert into day_list (day_no,day_short_name,day_caption) values (1,'Пн','Понедельник'); 
insert into day_list (day_no,day_short_name,day_caption) values (2,'Вт','Вторник');
insert into day_list (day_no,day_short_name,day_caption) values (3,'Ср','Среда');

insert into day_list (day_no,day_short_name,day_caption) values (4,'Чт','Четверг'); 
insert into day_list (day_no,day_short_name,day_caption) values (5,'Пт','Пятница');
insert into day_list (day_no,day_short_name,day_caption) values (6,'Сб','Суббота');
insert into day_list (day_no,day_short_name,day_caption) values (7,'Вс','Воскресенье');

select * from day_list;

-- Расписание звонков
delete from bell_list;
insert into bell_list (bell_id,time_start,time_end) values (1,'9:00','9:45');
insert into bell_list (bell_id,time_start,time_end) values (2,'10:00','10:45');
insert into bell_list (bell_id,time_start,time_end) values (3,'11:00','11:45');
insert into bell_list (bell_id,time_start,time_end) values (4,'12:00','12:45');
insert into bell_list (bell_id,time_start,time_end) values (5,'13:00','13:45');
insert into bell_list (bell_id,time_start,time_end) values (6,'14:00','14:45');
insert into bell_list (bell_id,time_start,time_end) values (7,'15:00','14:45');
insert into bell_list (bell_id,time_start,time_end) values (8,'16:00','16:45');
insert into bell_list (bell_id,time_start,time_end) values (9,'17:00','17:45');
insert into bell_list (bell_id,time_start,time_end) values (10,'18:00','18:45');
insert into bell_list (bell_id,time_start,time_end) values (11,'19:00','19:45');
insert into bell_list (bell_id,time_start,time_end) values (12,'20:00','20:45');
select * from bell_list;