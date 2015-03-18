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

--
--                Предметы
--                 

insert into subject(id,subject_name) values (1,'Русский яз');
insert into subject(id,subject_name) values (2,'Литература');
insert into subject(id,subject_name) values (3,'Алгебра');
insert into subject(id,subject_name) values (4,'Физика');
insert into subject(id,subject_name) values (5,'Химия');
insert into subject(id,subject_name) values (6,'Иностранный яз.');
insert into subject(id,subject_name) values (7,'Физ.культура');
        
insert into teacher (id,last_name,first_name,patronymic,profile_id) values (1,'Иванова','Ирина','',1);
insert into teacher (id,last_name,first_name,patronymic,profile_id) values (2,'Петрова','Людмила','',2);
insert into teacher (id,last_name,first_name,patronymic,profile_id) values (3,'Сидорова','Лариса','',3);
insert into teacher (id,last_name,first_name,patronymic) values (4,'Романова','Татьяна','');
insert into teacher (id,last_name,first_name,patronymic,profile_id) values (5,'Галкина','Клавдия','',4);


insert into skill(id,caption) values(1,'8-класс');
insert into skill(id,caption) values(2,'9-класс');
insert into skill(id,caption) values(3,'10-класс');

insert into depart(id,skill_id,shift_id) values (1,1,1);
insert into depart(id,skill_id,shift_id) values (2,1,1);
insert into depart(id,skill_id,shift_id) values (3,1,1);
insert into depart(id,skill_id,shift_id) values (4,1,1);
insert into depart(id,skill_id,shift_id) values (5,1,1);
insert into depart(id,skill_id,shift_id) values (6,1,1);

insert into curriculum (id,caption) values (1,'Учебный план 1');
insert into curriculum (id,caption) values (2,'Учебный план 2');
insert into curriculum (id,caption) values (3,'Учебный план 3');

insert into curriculum_detail(curriculum_id,subject_id,hour_per_day,hour_per_week)
  values(1,1,2,2);
    
insert into curriculum_detail(curriculum_id,subject_id,hour_per_day,hour_per_week)
  values(1,2,2,2);

insert into curriculum_detail(curriculum_id,subject_id,hour_per_day,hour_per_week)
  values(1,3,2,2);




