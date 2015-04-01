--  свободные часы из графика преподавателя
select b.day_id,b.bell_id from teacher a 
inner join shift_detail b on a.shift_id=b.shift_id
where a.id=1 and 
not exists (select * from schedule where day_id=b.day_id and bell_id=b.bell_id and teacher_id=a.id)
order by b.bell_id,b.day_id;