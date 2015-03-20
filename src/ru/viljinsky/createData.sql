create table subject(
    id integer primary key autoincrement,
    subject_name varchar(20)
);

create table profile_type (
    id integer primary_key,
    caption varchar(45)
);

create table profile(
    id integer primary key autoincrement,
    profile_type_id integer references profile_type(id),
    name varchar(18)
);

create table profile_item(
    profile_id integer references profile(id),
    subject_id integer references subject(id),
    primary key (profile_id,subject_id)
);

create table shift_type(
    id integer primary key,
    caption varchar(45)
);

create table shift(
    id integer primary key autoincrement,
    shift_type_id integer references shift_type(id),
    name varchar(18)
);

create table shift_detail(
    shift_id integer references shift(id),
    day_id integer references day_list(id),
    bell_id integer references bell_list(id)
);

create table teacher(
    id integer primary key autoincrement,
    last_name varchar(18),
    first_name varchar(18),
    patronymic varchar(18),
    profile_id integer references profile(id),
    shift_id integer references shift(id)
);

create table room(
    id integer primary key autoincrement,
    name varchar(18),
    profile_id integer references profile(id),
    shift_id integer references shift(id)
);

create table skill(
    id integer primary key autoincrement,caption varchar(18)
);
create table depart(
    id integer primary key autoincrement,
    skill_id integer references skill(id),
    shift_id integer references shift(id),
    curriculum_id integer references curriculum(id) 
);

create table subject_group (
    group_id integer,
    depart_id integer references depart(id),
    subject_id integer references subject(id),
    default_teacher_id integer references teacher(id),
    default_room_id integer references room(id),
    primary key (depart_id,subject_id,group_id)
);

create table curriculum(id integer primary key autoincrement,caption varchar(18));

create table group_type(
    id integer primary key,
    group_type_caption varchar(40)
);

create table curriculum_detail(
    curriculum_id integer references curriculun(id),
    subject_id integer references subject(id),
    hour_per_day integer,
    hour_per_week integer,
    group_type_id integer references group_type(id),
    primary key (curriculum_id,subject_id)

);

create table work_plan (depart_id integer);
create table day_list (day_no integer primary key,day_caption);
create table bell_list (bell_id integer primary key,time_start time,time_end time);
create table schedule (
    day_id integer,
    bell_id integer,
    depart_id integer,
    subject_id integer,
    group_id integer,
    teacher_id integer,
    room_id integer
);

create view v_teacher as
select a.id,a.first_name,a.last_name from teacher a;


