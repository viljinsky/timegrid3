----------    Профили учетелей
create view v_teacher_profile as
select s.subject_name,a.subject_id,b.id as teacher_id,a.profile_id
 from profile_item a inner join teacher b on a.profile_id=b.profile_id
inner join subject s on s.id=a.subject_id;


---------- Профили помещений
create view v_room_profile as
select s.subject_name,a.subject_id,b.id as room_id,a.profile_id
 from profile_item a inner join room b on a.profile_id=b.profile_id
inner join subject s on s.id=a.subject_id;



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

-- v_subject_group_on_schedule
drop view if exists v_subject_group_on_schedule;
create view  v_subject_group_on_schedule as
select a.group_label,a.depart_id,a.subject_id,a.group_id,a.hour_per_week,a.hour_per_day,a.group_type_id,a.default_teacher_id,a.default_room_id,
count(b.bell_id) as placed,
a.hour_per_week - count(b.bell_id) as unplaced,
a.stream_id,a.group_sequence_id,a.pupil_count
 from v_subject_group a
left join schedule b on a.depart_id=b.depart_id and a.subject_id=b.subject_id and a.group_id=b.group_id
group by a.depart_id,a.subject_id,a.group_id,a.hour_per_week,a.hour_per_day,a.group_id,a.group_type_id,a.pupil_count;



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



create view v_room_hour as
select g.default_room_id as room_id,sum(c.hour_per_week) as hour_per_week 
from depart a inner join curriculum b on a.curriculum_id=b.id
inner join curriculum_detail c on c.curriculum_id=b.id and c.skill_id=a.skill_id 
inner join subject_group g on g.subject_id=c.subject_id and g.depart_id=a.id
group by g.default_room_id
;
---------------  v_room --------------------------------------------------------
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

