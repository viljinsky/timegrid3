insert into building (id,building_name) values (1,'Главное здание');

insert into day_list(day_no,day_short_name,day_caption) values (1,'Пн','Понедельник');
insert into day_list(day_no,day_short_name,day_caption) values (2,'Вт','Вторник');
insert into day_list(day_no,day_short_name,day_caption) values (3,'Ср','Среда');
insert into day_list(day_no,day_short_name,day_caption) values (4,'Чт','Четверг');
insert into day_list(day_no,day_short_name,day_caption) values (5,'Пт','Пятница');
insert into day_list(day_no,day_short_name,day_caption) values (6,'Сб','Суббота');
insert into day_list(day_no,day_short_name,day_caption) values (7,'Вс','Воскресение');

insert into bell_list(bell_id,time_start,time_end) values (1,'10:00','10:45');
insert into bell_list(bell_id,time_start,time_end) values (2,'11:00','11:45');
insert into bell_list(bell_id,time_start,time_end) values (3,'12:00','12:45');
insert into bell_list(bell_id,time_start,time_end) values (4,'13:00','13:45');
insert into bell_list(bell_id,time_start,time_end) values (5,'14:00','14:45');
insert into bell_list(bell_id,time_start,time_end) values (6,'15:00','15:45');
insert into bell_list(bell_id,time_start,time_end) values (7,'16:00','16:45');
insert into bell_list(bell_id,time_start,time_end) values (8,'17:00','17:45');
insert into bell_list(bell_id,time_start,time_end) values (9,'18:00','18:45');
insert into bell_list(bell_id,time_start,time_end) values (10,'19:00','19:45');


--    Профили преподавателей

insert into profile(id,profile_type_id,profile_name) values (1,1,'Руск.яз и литература');
insert into profile(id,profile_type_id,profile_name) values (2,1,'Алгебра и геометрия');
insert into profile(id,profile_type_id,profile_name) values (3,1,'Физика');
insert into profile(id,profile_type_id,profile_name) values (4,1,'Химия');
insert into profile(id,profile_type_id,profile_name) values (5,1,'Иностранный яз');
insert into profile(id,profile_type_id,profile_name) values (6,1,'Физкультура');
insert into profile(id,profile_type_id,profile_name) values (7,1,'Информатика');
insert into profile(id,profile_type_id,profile_name) values (8,1,'Преподаватель мл.классов');

insert into profile(id,profile_type_id,profile_name) values (9,1,'ИЗО');
insert into profile(id,profile_type_id,profile_name) values (10,1,'Музыка');




--    Профили помещений

insert into profile(id,profile_type_id,profile_name) values (11,2,'Общ назн.');
insert into profile(id,profile_type_id,profile_name) values (12,2,'Спортзал');
insert into profile(id,profile_type_id,profile_name) values (13,2,'Ком.кабинет');
insert into profile(id,profile_type_id,profile_name) values (14,2,'Лингофонный каб.');

insert into shift(id,shift_type_id,shift_name) values(1,1,'Первая смена');  
insert into shift(id,shift_type_id,shift_name) values(2,1,'Вторая смена');  
insert into shift(id,shift_type_id,shift_name) values(3,2,'График преподавателя 1');  
insert into shift(id,shift_type_id,shift_name) values(4,2,'График преподавателя 2');  
insert into shift(id,shift_type_id,shift_name) values(5,3,'Обычный кабинет');  


insert into room(id,building_id,room_name,shift_id,profile_id,capacity) values (1,1,'каб 31',5,11,30);
insert into room(id,building_id,room_name,shift_id,profile_id,capacity) values (2,1,'каб 32',5,11,30);
insert into room(id,building_id,room_name,shift_id,profile_id,capacity) values (3,1,'каб 33',5,11,30);
insert into room(id,building_id,room_name,shift_id,profile_id,capacity) values (4,1,'каб 34',5,11,30);
insert into room(id,building_id,room_name,shift_id,profile_id,capacity) values (5,1,'каб 35',5,11,30);
insert into room(id,building_id,room_name,shift_id,profile_id,capacity) values (6,1,'каб 36',5,11,30);
insert into room(id,building_id,room_name,shift_id,profile_id,capacity) values (7,1,'каб 37',5,11,30);
insert into room(id,building_id,room_name,shift_id,profile_id,capacity) values (8,1,'каб 38',5,11,30);
insert into room(id,building_id,room_name,shift_id,profile_id)          values (9,1,'каб 39',5,11);
insert into room(id,building_id,room_name,shift_id,profile_id)          values (10,1,'спорт.зал',5,12);



insert into subject_domain(id,domain_caption) values (1,'Математические');
insert into subject_domain(id,domain_caption) values (2,'Естественно-научные');
insert into subject_domain(id,domain_caption) values (3,'Гуманитарные');
insert into subject_domain(id,domain_caption) values (4,'Филолагические');
insert into subject_domain(id,domain_caption) values (5,'Трудовое обучение');
insert into subject_domain(id,domain_caption) values (6,'Физкультурв');
insert into subject_domain(id,domain_caption) values (7,'Искуство');


