package ru.viljinsky.forms;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import java.util.Map;
import ru.viljinsky.DataModule;
import ru.viljinsky.Dataset;
import ru.viljinsky.Recordset;
import ru.viljinsky.forms.DataTask;
import ru.viljinsky.forms.EmptyCell;

/**
 *
 * @author вадик
 */
public class ScheduleBuilder {
    
    public static void placeDepart(Integer depart_id,Integer subject_id) throws Exception{
        Integer group_id,teacher_id,room_id;
        String sql = String.format("select group_id,default_teacher_id,default_room_id from subject_group where depart_id=%d and subject_id=%d",depart_id,subject_id);
        Recordset recordset = DataModule.getRecordet(sql);
        group_id=recordset.getInteger(0);
        teacher_id=recordset.getInteger(1);
        room_id = recordset.getInteger(2);
        try{
            EmptyCell cell = DataTask.findEmptyCell(depart_id, teacher_id, room_id);
            sql = String.format(
                    "insert into schedule (day_id,bell_id,depart_id,subject_id,group_id,teacher_id,room_id) "
                   + "values (%d,%d,%d,%d,%d,%d,%d)",
                    cell.day_id,cell.bell_id,depart_id,subject_id,group_id,teacher_id,room_id
                    );
            DataModule.execute(sql);
        } catch (Exception e){
            System.err.println(e.getMessage());
            throw new Exception("PLACE_DEPART_ERROR\n"+e.getMessage());
        }
    }
    
    public static void placeParelelGroup(int depart_id,int subject_id) throws Exception{
    }
    
    public static void placeSeialGroup(int depart_id,int subject_id) throws Exception{
        
    }
    
    public static void main(String[] args) throws Exception{
        DataModule.open();
        DataModule.execute("delete from schedule");
        Dataset dataset = DataModule.getDataset("v_subject_group_on_schedule");
        dataset.open();
        Map<String,Object> values;
        Integer depart_id,subject_id,group_id,hour_per_week,hour_per_day,teacher_id,room_id,
                palced,unplaced;
        int day_id, bell_id;
        EmptyCell cell ;
        
        Integer group_type_id;
        
        for (int i=0;i<dataset.size();i++){
            values = dataset.getValues(i);
            group_type_id= (Integer)values.get("group_type_id");
            if (group_type_id!=0){
                continue;
            }
            depart_id=(Integer)values.get("depart_id");
            subject_id=(Integer)values.get("subject_id");
//            group_id=(Integer)values.get("group_id");
//            teacher_id=(Integer)values.get("default_teacher_id");
//            room_id=(Integer)values.get("default_room_id");
            unplaced =(Integer)values.get("unplaced");
            
            if (unplaced==0){
                continue;
            }
            
            try{
                for (int j=0;j<unplaced;j++){
                    placeDepart(depart_id, subject_id);
                }
            } catch (Exception e){
                System.err.println(e.getMessage());
            }
            
//            if (unplaced>0){
//                    try{
//                        for (int j=0;j<unplaced;j++){
//                            cell = DataTask.findEmptyCell(depart_id, teacher_id, room_id);
//                            String sql = String.format("insert into schedule (day_id,bell_id,depart_id,group_id,subject_id,teacher_id,room_id,week_id)"
//                                    +" values (%d,%d,%d,%d,%d,%d,%d,%d)",cell.day_id,cell.bell_id,depart_id,group_id,subject_id,teacher_id,room_id,0);
//                            System.out.println(sql);
//                            DataModule.execute(sql);
//                            }
//                    } catch (Exception e){
//                        e.printStackTrace();
//                }
//            }
        }
        DataModule.commit();
        System.out.println("OK");
    }
    
}
