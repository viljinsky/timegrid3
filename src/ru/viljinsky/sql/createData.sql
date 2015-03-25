drop table if exists subject;
create table subject(
    id integer primary key autoincrement,
    subject_name varchar(20),
    default_hour_per_week integer,
    default_group_type_id integer references group_type(id),
    default_hour_per_day integer ,
    color integer
);
drop table if exists profile_type;
create table profile_type (
    id integer primary_key,
    caption varchar(45)
);

drop table if exists profile;
create table profile(
    id integer primary key autoincrement,
    profile_type_id integer references profile_type(id),
    name varchar(18)
);

drop table if exists profile_item;
create table profile_item(
    profile_id integer references profile(id),
    subject_id integer references subject(id),
    primary key (profile_id,subject_id)
);

drop table if exists shift_type;
create table shift_type(
    id integer primary key,
    caption varchar(45)
);

drop table if exists shift;
create table shift(
    id integer primary key autoincrement,
    shift_type_id integer references shift_type(id),
    name varchar(18)
);

drop table if exists shift_detail;
create table shift_detail(
    shift_id integer references shift(id),
    day_id integer references day_list(day_no),
    bell_id integer references bell_list(bell_id),
    enable boolean dafult 'true',
    unique (shift_id,day_id,bell_id)
);

drop table if exists teacher;
create table teacher(
    id integer primary key autoincrement,
    last_name varchar(18),
    first_name varchar(18),
    patronymic varchar(18),
    profile_id integer references profile(id),
    shift_id integer references shift(id)
);

drop table if exists room;
create table room(
    id integer primary key autoincrement,
    name varchar(18),
    profile_id integer references profile(id),
    shift_id integer references shift(id)
);

drop table if exists skill;
create table skill(
    id integer primary key autoincrement,caption varchar(18)
);

drop table if exists depart;
create table depart(
    id integer primary key autoincrement,
    label varchar(10),
    skill_id integer references skill(id),
    shift_id integer references shift(id),
    curriculum_id integer references curriculum(id),
    class_room integer references room(id),
    class_former integer references teacher(id),
    boy_count integer,
    gerl_count integer 
);

drop table if exists subject_group;
create table subject_group (
    group_id integer,
    depart_id integer references depart(id),
    subject_id integer references subject(id),
    default_teacher_id integer references teacher(id),
    default_room_id integer references room(id),
    primary key (depart_id,subject_id,group_id) on conflict fail
);

drop table if exists curriculum; 
create table curriculum(
    id integer primary key autoincrement,
    caption varchar(18));

drop table if exists group_type;
create table group_type(
    id integer primary key,
    group_type_caption varchar(40)
);

drop table if exists curriculum_detail;
create table curriculum_detail(
    curriculum_id integer references curriculum(id),
    subject_id integer references subject(id),
    hour_per_day integer,
    hour_per_week integer,
    group_type_id integer references group_type(id),
    primary key (curriculum_id,subject_id)

);

-- create table work_plan (depart_id integer);

drop table if exists day_list;
create table day_list (day_no integer primary key,day_caption);

drop table if exists bell_list;
create table bell_list (
  bell_id integer primary key,
  time_start time,
  time_end time);

drop table if exists schedule;
create table schedule (
    day_id integer,
    bell_id integer,
    depart_id integer references depart(id),
    subject_id integer,
    group_id integer,
    teacher_id integer references teacher(id),
    room_id integer integer references room(id) 
);