--
--                Предметы
--                 



insert into subject(id,subject_name,color) 
    values (1,'Русский яз',  '255 200 170');
insert into subject(id,subject_name,color)
    values (2,'Литература',  '255 170 200');
insert into subject(id,subject_name,color)
    values (3,'Алгебра'   ,   '200 255 170');
insert into subject(id,subject_name,color)
    values (4,'Геометрия' ,     '170 255 200');
insert into subject(id,subject_name,color)
    values (5,'Физика'    ,     '200 170 255');
insert into subject(id,subject_name,color)
    values (6,'Химия'     ,     '170 200 255');
insert into subject(id,subject_name,default_group_type_id,color)
    values (7,'Иностранный яз.',2,'200 200 255');
insert into subject(id,subject_name,default_group_type_id,color)
     values (8,'Физ.культура',  1,'200 255 200');
insert into subject(id,subject_name,default_group_type_id,color)
     values (9,'Информатика',   2,'255 200 200');



insert into subject(id,subject_name,color)
     values (22,'Чтение',  '255 200 200');
insert into subject(id,subject_name,color)
     values (10,'Математика',  '255 200 200');
insert into subject(id,subject_name,color)
     values (11,'Окружающий мир',   '255 200 200');
insert into subject(id,subject_name,color)
     values (12,'Музыка',   '255 200 200');
insert into subject(id,subject_name,color)
     values (13,'ИЗО',   '255 200 200');
insert into subject(id,subject_name,color)
     values (14,'Технология', '255 200 200');
insert into subject(id,subject_name,color)
     values (15,'Этика',   '255 200 200');


insert into subject(id,subject_name,color)
     values (16,'История',   '255 200 200');
insert into subject(id,subject_name,color)
     values (17,'Обществознание',   '255 200 200');
insert into subject(id,subject_name,color)
     values (18,'География',   '255 200 200');
insert into subject(id,subject_name,color)
     values (19,'Природоведение',   '255 200 200');
insert into subject(id,subject_name,color)
     values (20,'Биология',   '255 200 200');
insert into subject(id,subject_name,color)
     values (21,'ОБЖ',   '255 200 200');





insert into skill(id,caption) values(1,'1-класс');
insert into skill(id,caption) values(2,'2-класс');
insert into skill(id,caption) values(3,'3-класс');
insert into skill(id,caption) values(4,'4-класс');
insert into skill(id,caption) values(5,'5-класс');
insert into skill(id,caption) values(6,'6-класс');
insert into skill(id,caption) values(7,'7-класс');
insert into skill(id,caption) values(8,'8-класс');
insert into skill(id,caption) values(9,'9-класс');
insert into skill(id,caption) values(10,'10-класс');
insert into skill(id,caption) values(11,'11-класс');


insert into curriculum (id,caption) values (1,'1-4 классы');
insert into curriculum (id,caption) values (2,'5-9 классы');
insert into curriculum (id,caption) values (3,'10-11 классы');


--  *************************  ЗАПОЛНЕНИЕ ГРАФИКОВ  ****************************
delete from shift_detail;
-- первая смена
insert into shift_detail (day_id,bell_id,shift_id)
select day_no,bell_id,id
from day_list,bell_list,shift
where shift.id=1 and bell_id between 1 and 5 and not day_no=7;

-- вторая смена

insert into shift_detail (day_id,bell_id,shift_id)
select day_no,bell_id,id
from day_list,bell_list,shift
where shift.id=2 and bell_id between 6 and 10 and not day_no=7;

-- преподаватели и помещения
insert into shift_detail (day_id,bell_id,shift_id)
select day_no,bell_id,id
from day_list,bell_list,shift
where shift.id in (3,4) ;

update teacher set shift_id=3;

-- *************************** ЗАПОЛНЕНИЕ ПРОФИЛЕЙ *****************************
--          Профили преподавателей
-- русский и литература
insert into profile_item (profile_id,subject_id) values(1,1);
insert into profile_item (profile_id,subject_id) values(1,2);
-- алгебра и геомерия
insert into profile_item (profile_id,subject_id) values(2,3);
insert into profile_item (profile_id,subject_id) values(2,4);
-- физика
insert into profile_item (profile_id,subject_id) values(3,5);
-- химия
insert into profile_item (profile_id,subject_id) values(4,6);
-- иностранный
insert into profile_item (profile_id,subject_id) values(5,7);
-- физкультура
insert into profile_item (profile_id,subject_id) values(6,8);
-- информатика
insert into profile_item (profile_id,subject_id) values(7,9);

-- преподаватель мл.классов
insert into profile_item (profile_id,subject_id) select 8,id from subject where id in(1,22,10,11,14,15);

-- изо
insert into profile_item (profile_id,subject_id) values(10,12);

-- музыка
insert into profile_item (profile_id,subject_id) values(9,13);


-- insert into profile_item (profile_id,subject_id) select a.id,b.id
-- from profile a ,subject b where a.id in (8,9,10,11);

