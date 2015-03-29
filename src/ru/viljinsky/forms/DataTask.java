/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.sql.PreparedStatement;
import java.util.Map;
import ru.viljinsky.DataModule;
import ru.viljinsky.Dataset;
import ru.viljinsky.KeyMap;

/**
 *
 * @author вадик
 */
interface IDataTaskConstants{
}

public class DataTask implements IDataTaskConstants{
    protected static DataModule dataModule = DataModule.getInstance();
    
    /**
     * Доболение все предметов в учебный план
     * @param curriculum_id
     * @throws Exception 
     */
    public static void fillCurriculumn(Integer curriculum_id) throws Exception{
        String sql = "insert into curriculum_detail (curriculum_id,subject_id,hour_per_day,hour_per_week,group_type_id)\n"+
                     "select curriculum.id,subject.id,subject.default_hour_per_day,subject.default_hour_per_week,subject.default_group_type_id \n"+
                     "from curriculum ,subject where curriculum.id=?;";
        KeyMap map = new KeyMap();
        map.put(1, curriculum_id);
        dataModule.execute(sql, map);
        
    }
    
    /**
     * Удаление всех подчинённых записей учебного плана
     * @param curriculumn_id
     * @throws Exception 
     */
    public static void removeCurriculum(Integer curriculumn_id) throws Exception{
        String sql = "delete from curriculum_detail where curriculum_id=?;";
        KeyMap map = new KeyMap();
        map.put(1, curriculumn_id);
        dataModule.execute(sql, map);
    }
    
    public static void fillSubjectGroup(Integer depart_id) throws Exception{
        String sql ="insert into subject_group (group_id,depart_id,subject_id)\n" +
                    "select 1 as group_id,a.id as depart_id,b.subject_id\n"+
                    "from depart a\n" +
                    " inner join curriculum_detail b \n" +
                    " on a.curriculum_id=b.curriculum_id\n" +
                    " where a.id=?;";
        KeyMap map = new KeyMap();
        map.put(1, depart_id);
        dataModule.execute(sql, map);
    }
    
    
    public static void fillSubjectGroup2(Integer depart_id) throws Exception{
        String sql = 
                "select a.subject_id,a.group_type_id,a.a.hour_per_day,a.hour_per_week from curriculum_detail a "
                + "inner join depart b on a.curriculum_id=b.curriculum_id "
                + "where b.id="+depart_id+";";
        
        Dataset dataset = dataModule.getSQLDataset(sql);
        dataset.open();
        Map<String,Object> values;
        
        sql = " insert into subject_group(group_id,subject_id,depart_id) values (?,?,?) ";
        PreparedStatement stmt = null;
        int group_id;
        int group_type_id;
        try{
            stmt = dataModule.getConnection().prepareStatement(sql);

            for (int i=0;i<dataset.size();i++){
                values=dataset.getValues(i);
                System.out.println(values);
                group_id=1;
                group_type_id = (Integer)values.get("group_type_id");
                
                stmt.setObject(1, group_id++);
                stmt.setObject(2, values.get("subject_id"));
                stmt.setObject(3, depart_id);
                stmt.execute();
                switch (group_type_id){
                    case 1:case 2:{
                        stmt.setObject(1, group_id++);
                        stmt.execute();
                    }
                }
            }
        } finally {
            if (stmt!=null) stmt.close();
        }
    }
    
    public static void clearSubjectGroup(Integer depart_id) throws Exception{
        String sql = "delete from subject_group where depart_id=?;";
        KeyMap map = new KeyMap();
        map.put(1, depart_id);
        dataModule.execute(sql, map);
    }
    
    
    
    /**
     * Заполнение расписания класса
     * @param depart_id
     * @throws Exception 
     */
    public static void fillSchedule(Integer depart_id) throws Exception{
        if (depart_id==null)
            throw new Exception("DEPART IS NULL");
        Map<String,Object> values;
        PreparedStatement stmt = null;
        
        System.out.println("fill depart_id="+depart_id);
        String sql = 
                "select a.depart_id,a.group_id,a.subject_id , a.default_teacher_id as teacher_id,a.default_room_id as room_id,\n" +
                "c.hour_per_week,c.hour_per_day\n" +
                "from subject_group a \n" +
                "inner join depart b on b.id=a.depart_id\n" +
                "inner join curriculum_detail c \n" +
                "	on c.curriculum_id=b.curriculum_id\n" +
                "        and c.subject_id=a.subject_id\n" +
                "where a.depart_id="+depart_id+";";
        
        String inserSql = 
                "insert into schedule \n"
                + "(day_id,bell_id,depart_id,group_id,subject_id,teacher_id,room_id,week_id) \n"
                + "values (?,?,?,?,?,?,?,0);";
        Dataset dataset = dataModule.getSQLDataset(sql);
        dataset.open();
        
        stmt = dataModule.getConnection().prepareStatement(inserSql);
        dataModule.startTrans();
        int day_no=1,bell_id=1,hour_per_day,hour_per_week;
        Integer group_id,subject_id,teacher_id,room_id;
        try{
        
            for (int i=0;i<dataset.size();i++){
                
                values=dataset.getValues(i);
                group_id=(Integer)values.get("group_id");
                subject_id = (Integer)values.get("subject_id");
                teacher_id = (Integer)values.get("teacher_id");
                room_id=(Integer)values.get("room_id");
                hour_per_day=(Integer)values.get("hour_per_day");
                hour_per_week =(Integer)values.get("hour_per_week");
                
                stmt.setObject(3, depart_id);
                stmt.setObject(4, group_id);
                stmt.setObject(5, subject_id);
                stmt.setObject(6, teacher_id);
                stmt.setObject(7, room_id);
                
                while (hour_per_week>0){
                    stmt.setObject(1, day_no);
                    stmt.setObject(2, bell_id);
                    stmt.execute();
                    hour_per_week-=1;
                    day_no+=1;
                    if (day_no>5){
                        day_no=0;
                        bell_id+=1;
                    }
                }
            }
        
        } finally {
            dataModule.stopTrans();
        }
                
    }
    /**
     * Очистка расписания класса
     * @param depart_id
     * @throws Exception 
     */
    public static void clearSchedule(Integer depart_id) throws Exception{
        System.out.println("clear depart_id="+depart_id);
        String sql = "delete from schedule where depart_id="+depart_id+";";
        dataModule.execute(sql);
        
        
    }
    
    
    public static void fillProfile(Integer profile_id) throws Exception{
        String sql = "insert into profile_item (profile_id,subject_id)\n"
                + "select profile.id,subject.id from profile,subject where profile.id="+profile_id;
        dataModule.execute(sql);
        
    }
    
    public static void fillShift(Integer shift_id) throws Exception{
        String sql = "insert into shift_detail(shift_id,day_id,bell_id) \n"
                + "select shift.id,day_no,bell_id from shift,day_list,bell_list\n"
                + "where shift.id="+shift_id;
        dataModule.execute(sql);
    }
}
