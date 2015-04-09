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
    
    public static final int  GT_ALL_DEPART  = 0;
    public static final int  GT_MALE_FAMELE = 1;
    public static final int  GT_GROUP = 2;

    /**
     * Весь класс один академический час
     * @param depart_id
     * @param subject_id
     * @throws Exception 
     */
    public static void placeDepartHour(Integer depart_id,Integer subject_id) throws Exception{
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
    /**
     * Группы параллельно один академический час
     * @param depart_id
     * @param subject_id
     * @throws Exception 
     */
    public static void placeParallelGroupHour(int depart_id,int subject_id) throws Exception{
        Integer teacher_id,room_id,group_id;
        Recordset recordset = DataModule.getRecordet(String.format("select group_id,default_teacher_id,default_room_id from subject_group where depart_id=%d and subject_id=%d",depart_id,subject_id));
        EmptyCell cell = DataTask.findEmptyCell(depart_id, null, null);
        String sql = "insert into schedule (day_id,bell_id,depart_id,subject_id,group_id,teacher_id,room_id)"
                + " values (%d,%d,%d,%d,%d,%d,%d)";
        try{
            for (int i=0;i<recordset.size();i++){
                group_id=(Integer)recordset.get(i)[0];
                teacher_id =(Integer)recordset.get(i)[1];
                room_id =(Integer)recordset.get(i)[2];
                DataModule.execute(String.format(sql,cell.day_id,cell.bell_id,depart_id,subject_id,group_id,teacher_id,room_id ));
            }
        } catch (Exception e){
            e.printStackTrace();
            throw new Exception("PLACE_PARALLEL_GROUP_ERROR\n"+e.getMessage());
        }
    }
    /**
     * Группы последовательно один академический час
     * @param depart_id
     * @param subject_id
     * @throws Exception 
     */
    public static void placeSeialGroupHour(int depart_id,int subject_id) throws Exception{
        
    }
    
    
    public static void placeDepart(Integer depart_id) throws Exception{
        Dataset dataset = DataModule.getSQLDataset("select * from v_depart_on_schedule where depart_id="+depart_id);
        Map<String,Object> values;
        Integer group_type_id,subject_id,unplaced;
        dataset.open();
        try{
            for (int row=0;row<dataset.getRowCount();row++){
                values = dataset.getValues(row);
                unplaced = (Integer)values.get("hour_per_week")-(Integer)values.get("placed");
                if (unplaced<=0)
                    continue;
                group_type_id=(Integer)values.get("group_type_id");
                subject_id=(Integer)values.get("subject_id");
                for (int count=0;count<unplaced;count++){
                    switch (group_type_id){
                        case GT_ALL_DEPART:
                            placeDepartHour(depart_id,subject_id);
                            break;
                        case GT_MALE_FAMELE:
                            placeParallelGroupHour(depart_id,subject_id);
                            break;
                        case GT_GROUP:
                            placeParallelGroupHour(depart_id,subject_id);
                            break;
                    }
                }
            }
            DataModule.commit();
        } catch (Exception e){
            DataModule.rollback();
            e.printStackTrace();
            throw new Exception("PLACE_DEPART_ERROR\n"+e.getMessage());
        }
        
    }
    
    public static void main(String[] args) throws Exception{
        DataModule.open();
        DataModule.execute("delete from schedule");
        Dataset dataset = DataModule.getDataset("v_depart_on_schedule");
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
//            if (group_type_id!=0){
//                continue;
//            }
            depart_id=(Integer)values.get("depart_id");
            subject_id=(Integer)values.get("subject_id");
            
            unplaced =(Integer)values.get("hour_per_week")-(Integer)values.get("placed");
            
            if (unplaced<=0){
                continue;
            }
            
            try{
                for (int j=0;j<unplaced;j++){
                    switch (group_type_id){
                        case 0:placeDepartHour(depart_id, subject_id);
                            break;
                        case 1:placeParallelGroupHour(depart_id,subject_id);
                            break;
                        case 2: placeParallelGroupHour(depart_id,subject_id);
                            break;   
                    }
                }
            } catch (Exception e){
                System.err.println(e.getMessage());
            }
            
        }
        DataModule.commit();
        System.out.println("OK");
    }
    
}
