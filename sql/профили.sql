-- провили преподавателей

delete from profile;

insert into profile(id,profile_type_id,profile_name) values (1,1,'Русский яз и литература');
insert into profile_item (profile_id,subject_id) values (1,1);
insert into profile_item (profile_id,subject_id) values (1,2);

insert into profile(id,profile_type_id,profile_name) values (2,1,'Алгебра и геометрия');
insert into profile_item (profile_id,subject_id) values (2,3);
insert into profile_item (profile_id,subject_id) values (2,4);

insert into profile(id,profile_type_id,profile_name) values (3,1,'Физика');
insert into profile_item (profile_id,subject_id) values (3,5);

insert into profile(id,profile_type_id,profile_name) values (4,1,'Химия');
insert into profile_item (profile_id,subject_id) values (4,6);

insert into profile(id,profile_type_id,profile_name) values (5,1,'Иностранный яз');
insert into profile_item (profile_id,subject_id) values (5,7);

insert into profile(id,profile_type_id,profile_name) values (6,1,'Физкультура');
insert into profile_item (profile_id,subject_id) values (6,8);

insert into profile(id,profile_type_id,profile_name) values (7,1,'История и Обществознание');
insert into profile_item (profile_id,subject_id) values (7,16);
insert into profile_item (profile_id,subject_id) values (7,17);

insert into profile(id,profile_type_id,profile_name) values (8,1,'Искуство');
insert into profile_item (profile_id,subject_id) values (8,12);
insert into profile_item (profile_id,subject_id) values (8,13);

insert into profile(id,profile_type_id,profile_name) values (9,1,'География и Биология');
insert into profile_item (profile_id,subject_id) values (9,18);
insert into profile_item (profile_id,subject_id) values (9,20);


insert into profile(id,profile_type_id,profile_name) values (10,1,'Трудовое обучение');
insert into profile_item (profile_id,subject_id) values (10,14);

insert into profile(id,profile_type_id,profile_name) values (11,1,'Информатика');
insert into profile_item (profile_id,subject_id) values (11,9);

insert into profile(id,profile_type_id,profile_name) values (12,1,'Преподаватель мл.классов');
insert into profile_item (profile_id,subject_id) values (12,22);
insert into profile_item (profile_id,subject_id) values (12,19);
insert into profile_item (profile_id,subject_id) values (12,10);
insert into profile_item (profile_id,subject_id) values (12,4);
insert into profile_item (profile_id,subject_id) values (12,11);

insert into profile_item (profile_id,subject_id) values (12,1);
insert into profile_item (profile_id,subject_id) values (12,14);

insert into profile(id,profile_type_id,profile_name) values (13,1,'ОБЖ');
insert into profile_item (profile_id,subject_id) values (13,21);

-- Помещения
insert into profile (id,profile_type_id,profile_name) values (14,2,'Кабинет');
insert into profile_item(profile_id,subject_id) values (14,1);
insert into profile_item(profile_id,subject_id) values (14,2);
insert into profile_item(profile_id,subject_id) values (14,3);
insert into profile_item(profile_id,subject_id) values (14,4);
insert into profile_item(profile_id,subject_id) values (14,5);
insert into profile_item(profile_id,subject_id) values (14,6);
insert into profile_item(profile_id,subject_id) values (14,7);
insert into profile_item(profile_id,subject_id) values (14,9);
insert into profile_item(profile_id,subject_id) values (14,10);
insert into profile_item(profile_id,subject_id) values (14,11);
insert into profile_item(profile_id,subject_id) values (14,13);
insert into profile_item(profile_id,subject_id) values (14,14);
insert into profile_item(profile_id,subject_id) values (14,15);
insert into profile_item(profile_id,subject_id) values (14,16);
insert into profile_item(profile_id,subject_id) values (14,17);
insert into profile_item(profile_id,subject_id) values (14,18);
insert into profile_item(profile_id,subject_id) values (14,19);
insert into profile_item(profile_id,subject_id) values (14,20);
insert into profile_item(profile_id,subject_id) values (14,21);
insert into profile_item(profile_id,subject_id) values (14,22);

insert into profile (id,profile_type_id,profile_name) values (15,2,'Спортзал');
insert into profile_item(profile_id,subject_id) values (15,8);
insert into profile (id,profile_type_id,profile_name) values (16,2,'Акт.зал');
insert into profile_item(profile_id,subject_id) values (16,8);
insert into profile_item(profile_id,subject_id) values (16,12);




select * from profile;