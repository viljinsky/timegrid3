drop table if exists schedule_state;
create table schedule_state (id integer primary key not null,state_description varchar(40));

insert into schedule_state (id,state_description) values (0,'Новое');
insert into schedule_state (id,state_description) values (1,'В работе');
insert into schedule_state (id,state_description) values (2,'Составлено (есть ошибками)');
insert into schedule_state (id,state_description) values (3,'Составлено');
insert into schedule_state (id,state_description) values (4,'Действует');

select * from schedule_state;

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

drop table if exists group_sequence;
create table group_sequence (id integer primary key ,group_sequence_name varchar(20));

insert into group_sequence (id,group_sequence_name) values (0,'Каждую неделю');
insert into group_sequence (id,group_sequence_name) values (1,'Через неделю');
-- insert into group_sequence (id,group_sequence_name) values (2,'Чётная неделя');



drop table if exists week;
create table week(
    id integer primary key ,             -- 0 каждую неделю; 1,2,.. 1-ая.2-я .. неделя
    caption varchar(10)
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

drop table if exists profile_type;
create table profile_type (
    id integer primary key,
    caption varchar(45),
    default_profile_id integer
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

drop table if exists shift_type;
create table shift_type(
    id integer primary key,
    caption varchar(45),
    default_shift_id integer 
);

drop table if exists shift;
create table shift(
    id integer primary key autoincrement,
    shift_type_id integer references shift_type(id),
    shift_name varchar(18) not null,
    unique (shift_type_id,shift_name)
);

drop table if exists shift_detail;
create table shift_detail(
    shift_id integer references shift(id) on delete cascade,
    day_id integer references day_list(day_no),
    bell_id integer references bell_list(bell_id),
    enable boolean default 1,
    primary key (shift_id,day_id,bell_id),
    unique (shift_id,day_id,bell_id)
);

drop table if exists teacher;
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


drop table if exists skill;
create table skill(
    id integer primary key autoincrement,
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

drop table if exists curriculum; 
create table curriculum(
    id integer primary key autoincrement,
--     skill_id integer not null references skill(id),
    caption varchar(18) not null unique
);

drop table if exists group_type;
create table group_type(
    id integer primary key,
    group_type_caption varchar(40)
);

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


drop table if exists day_list;
create table day_list (
  day_no integer primary key,
  day_short_name varchar(3),
  day_caption varchar(10)
);

drop table if exists bell_list;
create table bell_list (
  bell_id integer primary key,
  time_start time,
  time_end time);

drop table if exists schedule;
create table schedule (
    day_id integer references day_list(day_no),
    bell_id integer references bell_list(bell_id),
--     week_id integer  default 0 references week(id),
    depart_id integer references depart(id) on delete cascade,
    subject_id integer references subject(id),
    group_id integer,
    teacher_id integer references teacher(id),
    room_id integer integer references room(id),
    ready boolean default 'false',
    primary key (day_id,bell_id,depart_id,subject_id,group_id) 
);

----------    Профили учетелей
create view v_teacher_profile as
select s.subject_name,a.subject_id,b.id as teacher_id,a.profile_id
 from profile_item a inner join teacher b on a.profile_id=b.profile_id
inner join subject s on s.id=a.subject_id;

-- select * from v_teacher_profile;

---------- Профили помещений
create view v_room_profile as
select s.subject_name,a.subject_id,b.id as room_id,a.profile_id
 from profile_item a inner join room b on a.profile_id=b.profile_id
inner join subject s on s.id=a.subject_id;

-- select * from v_room_profile;


-- v_subject_group

drop view if exists v_subject_group;
create view v_subject_group as
select 
s.subject_name,
case 
	when c.group_type_id = 0 then ''
	when c.group_type_id = 1  and a.group_id=1 then 'М'
	when c.group_type_id = 1  and a.group_id=2 then 'Д'
	when c.group_type_id = 2 then 'ГР.' || a.group_id
end as group_label,
case when a.week_id = 0 then '' 
     else w.caption end as week_caption,
-- g.group_sequence_name,
t.last_name || ' ' || substr(t.first_name,1,1) ||'. ' || substr(t.patronymic,1,1) ||'.' as teacher,
r.room_name as room,
a.group_id,a.depart_id,a.subject_id,c.group_type_id,a.stream_id,c.hour_per_week,c.hour_per_day,c.group_sequence_id,
a.default_teacher_id,a.default_room_id,a.pupil_count,
a.week_id,
c.is_stream
 from subject_group a 
	inner join depart b on a.depart_id=b.id 
	inner join curriculum_detail c
 		on c.curriculum_id=b.curriculum_id and c.subject_id=a.subject_id and c.skill_id=b.skill_id
	inner join subject s on a.subject_id=s.id
	inner join week w on w.id = a.week_id
        left join teacher t on t.id=a.default_teacher_id
        left join room r on r.id = a.default_room_id
        order by a.depart_id,s.subject_name,a.group_id;

-- select * from v_subject_group;
--                          v_subject_group_on_schedule

drop view if exists v_subject_group_on_schedule;
create view  v_subject_group_on_schedule as
select a.group_label,a.depart_id,a.subject_id,a.group_id,a.hour_per_week,a.hour_per_day,a.group_type_id,a.default_teacher_id,a.default_room_id,
count(b.bell_id) as placed,
a.hour_per_week - count(b.bell_id) as unplaced,
a.stream_id,a.group_sequence_id,a.pupil_count
 from v_subject_group a
left join schedule b on a.depart_id=b.depart_id and a.subject_id=b.subject_id and a.group_id=b.group_id
group by a.depart_id,a.subject_id,a.group_id,a.hour_per_week,a.hour_per_day,a.group_id,a.group_type_id,a.pupil_count;

-- select * from v_subject_group_on_schedule;

-- v_schedule

drop view if exists v_schedule;
create view v_schedule as
select f.label as depart_label,dl.day_caption,bl.time_start ||'-'||time_end as lesson_time, 
  s.subject_name,
  a.group_label,
  c.last_name || ' ' || substr(c.first_name,1,1) || '. ' || substr(c.patronymic,1,1) || '.' as teacher,
  d.room_name as room,
  g.building_name as building,
  b.day_id,
  b.bell_id,
  a.depart_id,
  a.group_id,
  a.week_id,
  a.subject_id,
  a.group_type_id,
  b.teacher_id,
  b.room_id,
  a.stream_id,
  d.building_id,
  b.ready,
  s.color,
  f.schedule_state_id
from v_subject_group a 
    inner join schedule b
        on a.depart_id=b.depart_id and a.subject_id=b.subject_id and a.group_id=b.group_id
    left join teacher c on c.id=b.teacher_id
    left join room d on d.id=b.room_id
    left join building g on d.building_id=g.id
    inner join subject s on s.id =a.subject_id
    inner join day_list dl on dl.day_no=b.day_id
    inner join bell_list bl on bl.bell_id=b.bell_id
    inner join depart f on f.id=a.depart_id;

--   v_teacher

create view v_teacher_hour as
select a.default_teacher_id,c.group_sequence_id,cast(sum(c.hour_per_week) as real) as total_hour
 from subject_group a inner join depart b
on a.depart_id=b.id
inner join curriculum_detail c on c.skill_id=b.skill_id and b.curriculum_id=c.curriculum_id and c.subject_id=a.subject_id
group by a.default_teacher_id,c.group_sequence_id;

drop view if exists v_teacher;
create view v_teacher as
select 
a.last_name || ' ' || substr(a.first_name,1,1) || '. ' || substr(a.patronymic,1,1) || '.' as teacher_fio,
a.last_name,a.first_name,a.patronymic,b.profile_name,c.room_name,d.building_name,s.shift_name,
th.total_hour,a.id,a.profile_id,a.teacher_room_id,a.shift_id
 from teacher a
left join profile b
	on a.profile_id=b.id
left join shift s on s.id=a.shift_id
left join room c
	on a.teacher_room_id=c.id
left join building d on d.id=c.building_id
left join v_teacher_hour th on th.default_teacher_id=a.id;


-- v_room


-- v_depart_on_schedule

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

-----------------------  v_depart ----------------------------------------------

drop view if exists v_depart;
create view  v_depart as
select
a.label as depart_label,
s.shift_name,
b.caption as curriculum_caption,
t.last_name || ' ' || substr(t.first_name,1,1) || '. ' || substr(t.patronymic,1,1) || '.'as teacher,
r.room_name  as room,
g.building_name as main_building,
a.boy_count,
a.gerl_count,
ss.state_description,
a.id as depart_id,
a.curriculum_id,
a.skill_id,
a.shift_id
from depart a
inner join curriculum b on a.curriculum_id=b.id
inner join skill c on c.id=a.skill_id
inner join shift s on s.id=a.shift_id
left join teacher t on a.class_former=t.id
left join room r on r.id=a.class_room
left join building g on g.id=r.building_id
left join schedule_state ss on ss.id=a.schedule_state_id;
select * from v_depart;

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



create view v_room_hour as
select g.default_room_id as room_id,sum(c.hour_per_week) as hour_per_week from depart a inner join curriculum b on a.curriculum_id=b.id
inner join curriculum_detail c on c.curriculum_id=b.id and c.skill_id=a.skill_id 
inner join subject_group g on g.subject_id=c.subject_id and g.depart_id=a.id
group by g.default_room_id
;

drop view if exists v_room;
CREATE VIEW v_room as
select b.building_name,a.room_name,p.profile_name,s.shift_name,
a.capacity,a.id,a.building_id,a.profile_id,a.shift_id,v.hour_per_week
from room a
inner join profile p on p.id =a.profile_id
inner join shift s on s.id=a.shift_id
inner join building b on b.id=a.building_id
left join v_room_hour v on v.room_id=a.id;
select * from v_room;

