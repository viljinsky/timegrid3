drop view if exists v_teacher_profile;
create view v_teacher_profile as
select s.subject_name,a.subject_id,b.id as teacher_id,a.profile_id
 from profile_item a inner join teacher b on a.profile_id=b.profile_id
inner join subject s on s.id=a.subject_id;

select * from v_teacher_profile;

---------- Профили помещений
drop view if exists  v_room_profile;
create view v_room_profile as
select s.subject_name,a.subject_id,b.id as room_id,a.profile_id
 from profile_item a inner join room b on a.profile_id=b.profile_id
inner join subject s on s.id=a.subject_id;

select * from v_room_profile;
