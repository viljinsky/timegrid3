
--  table subject_domain 

delete from subject_domain;
insert into subject_domain (id,domain_caption)
	 values (1,'Математические');
insert into subject_domain (id,domain_caption)
	 values (2,'Естественно-научные');
insert into subject_domain (id,domain_caption)
	 values (3,'Гуманитарные');
insert into subject_domain (id,domain_caption)
	 values (4,'Филолагические');
insert into subject_domain (id,domain_caption)
	 values (5,'Трудовое обучение');
insert into subject_domain (id,domain_caption)
	 values (6,'Физкультурв');
insert into subject_domain (id,domain_caption)
	 values (7,'Искуство');

--  table subject 

delete from subject;
insert into subject (id,default_hour_per_week,subject_name,subject_domain_id,color,default_group_type_id,default_hour_per_day,sort_order)
	 values (1,2,'Русский яз',4,'210 210 210',0,1,0);
insert into subject (id,default_hour_per_week,subject_name,subject_domain_id,color,default_group_type_id,default_hour_per_day,sort_order)
	 values (2,2,'Литература',4,'210 210 220',0,1,11);
insert into subject (id,default_hour_per_week,subject_name,subject_domain_id,color,default_group_type_id,default_hour_per_day,sort_order)
	 values (3,2,'Алгебра',2,'210 210 230',0,1,12);
insert into subject (id,default_hour_per_week,subject_name,subject_domain_id,color,default_group_type_id,default_hour_per_day,sort_order)
	 values (4,2,'Геометрия',2,'210 210 255',0,1,13);
insert into subject (id,default_hour_per_week,subject_name,subject_domain_id,color,default_group_type_id,default_hour_per_day,sort_order)
	 values (5,2,'Физика',2,'210 220 210',0,1,14);
insert into subject (id,default_hour_per_week,subject_name,subject_domain_id,color,default_group_type_id,default_hour_per_day,sort_order)
	 values (6,2,'Химия',2,'210 220 220',0,1,15);
insert into subject (id,default_hour_per_week,subject_name,subject_domain_id,color,default_group_type_id,default_hour_per_day,sort_order)
	 values (7,2,'Иностранный яз.',4,'210 220 230',2,1,3);
insert into subject (id,default_hour_per_week,subject_name,subject_domain_id,color,default_group_type_id,default_hour_per_day,sort_order)
	 values (8,2,'Физ.культура',6,'210 220 255',1,1,9);
insert into subject (id,default_hour_per_week,subject_name,subject_domain_id,color,default_group_type_id,default_hour_per_day,sort_order)
	 values (9,2,'Информатика',1,'210 230 210',2,1,16);
insert into subject (id,default_hour_per_week,subject_name,subject_domain_id,color,default_group_type_id,default_hour_per_day,sort_order)
	 values (10,2,'Математика',1,'210 230 220',0,1,4);
insert into subject (id,default_hour_per_week,subject_name,subject_domain_id,color,default_group_type_id,default_hour_per_day,sort_order)
	 values (11,2,'Окружающий мир',2,'210 230 230',0,1,5);
insert into subject (id,default_hour_per_week,subject_name,subject_domain_id,color,default_group_type_id,default_hour_per_day,sort_order)
	 values (12,2,'Музыка',7,'210 230 255',0,1,7);
insert into subject (id,default_hour_per_week,subject_name,subject_domain_id,color,default_group_type_id,default_hour_per_day,sort_order)
	 values (13,2,'ИЗО',7,'210 255 210',0,1,8);
insert into subject (id,default_hour_per_week,subject_name,subject_domain_id,color,default_group_type_id,default_hour_per_day,sort_order)
	 values (14,2,'Технология',5,'210 255 220',0,1,10);
insert into subject (id,default_hour_per_week,subject_name,subject_domain_id,color,default_group_type_id,default_hour_per_day,sort_order)
	 values (15,2,'Этика',3,'210 255 230',0,1,6);
insert into subject (id,default_hour_per_week,subject_name,subject_domain_id,color,default_group_type_id,default_hour_per_day,sort_order)
	 values (16,2,'История',3,'210 255 255',0,1,17);
insert into subject (id,default_hour_per_week,subject_name,subject_domain_id,color,default_group_type_id,default_hour_per_day,sort_order)
	 values (17,2,'Обществознание',3,'220 210 210',0,1,18);
insert into subject (id,default_hour_per_week,subject_name,subject_domain_id,color,default_group_type_id,default_hour_per_day,sort_order)
	 values (18,2,'География',2,'220 210 220',0,1,19);
insert into subject (id,default_hour_per_week,subject_name,subject_domain_id,color,default_group_type_id,default_hour_per_day,sort_order)
	 values (19,2,'Природоведение',2,'220 210 230',0,1,20);
