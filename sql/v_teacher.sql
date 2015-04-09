drop view if exists v_teacher;
create view v_teacher as
select a.last_name,a.first_name,a.patronymic,b.profile_name,c.name,d.caption,
a.id,a.profile_id,a.teacher_room_id,a.shift_id
 from teacher a
left join profile b
	on a.profile_id=b.id
left join room c
	on a.teacher_room_id=c.id
left join building d on d.id=c.building_id;
select * from v_teacher;