-- графики помещений

insert into shift_detail (shift_id,day_id,bell_id)
select 5 ,a.day_no,b.bell_id from day_list a,bell_list b;

-- графики преподавателей

insert into shift (id,shift_type_id,shift_name)
values(10,2,'Вт,Ср,Чт,Пт,Сб') ;

insert into shift_detail (shift_id,day_id,bell_id)
select 10 ,a.day_no,b.bell_id from day_list a,bell_list b
where  not a.day_no in (1,7);

insert into shift (id,shift_type_id,shift_name)
values(11,2,'Пт,Ср,Чт,Пт,Сб') ;

insert into shift_detail (shift_id,day_id,bell_id)
select 11 ,a.day_no,b.bell_id from day_list a,bell_list b
where  not a.day_no in (2,7);

--

insert into shift (id,shift_type_id,shift_name)
values(12,2,'Пт,Вт,Чт,Пт,Сб') ;

insert into shift_detail (shift_id,day_id,bell_id)
select 12 ,a.day_no,b.bell_id from day_list a,bell_list b
where  not a.day_no in (3,7);

--
insert into shift (id,shift_type_id,shift_name)
values(14,2,'Пт,Вт,Ср,Пт,Сб') ;

insert into shift_detail (shift_id,day_id,bell_id)
select 14 ,a.day_no,b.bell_id from day_list a,bell_list b
where  not a.day_no in (4,7);
--
insert into shift (id,shift_type_id,shift_name)
values(15,2,'Пт,Вт,Ср,Чт,Сб') ;

insert into shift_detail (shift_id,day_id,bell_id)
select 15 ,a.day_no,b.bell_id from day_list a,bell_list b
where  not a.day_no in (5,7);

--
insert into shift (id,shift_type_id,shift_name)
values(16,2,'Пт,Вт,Ср,Чт,Пт') ;

insert into shift_detail (shift_id,day_id,bell_id)
select 16 ,a.day_no,b.bell_id from day_list a,bell_list b
where  not a.day_no in (6,7);


-- Преподаватели

-- младшие классы
insert into teacher (id,last_name,first_name,patronymic,profile_id,shift_id) 
    values (20,'Ласточкина','Ирина','Ивановна',8,10);
insert into teacher (id,last_name,first_name,patronymic,profile_id,shift_id) 
    values (21,'Быкова','Ирина','Ивановна',8,10);
insert into teacher (id,last_name,first_name,patronymic,profile_id,shift_id) 
    values (22,'Снигерёва','Ирина','Ивановна',8,10);
insert into teacher (id,last_name,first_name,patronymic,profile_id,shift_id) 
    values (23,'Пеликанова','Ирина','Ивановна',8,10);
insert into teacher (id,last_name,first_name,patronymic,profile_id,shift_id) 
    values (24,'Зябликова','Ирина','Ивановна',8,10);

insert into teacher (id,last_name,first_name,patronymic,profile_id,shift_id) 
    values (25,'Мухина','Ирина','Ивановна',9,10);
insert into teacher (id,last_name,first_name,patronymic,profile_id,shift_id) 
    values (26,'Шмелёва','Ирина','Ивановна',10,10);



-- русский и лит.        
insert into teacher (id,last_name,first_name,patronymic,profile_id,shift_id) 
    values (1,'Ежёва','Ирина','Ивановна',1,10);
insert into teacher (id,last_name,first_name,patronymic,profile_id,shift_id) 
    values (2,'Белкина','Людмила','Олеговна',1,11);

-- алг и геометрия
insert into teacher (id,last_name,first_name,patronymic,profile_id,shift_id)
    values (3,'Сорокина','Лариса','Петровна',2,12);
insert into teacher (id,last_name,first_name,patronymic,profile_id,shift_id)
    values (4,'Орлова','Татьяна','Игоревна',2,14);

-- физика
insert into teacher (id,last_name,first_name,patronymic,profile_id,shift_id)
    values (5,'Медведева','Клавдия','Николоаевна',3,15);

-- химия
insert into teacher (id,last_name,first_name,patronymic,profile_id,shift_id)
    values (6,'Волкова','Марфа','Сидоровна',4,16);

-- иностранный
insert into teacher (id,last_name,first_name,patronymic,profile_id,shift_id)
    values (7,'Птичкина','Раиса','Григорьевна',5,12);
insert into teacher (id,last_name,first_name,patronymic,profile_id,shift_id)
    values (8,'Рыбкина','Софья','Петровна',5,12);

-- физкультура
insert into teacher (id,last_name,first_name,patronymic,profile_id,shift_id)
    values (9,'Ужёва','Тамра','Сидоровна',6,14);
insert into teacher (id,last_name,first_name,patronymic,profile_id,shift_id)
    values (10,'Синицина','Ирина','Олеговна',6,14);

-- информатика
insert into teacher (id,last_name,first_name,patronymic,profile_id,shift_id)
    values (11,'Карпова','Вероника','Маврикиевна',7,15);



