delete from shift_detail;

insert into shift_detail (day_id,bell_id,shift_id)
select day_list.day_no,bell_list.bell_id ,shift.id  as shift_id from day_list,bell_list,shift
order by shift_id;

select * from shift_detail;
