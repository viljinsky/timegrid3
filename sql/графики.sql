delete from shift;
delete from shift_detail;

insert into shift (id,shift_type_id,shift_name) values (1,1,'Первая смена (мл.кл)');
insert into shift (id,shift_type_id,shift_name) values (2,1,'Первая смена');
insert into shift (id,shift_type_id,shift_name) values (3,1,'Вторая смена');
update shift_type set default_shift_id=1 where id=1;

-- Понедельник
insert into shift_detail (shift_id,day_id,bell_id) values (1,1,1);
insert into shift_detail (shift_id,day_id,bell_id) values (1,1,2);
insert into shift_detail (shift_id,day_id,bell_id) values (1,1,3);
insert into shift_detail (shift_id,day_id,bell_id) values (1,1,4);
insert into shift_detail (shift_id,day_id,bell_id) values (1,1,5);
-- Вторник
insert into shift_detail (shift_id,day_id,bell_id) values (1,2,1);
insert into shift_detail (shift_id,day_id,bell_id) values (1,2,2);
insert into shift_detail (shift_id,day_id,bell_id) values (1,2,3);
insert into shift_detail (shift_id,day_id,bell_id) values (1,2,4);
insert into shift_detail (shift_id,day_id,bell_id) values (1,2,5);
-- Среда
insert into shift_detail (shift_id,day_id,bell_id) values (1,3,1);
insert into shift_detail (shift_id,day_id,bell_id) values (1,3,2);
insert into shift_detail (shift_id,day_id,bell_id) values (1,3,3);
insert into shift_detail (shift_id,day_id,bell_id) values (1,3,4);
insert into shift_detail (shift_id,day_id,bell_id) values (1,3,5);
-- Четверг
insert into shift_detail (shift_id,day_id,bell_id) values (1,4,1);
insert into shift_detail (shift_id,day_id,bell_id) values (1,4,2);
insert into shift_detail (shift_id,day_id,bell_id) values (1,4,3);
insert into shift_detail (shift_id,day_id,bell_id) values (1,4,4);
insert into shift_detail (shift_id,day_id,bell_id) values (1,4,5);
-- Пятница
insert into shift_detail (shift_id,day_id,bell_id) values (1,5,1);
insert into shift_detail (shift_id,day_id,bell_id) values (1,5,2);
insert into shift_detail (shift_id,day_id,bell_id) values (1,5,3);
insert into shift_detail (shift_id,day_id,bell_id) values (1,5,4);
insert into shift_detail (shift_id,day_id,bell_id) values (1,5,5);
------------------------------------------------------------------
-- Понедельник 
insert into shift_detail (shift_id,day_id,bell_id) values (2,1,1);
insert into shift_detail (shift_id,day_id,bell_id) values (2,1,2);
insert into shift_detail (shift_id,day_id,bell_id) values (2,1,3);
insert into shift_detail (shift_id,day_id,bell_id) values (2,1,4);
insert into shift_detail (shift_id,day_id,bell_id) values (2,1,5);
insert into shift_detail (shift_id,day_id,bell_id) values (2,1,6);
-- Вторник 
insert into shift_detail (shift_id,day_id,bell_id) values (2,2,1);
insert into shift_detail (shift_id,day_id,bell_id) values (2,2,2);
insert into shift_detail (shift_id,day_id,bell_id) values (2,2,3);
insert into shift_detail (shift_id,day_id,bell_id) values (2,2,4);
insert into shift_detail (shift_id,day_id,bell_id) values (2,2,5);
insert into shift_detail (shift_id,day_id,bell_id) values (2,2,6);
-- Среда
insert into shift_detail (shift_id,day_id,bell_id) values (2,3,1);
insert into shift_detail (shift_id,day_id,bell_id) values (2,3,2);
insert into shift_detail (shift_id,day_id,bell_id) values (2,3,3);
insert into shift_detail (shift_id,day_id,bell_id) values (2,3,4);
insert into shift_detail (shift_id,day_id,bell_id) values (2,3,5);
insert into shift_detail (shift_id,day_id,bell_id) values (2,3,6);
-- Четверг
insert into shift_detail (shift_id,day_id,bell_id) values (2,4,1);
insert into shift_detail (shift_id,day_id,bell_id) values (2,4,2);
insert into shift_detail (shift_id,day_id,bell_id) values (2,4,3);
insert into shift_detail (shift_id,day_id,bell_id) values (2,4,4);
insert into shift_detail (shift_id,day_id,bell_id) values (2,4,5);
insert into shift_detail (shift_id,day_id,bell_id) values (2,4,6);
-- Пятница
insert into shift_detail (shift_id,day_id,bell_id) values (2,5,1);
insert into shift_detail (shift_id,day_id,bell_id) values (2,5,2);
insert into shift_detail (shift_id,day_id,bell_id) values (2,5,3);
insert into shift_detail (shift_id,day_id,bell_id) values (2,5,4);
insert into shift_detail (shift_id,day_id,bell_id) values (2,5,5);
insert into shift_detail (shift_id,day_id,bell_id) values (2,5,6);
-- Суббота
insert into shift_detail (shift_id,day_id,bell_id) values (2,6,1);
insert into shift_detail (shift_id,day_id,bell_id) values (2,6,2);
insert into shift_detail (shift_id,day_id,bell_id) values (2,6,3);
insert into shift_detail (shift_id,day_id,bell_id) values (2,6,4);
insert into shift_detail (shift_id,day_id,bell_id) values (2,6,5);
insert into shift_detail (shift_id,day_id,bell_id) values (2,6,6);
------------------------------------------------------------------
-- Понедельник 
insert into shift_detail (shift_id,day_id,bell_id) values (3,1,7);
insert into shift_detail (shift_id,day_id,bell_id) values (3,1,8);
insert into shift_detail (shift_id,day_id,bell_id) values (3,1,9);
insert into shift_detail (shift_id,day_id,bell_id) values (3,1,10);
insert into shift_detail (shift_id,day_id,bell_id) values (3,1,11);
insert into shift_detail (shift_id,day_id,bell_id) values (3,1,12);
-- Вторник 
insert into shift_detail (shift_id,day_id,bell_id) values (3,2,7);
insert into shift_detail (shift_id,day_id,bell_id) values (3,2,8);
insert into shift_detail (shift_id,day_id,bell_id) values (3,2,9);
insert into shift_detail (shift_id,day_id,bell_id) values (3,2,10);
insert into shift_detail (shift_id,day_id,bell_id) values (3,2,11);
insert into shift_detail (shift_id,day_id,bell_id) values (3,2,12);
-- Среда
insert into shift_detail (shift_id,day_id,bell_id) values (3,3,7);
insert into shift_detail (shift_id,day_id,bell_id) values (3,3,8);
insert into shift_detail (shift_id,day_id,bell_id) values (3,3,9);
insert into shift_detail (shift_id,day_id,bell_id) values (3,3,10);
insert into shift_detail (shift_id,day_id,bell_id) values (3,3,11);
insert into shift_detail (shift_id,day_id,bell_id) values (3,3,12);
-- Четверг
insert into shift_detail (shift_id,day_id,bell_id) values (3,4,7);
insert into shift_detail (shift_id,day_id,bell_id) values (3,4,8);
insert into shift_detail (shift_id,day_id,bell_id) values (3,4,9);
insert into shift_detail (shift_id,day_id,bell_id) values (3,4,10);
insert into shift_detail (shift_id,day_id,bell_id) values (3,4,11);
insert into shift_detail (shift_id,day_id,bell_id) values (3,4,12);
-- Пятница
insert into shift_detail (shift_id,day_id,bell_id) values (3,5,7);
insert into shift_detail (shift_id,day_id,bell_id) values (3,5,8);
insert into shift_detail (shift_id,day_id,bell_id) values (3,5,9);
insert into shift_detail (shift_id,day_id,bell_id) values (3,5,10);
insert into shift_detail (shift_id,day_id,bell_id) values (3,5,11);
insert into shift_detail (shift_id,day_id,bell_id) values (3,5,12);
-- Суббота
insert into shift_detail (shift_id,day_id,bell_id) values (3,6,7);
insert into shift_detail (shift_id,day_id,bell_id) values (3,6,8);
insert into shift_detail (shift_id,day_id,bell_id) values (3,6,9);
insert into shift_detail (shift_id,day_id,bell_id) values (3,6,10);
insert into shift_detail (shift_id,day_id,bell_id) values (3,6,11);
insert into shift_detail (shift_id,day_id,bell_id) values (3,6,12);

