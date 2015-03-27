insert into building (id,caption) values (1,'Главное здание');

insert into group_type (id,group_type_caption) values (0,'весь класс');
insert into group_type (id,group_type_caption) values (1,'м.-д.');
insert into group_type (id,group_type_caption) values (2,'группы');


insert into day_list(day_no,day_caption) values (1,'Понедельник');
insert into day_list(day_no,day_caption) values (2,'Вторник');
insert into day_list(day_no,day_caption) values (3,'Среда');
insert into day_list(day_no,day_caption) values (4,'Четверг');
insert into day_list(day_no,day_caption) values (5,'Пятница');
insert into day_list(day_no,day_caption) values (6,'Суббота');
insert into day_list(day_no,day_caption) values (7,'Воскресение');

insert into bell_list(bell_id,time_start,time_end) values (1,'10:00','10:45');
insert into bell_list(bell_id,time_start,time_end) values (2,'11:00','11:45');
insert into bell_list(bell_id,time_start,time_end) values (3,'12:00','12:45');
insert into bell_list(bell_id,time_start,time_end) values (4,'13:00','13:45');
insert into bell_list(bell_id,time_start,time_end) values (5,'14:00','14:45');
insert into bell_list(bell_id,time_start,time_end) values (6,'15:00','15:45');
insert into bell_list(bell_id,time_start,time_end) values (7,'16:00','16:45');
insert into bell_list(bell_id,time_start,time_end) values (8,'17:00','17:45');
insert into bell_list(bell_id,time_start,time_end) values (9,'18:00','18:45');

insert into profile_type(id,caption) values (1,'Профиль преподавателя');
insert into profile_type(id,caption) values (2,'Профиль помещения');

--    Графики преподавателей

insert into profile(id,profile_type_id,name) values (1,1,'Преподаватель 1');
insert into profile(id,profile_type_id,name) values (2,1,'Преподаватель 2');
insert into profile(id,profile_type_id,name) values (3,1,'Преподаватель 3');
insert into profile(id,profile_type_id,name) values (4,1,'Преподаватель 4');

--    Графики помещений

insert into profile(id,profile_type_id,name) values (5,2,'Кабинет 1');
insert into profile(id,profile_type_id,name) values (6,2,'Кабинет 2');
insert into profile(id,profile_type_id,name) values (7,2,'Кабинет 3');
insert into profile(id,profile_type_id,name) values (8,2,'Кабинет 4');


insert into shift_type(id,caption) values (1,'График класса');
insert into shift_type(id,caption) values (2,'График преподователя');
insert into shift_type(id,caption) values (3,'График помещения');

insert into shift(id,shift_type_id,name) values(1,1,'Первая смена');  
insert into shift(id,shift_type_id,name) values(2,1,'Вторая смена');  
insert into shift(id,shift_type_id,name) values(3,2,'График преподавателя 1');  
insert into shift(id,shift_type_id,name) values(4,3,'График преподавателя 2');  
insert into shift(id,shift_type_id,name) values(5,3,'Обычный кабинет');  

--
--                Предметы
--                 

insert into subject(id,subject_name,default_hour_per_day,default_group_type_id,default_hour_per_week) 
    values (1,'Русский яз',1,0,4);
insert into subject(id,subject_name,default_hour_per_day,default_group_type_id,default_hour_per_week)
    values (2,'Литература',1,0,2);
insert into subject(id,subject_name,default_hour_per_day,default_group_type_id,default_hour_per_week)
    values (3,'Алгебра',1,0,2);
insert into subject(id,subject_name,default_hour_per_day,default_group_type_id,default_hour_per_week)
    values (4,'Физика',1,0,2);
insert into subject(id,subject_name,default_hour_per_day,default_group_type_id,default_hour_per_week)
    values (5,'Химия',1,0,2);
insert into subject(id,subject_name,default_hour_per_day,default_group_type_id,default_hour_per_week)
    values (6,'Иностранный яз.',1,2,2);
insert into subject(id,subject_name,default_hour_per_day,default_group_type_id,default_hour_per_week)
     values (7,'Физ.культура',1,1,2);
        
insert into teacher (id,last_name,first_name,patronymic,profile_id,shift_id) values (1,'Иванова','Ирина','',1,1);
insert into teacher (id,last_name,first_name,patronymic,profile_id) values (2,'Петрова','Людмила','',2);
insert into teacher (id,last_name,first_name,patronymic,profile_id,shift_id) values (3,'Сидорова','Лариса','',3,1);
insert into teacher (id,last_name,first_name,patronymic) values (4,'Романова','Татьяна','');
insert into teacher (id,last_name,first_name,patronymic,profile_id) values (5,'Галкина','Клавдия','',4);

insert into room(id,name,shift_id,profile_id) values (1,'каб 31',5,5);


insert into skill(id,caption) values(1,'8-класс');
insert into skill(id,caption) values(2,'9-класс');
insert into skill(id,caption) values(3,'10-класс');

insert into depart(id,label,skill_id,shift_id) values (1,'1-a',1,1);
insert into depart(id,label,skill_id,shift_id) values (2,'1-б',1,1);
insert into depart(id,label,skill_id,shift_id) values (3,'1-в',1,1);
insert into depart(id,label,skill_id,shift_id) values (4,'2-a',1,1);
insert into depart(id,label,skill_id,shift_id) values (5,'2-б',1,1);
insert into depart(id,label,skill_id,shift_id) values (6,'2-в',1,1);

insert into curriculum (id,caption) values (1,'Учебный план 1');
insert into curriculum (id,caption) values (2,'Учебный план 2');
insert into curriculum (id,caption) values (3,'Учебный план 3');

insert into curriculum_detail(curriculum_id,subject_id,hour_per_day,hour_per_week)
  values(1,1,2,2);
    
insert into curriculum_detail(curriculum_id,subject_id,hour_per_day,hour_per_week)
  values(1,2,2,2);

insert into curriculum_detail(curriculum_id,subject_id,hour_per_day,hour_per_week)
  values(1,3,2,2);




