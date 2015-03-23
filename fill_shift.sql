insert into shift_detail (day_id,bell_id,shift_id)
select day_no,bell_id ,id  as shift_id from day_list,bell_list,shift
order by shift_id;

select * from shift_detail;