-- Статус расписания
create table schedule_state (id integer primary key not null,state_description varchar(40));

insert into schedule_state (id,state_description) values (0,'Новое');
insert into schedule_state (id,state_description) values (1,'В работе');
insert into schedule_state (id,state_description) values (2,'Составлено (есть ошибками)');
insert into schedule_state (id,state_description) values (3,'Составлено');
insert into schedule_state (id,state_description) values (4,'Действует');


-- Тип группы;
create table group_type(
    id integer primary key,
    group_type_caption varchar(40)
);

insert into group_type (id,group_type_caption) values (0,'весь класс');
insert into group_type (id,group_type_caption) values (1,'м.-д.');
insert into group_type (id,group_type_caption) values (2,'группы');


-- Тип профиля для ;
create table profile_type (
    id integer primary key,
    caption varchar(45),
    default_profile_id integer
);

insert into profile_type(id,caption,default_profile_id) values (1,'Профиль преподавателя',1);
insert into profile_type(id,caption,default_profile_id) values (2,'Профиль помещения',8);


-- Регулярность проведения занятий
create table group_sequence (
    id integer primary key ,
    group_sequence_name varchar(20)
);

insert into group_sequence (id,group_sequence_name) values (0,'Каждую неделю');
insert into group_sequence (id,group_sequence_name) values (1,'Через неделю');

-- Недели 
create table week(
    id integer primary key ,             -- 0 каждую неделю; 1,2,.. 1-ая.2-я .. неделя
    caption varchar(10)
);
insert into week(id,caption) values (0,'I/II нед.');
insert into week(id,caption) values (1,'I нед.');
insert into week(id,caption) values (2,'II нед.');


-- Предметная область
create table subject_domain(
    id innteger primary key not null,
    domain_caption varchr(40) not null
);

drop table if exists stream;
create table stream (
    id integer primary key autoincrement,
    stream_caption varchar(40),
    subject_id integer references subject(id),
    skill_id integer references skill(id),
    room_id integer references room(id),
    teacher_id integer references teacher(id)
);


drop table if exists building;
create table building(
    id integer primary key autoincrement,
    building_name varchar(20) unique
);

drop table if exists room;
create table room(
    id integer primary key autoincrement,
    room_name varchar(18) not null unique,
    capacity integer,
    building_id integer not null references building(id),
    profile_id integer references profile(id),
    shift_id integer references shift(id)
--    constraint unique (building_id) 

);

drop table if exists subject;
create table subject(
    id integer primary key autoincrement,
    subject_name varchar(20) not null unique,
    default_hour_per_week integer ,
    default_group_type_id integer references group_type(id),
    default_hour_per_day integer ,
    subject_domain_id integer references subject_domain(id),
    color varchar(11) default '240 240 240'
--     color_rgb varchar(20),
);

drop table if exists profile;
create table profile(
    id integer primary key autoincrement,
    profile_type_id integer references profile_type(id),
    profile_name varchar(18) not null,
    unique(profile_type_id,profile_name)
);

drop table if exists profile_item;
create table profile_item(
    profile_id integer references profile(id) on delete cascade,
    subject_id integer references subject(id),
    primary key (profile_id,subject_id)
);

-- Тип графика
create table shift_type(
    id integer primary key,
    caption varchar(45),
    default_shift_id integer 
);
insert into shift_type(id,caption,default_shift_id) values (1,'График класса',1);
insert into shift_type(id,caption,default_shift_id) values (2,'График преподователя',3);
insert into shift_type(id,caption,default_shift_id) values (3,'График помещения',5);

-- Графики
create table shift(
    id integer primary key autoincrement,
    shift_type_id integer references shift_type(id),
    shift_name varchar(18) not null,
    unique (shift_type_id,shift_name)
);

-- Дни и часы графиков
create table shift_detail(
    shift_id integer references shift(id) on delete cascade,
    day_id integer references day_list(day_no),
    bell_id integer references bell_list(bell_id),
    enable boolean default 1,
    primary key (shift_id,day_id,bell_id),
    unique (shift_id,day_id,bell_id)
);

-- Список преподавателей
create table teacher(
    id integer primary key autoincrement,
    last_name varchar(18) not null,
    first_name varchar(18),
    patronymic varchar(18),
    photo binary,
    profile_id integer not null references profile(id),
    shift_id integer  references shift(id),
    teacher_room_id integer references room(id) on delete set null,
    comments blob
    
);


-- Уровеь обучения
create table skill(
    id integer primary key autoincrement,
    sort_order integer not null default 0,
    caption varchar(18) unique
);

drop table if exists depart;
create table depart(
    id integer primary key autoincrement,
    label varchar(10) not null unique,
    skill_id integer not null references skill(id),
    shift_id integer not null references shift(id),
    curriculum_id integer not null references curriculum(id),
    class_room integer references room(id),
    class_former integer references teacher(id),
    boy_count integer,
    gerl_count integer,
    schedule_state_id integer not null references schedule_state(id) default 0 
);