insert into subject (id,default_hour_per_week,subject_name,subject_domain_id,color,default_group_type_id,default_hour_per_day,sort_order)
	 values (20,2,'Биология',2,'220 210 255',0,1,21);
insert into subject (id,default_hour_per_week,subject_name,subject_domain_id,color,default_group_type_id,default_hour_per_day,sort_order)
	 values (21,2,'ОБЖ',6,'220 220 210',0,1,22);
insert into subject (id,default_hour_per_week,subject_name,subject_domain_id,color,default_group_type_id,default_hour_per_day,sort_order)
	 values (22,2,'Чтение',4,'255 200 200',0,1,0);

--  table skill 

delete from skill;
insert into skill (id,caption,sort_order)
	 values (1,'1-класс',0);
insert into skill (id,caption,sort_order)
	 values (2,'2-класс',0);
insert into skill (id,caption,sort_order)
	 values (3,'3-класс',0);
insert into skill (id,caption,sort_order)
	 values (4,'4-класс',0);
insert into skill (id,caption,sort_order)
	 values (5,'5-класс',0);
insert into skill (id,caption,sort_order)
	 values (6,'6-класс',0);
insert into skill (id,caption,sort_order)
	 values (7,'7-класс',0);
insert into skill (id,caption,sort_order)
	 values (8,'8-класс',0);
insert into skill (id,caption,sort_order)
	 values (9,'9-класс',0);
insert into skill (id,caption,sort_order)
	 values (10,'10-класс',0);
insert into skill (id,caption,sort_order)
	 values (11,'11-класс',0);

--  table curriculum 

delete from curriculum;
insert into curriculum (id,caption)
	 values (1,'1-4 классы');
insert into curriculum (id,caption)
	 values (2,'5-9 классы');
insert into curriculum (id,caption)
	 values (3,'10-11 классы');

--  table curriculum_detail 

delete from curriculum_detail;
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,1,2,2,8,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,2,2,2,8,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,3,2,2,8,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,4,2,2,8,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,5,2,2,8,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,2,1,7,2,2,8,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,1,1,8,2,2,8,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,2,1,9,2,2,8,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,1,2,2,9,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,2,2,2,9,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,3,2,2,9,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,4,2,2,9,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,5,2,2,9,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,2,1,7,2,2,9,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,1,1,8,2,2,9,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,2,1,9,2,2,9,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,1,1,5,1,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,22,1,4,1,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,8,1,3,1,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,10,1,4,1,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,11,1,2,1,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,12,1,1,1,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,13,1,1,1,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,14,1,1,1,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,1,1,5,2,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,2,1,7,1,2,2,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,8,1,3,2,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,10,1,4,2,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,11,1,2,2,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,12,1,1,2,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,13,1,1,2,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,14,1,1,2,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,22,1,4,2,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,1,1,5,3,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,2,1,7,1,2,3,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,8,1,3,3,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,10,1,4,3,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,11,1,2,3,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,12,1,1,3,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,13,1,1,3,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,14,1,1,3,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,22,1,4,3,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,1,1,5,4,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,2,1,7,1,2,4,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,8,1,3,4,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,10,1,4,4,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,11,1,2,4,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,12,1,1,4,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,13,1,1,4,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,14,1,1,4,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,15,1,1,4,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,22,1,3,4,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,1,2,6,5,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,16,2,2,5,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,2,2,2,5,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,19,2,2,5,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,2,1,7,2,3,5,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,1,1,8,2,3,5,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,10,2,5,5,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,12,2,2,5,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,14,2,2,5,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,1,3,1,10,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,2,3,3,10,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,5,3,2,10,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,6,3,2,10,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,2,1,7,3,3,10,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,1,1,8,3,3,10,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,17,3,2,10,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,16,3,1,10,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,21,3,1,10,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,20,3,2,10,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,10,3,4,10,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,2,1,9,3,2,10,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,1,3,1,11,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,2,3,3,11,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,5,3,2,11,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,6,3,2,11,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,2,1,7,3,3,11,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,1,1,8,3,3,11,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,2,1,9,3,2,11,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,10,3,4,11,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,16,3,1,11,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,17,3,2,11,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,20,3,2,11,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,21,3,1,11,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,1,2,6,6,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,2,2,2,6,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,2,1,7,2,3,6,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,1,1,8,2,3,6,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,10,2,5,6,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,12,2,2,6,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,14,2,2,6,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,16,2,2,6,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,19,2,2,6,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,1,2,6,7,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,2,2,2,7,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,2,1,7,2,3,7,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,1,1,8,2,3,7,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,10,2,5,7,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,12,2,2,7,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,14,2,2,7,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,16,2,2,7,'false');
insert into curriculum_detail (group_sequence_id,group_type_id,hour_per_day,subject_id,curriculum_id,hour_per_week,skill_id,is_stream)
	 values (0,0,1,19,2,2,7,'false');
