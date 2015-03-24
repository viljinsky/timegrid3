insert into subject_group (group_id,depart_id,subject_id)
select 1 as group_id,a.id as depart_id,b.subject_id from depart a
 inner join curriculum_detail b 
 on a.curriculum_id=b.curriculum_id
;

select * from subject_group;