drop table if exists subject_group;
create table subject_group (
    group_id integer,
    depart_id integer references depart(id) on delete cascade,
    subject_id integer references subject(id) on delete restrict,
    default_teacher_id integer references teacher(id),
    default_room_id integer references room(id),
    week_id integer references week(id) default 0,
    stream_id integer references stream(id) on delete set null,
    pupil_count integer,
    primary key (depart_id,subject_id,group_id)
);

-- Учебный план
drop table if exists curriculum; 
create table curriculum(
    id integer primary key autoincrement,
    caption varchar(18) not null unique
);
-- Ддетали учебного плана
drop table if exists curriculum_detail;
create table curriculum_detail(
    curriculum_id integer references curriculum(id) on delete cascade,
    skill_id integer references skill(id),
    subject_id integer references subject(id) on delete restrict on update cascade,
    hour_per_day integer not null,
    hour_per_week integer not null,
    group_type_id integer not null default 0 references group_type(id),
    group_sequence_id integer default 0 references group_sequence(id),
    is_stream boolean default false,
    constraint pk_cur_det primary key (curriculum_id,skill_id,subject_id),
    constraint check_hour check (hour_per_week>=hour_per_day)    
);

-- список дней в сетке расписания
drop table if exists day_list;
create table day_list (
  day_no integer primary key,
  day_short_name varchar(3),
  day_caption varchar(10)
);
-- список часо в седке расписания
drop table if exists bell_list;
create table bell_list (
  bell_id integer primary key,
  time_start time,
  time_end time);

-- сетка расписания
drop table if exists schedule;
create table schedule (
    day_id integer references day_list(day_no),
    bell_id integer references bell_list(bell_id),
    depart_id integer references depart(id) on delete cascade,
    subject_id integer references subject(id),
    group_id integer,
    teacher_id integer references teacher(id),
    room_id integer integer references room(id),
    ready boolean default 'false',
    primary key (day_id,bell_id,depart_id,subject_id,group_id) 
);

--------------------------------------------------------------------------------

create view v_depart_on_schedule as
select distinct a.depart_id,a.subject_id,c.group_type_id,
 (select count() from schedule 
                where depart_id=a.depart_id 
                and subject_id=a.subject_id and group_id=a.group_id)  as placed,
  c.hour_per_week
from subject_group a 
       inner join depart b on a.depart_id=b.id 
       inner join curriculum_detail c on 
       c.curriculum_id=b.curriculum_id and c.subject_id=a.subject_id;


drop view if exists v_curriculum_detail;
create view v_curriculum_detail as
select s.subject_name,g.group_sequence_name,t.group_type_caption,a.hour_per_week,a.hour_per_day,a.is_stream,
a.subject_id,a.curriculum_id,a.group_type_id,a.group_sequence_id,a.skill_id
 from curriculum_detail a
 inner join subject s on a.subject_id=s.id
 inner join group_sequence g on g.id=a.group_sequence_id
 inner join group_type t on t.id=a.group_type_id 
;


---  Ноаый куррикулум
create view v_curriculum as
select a.id as curriculum_id,a.caption as curriculum,
b.id as skill_id,b.caption as skill from
curriculum a,skill b;

-- select * from v_curriculum;


--- Для расчётов всободных часов
create view v_schedule_calc as
select a.day_id,a.bell_id,a.depart_id,a.group_id ,c.group_type_id --,a.week_id
from schedule a 
inner join depart d on a.depart_id=d.id
inner join curriculum_detail c 
	on  c.skill_id=d.skill_id 
	and c.curriculum_id=d.curriculum_id
	and c.subject_id=a.subject_id
order by a.day_id,a.bell_id;

------------------------security--------------------------

create table user_role(
	id integer primary key,
	role_name varchar(45) not null unique
);

create table users (
	id integer primary key autoincrement,
        user_name varchar(18) not null unique,
	password  varchar(45),
	nick_name varchar(45) not null,
	user_role_id integer not null references user_role(id)	
);

insert into user_role (id,role_name) values (1,'admin');
insert into user_role (id,role_name) values (2,'teacher');
insert into user_role (id,role_name) values (3,'guest');

insert into users (user_name,nick_name,user_role_id) values ('admin','Администратор',1);


create table attr (param_name varchar(40) not null unique,
param_value varchar(40)
);

insert into attr(param_name,param_value) values ('date_begin','1/1/2015');
insert into attr(param_name,param_value) values ('date_end','1/1/2015');
insert into attr(param_name,param_value) values ('schedule_span','2015/2016 учебный год');
insert into attr(param_name,param_value) values ('schedule_title','Первая четверть');
insert into attr(param_name,param_value) values ('educational_institution','Среднее школа № 212');





