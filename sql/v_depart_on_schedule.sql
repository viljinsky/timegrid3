select a.depart_id,a.subject_id,bell_id,day_id,count(*)
 from subject_group a left join schedule b on a.depart_id=b.depart_id
and a.subject_id=b.subject_id and a.group_id=b.group_id
group by a.depart_id,a.subject_id,a.group_id;

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

select * from v_depart_on_schedule order by group_type_id desc;