-- Графики преподавателей

insert into shift (id,shift_type_id,shift_name) values (4,2,'ПН ВТ СР ЧТ ПТ СБ');
insert into shift (id,shift_type_id,shift_name) values (5,2,'ПН ВТ СР ЧТ ПТ');
insert into shift (id,shift_type_id,shift_name) values (6,2,'ПН ВТ СР ЧТ    СБ');
insert into shift (id,shift_type_id,shift_name) values (7,2,'ПН ВТ СР    ПТ СБ');
insert into shift (id,shift_type_id,shift_name) values (8,2,'ПН ВТ    ЧТ ПТ СБ');
insert into shift (id,shift_type_id,shift_name) values (9,2,'ПН    СР ЧТ ПТ СБ');
insert into shift (id,shift_type_id,shift_name) values (10,2,'   ВТ СР ЧТ ПТ СБ');
update shift_type set default_shift_id=4 where id=2;


-- Графики помещений
insert into shift (id,shift_type_id,shift_name) values (11,3,'Кабинет');
update shift_type set default_shift_id=11 where id=3;


--  Зполнение графиков по умолчанию
insert into shift_detail (shift_id,day_id,bell_id)
select default_shift_id,day_no,bell_id 
from day_list,bell_list,shift_type where shift_type.id in (2,3)  ;


select * from shift;
select * from shift_detail;
