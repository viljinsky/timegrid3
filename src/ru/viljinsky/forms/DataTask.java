/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.sql.PreparedStatement;
import java.util.HashMap;
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

interface IDataTask{
    
}

class Values extends HashMap<String,Object>{
    public Integer getInteger(String columnName) throws Exception{
        if (containsKey(columnName)){
            Object result = get(columnName);
            if (result==null)
                return null;
            return (Integer)result;
        }
        throw new Exception("COLUMN_NOT_FOUND '"+columnName+"'");
    }
}

public class DataTask implements IDataTask, IDataTaskConstants{
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
        try{
            dataModule.execute(sql, map);
            dataModule.commit();
        } catch (Exception e){
            dataModule.rollback();
            throw new Exception("FILL_CRURRICULUM_ERROR"+e.getMessage());
        }
        
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
        try{
            dataModule.execute(sql, map);
            dataModule.commit();
        } catch (Exception e){
            dataModule.rollback();
            throw new Exception("REMOVE_CURRICULUM_ERROR\n"+e.getMessage());
        }
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
            dataModule.commit();
        } catch (Exception e){
            dataModule.reopen();
            throw new Exception("FILL_SUBJECT_GROUP_ERROR\n"+e.getMessage());
        } finally {
            if (stmt!=null) stmt.close();
        }
    }
    
    public static void clearSubjectGroup(Integer depart_id) throws Exception{
        String sql = "delete from subject_group where depart_id=?;";
        KeyMap map = new KeyMap();
        map.put(1, depart_id);
        try{
            dataModule.execute(sql, map);
            dataModule.commit();
        } catch (Exception e){
            dataModule.rollback();
            throw new Exception("CLEAR_SUBJECT_GROUP_ERROR\n"+e.getMessage());
        }
    }
    
    
    
    public static EmptyCell findEmptyCell(Integer depart_id,Integer teacher_id,Integer room_id) throws Exception{
        CellsList list1,list2;
        list1 = TimeCell.getEmptyDepartCell(depart_id);
        if (list1.isEmpty()){
            throw new Exception ("DEPART_HAS_NOT_EMPTY_CELL");
        }
        
        if (teacher_id!=null){
            list2 = TimeCell.getEmptyTeacherCell(teacher_id);
            if (list2.isEmpty()){
                throw new Exception ("TEACHER_HAS_NOT_EMPTY_CELL");
            }
            list1.intersect(list2);
        }
        if (room_id!=null){
            list2 = TimeCell.getEmptyRoomCell(room_id);
            if (list2.isEmpty()){
                throw new Exception("ROOM_HAS_NOT_EMPTY_CELL");
            }
            list1.intersect(list2);
        }
        if (list1.isEmpty()){
            throw new Exception("AFTER_INTERSECT_HAS_NOT_EMPTY_CELL");
        }
        return list1.getPrefferedValue();
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
        int hour_per_day,hour_per_week;
        Integer group_id,subject_id,teacher_id,room_id;
        EmptyCell emptyCell;
        
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
                
                for (int count=0;count<hour_per_week;count++){

                    System.out.println(String.format("d: %d s:%d t:%d :r%d", depart_id,subject_id,teacher_id,room_id));
                    emptyCell = findEmptyCell(depart_id, teacher_id, room_id);
                        
                    stmt.setObject(1, emptyCell.day_id);
                    stmt.setObject(2, emptyCell.bell_id);
                    
                    
                    stmt.executeUpdate();
                }
            }
            dataModule.commit();
        } catch (Exception e){
            dataModule.rollback();
            throw new Exception("FILL_SCHEDULE_ERROR\n"+e.getMessage());
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
    
    ///////////////////////////// ROOM PANEL ///////////////////////////////////
    public static void includeGroupToRoom(int depart_id,int subject_id,int group_id,int room_id)
            throws Exception{
        String sql = "update subject_group set default_room_id=? where depart_id=? and subject_id=? and group_id=?;";
        KeyMap map = new KeyMap();
        map.put(1, room_id);
        map.put(2, depart_id);
        map.put(3, subject_id);
        map.put(4, group_id);
        dataModule.execute(sql, map);
        
    }
    
    public static void excluderGroupFromRoom(int depart_id,int subject_id,int group_id) 
            throws Exception{
        String sql ="update subject_group set default_room_id=null where depart_id=? and subject_id=? and group_id=?;";
        KeyMap map = new KeyMap();
        map.put(1, depart_id);
        map.put(2,subject_id);
        map.put(3,group_id);
        dataModule.execute(sql,map);
    }
    
    ///////////////////////////// TEACHER PANEL ////////////////////////////////
        // перенести в DataTask
    public static void inclideGroupToTeacher(int depart_id,int subject_id,int group_id,int teacher_id)
            throws Exception{
        String sql = "update subject_group set default_teacher_id=?\n"
                   + " where depart_id=? and subject_id=? and group_id=?;";
        KeyMap map = new KeyMap();
        map.put(1, teacher_id);
//            map.put(2, room_id);
        map.put(2, depart_id);
        map.put(3, subject_id);
        map.put(4, group_id);
        dataModule.execute(sql, map);
    }

    public static void excludeGroupFromTeacher(int depart_id,int subject_id,int group_id) 
            throws Exception{
        String sql = "update subject_group set default_teacher_id=?\n"
                   + "where depart_id=? and subject_id=? and group_id=?;";
        KeyMap map = new KeyMap();
        map.put(1, null);
        map.put(2, depart_id);
        map.put(3, subject_id);
        map.put(4, group_id);
        dataModule.execute(sql, map);
    }
    
}
