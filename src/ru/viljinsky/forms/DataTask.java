/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.viljinsky.DataModule;
import ru.viljinsky.Dataset;
import ru.viljinsky.KeyMap;
import ru.viljinsky.Recordset;

/**
 *
 * @author вадик
 */
interface IDataTaskConstants{
}

interface IDataTask{
    
}

class Values extends HashMap<String,Object>{
    public static final String COLUMN_NOT_FOUND="COLUMN_NOT_FOUND";
    public Integer getInteger(String columnName) throws Exception{
        if (containsKey(columnName)){
            Object result = get(columnName);
            if (result==null)
                return null;
            return (Integer)result;
        }
        throw new Exception(COLUMN_NOT_FOUND+"'"+columnName+"'");
    }
}

public class DataTask implements IDataTask, IDataTaskConstants{
//    protected static DataModule dataModule = DataModule.getInstance();
    
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
            DataModule.execute(sql, map);
            DataModule.commit();
        } catch (Exception e){
            DataModule.rollback();
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
            DataModule.execute(sql, map);
            DataModule.commit();
        } catch (Exception e){
            DataModule.rollback();
            throw new Exception("REMOVE_CURRICULUM_ERROR\n"+e.getMessage());
        }
    }
    
    
    public static void fillSubjectGroup2(Integer depart_id) throws Exception{
        String sql = 
                "select a.subject_id,a.group_type_id,a.a.hour_per_day,a.hour_per_week from curriculum_detail a "
                + "inner join depart b on a.curriculum_id=b.curriculum_id "
                + "where b.id="+depart_id+";";
        
        Dataset dataset = DataModule.getSQLDataset(sql);
        dataset.open();
        Map<String,Object> values;
        
        sql = " insert into subject_group(group_id,subject_id,depart_id) values (?,?,?) ";
        PreparedStatement stmt = null;
        int group_id;
        int group_type_id;
        try{
            stmt = DataModule.getConnection().prepareStatement(sql);

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
            DataModule.commit();
        } catch (Exception e){
            DataModule.reopen();
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
            DataModule.execute(sql, map);
            DataModule.commit();
        } catch (Exception e){
            DataModule.rollback();
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
        Dataset dataset = DataModule.getSQLDataset(sql);
        dataset.open();
        
        stmt = DataModule.getConnection().prepareStatement(inserSql);
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
            DataModule.commit();
        } catch (Exception e){
            DataModule.rollback();
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
        DataModule.execute(sql);
        
        
    }
    
    
    public static void fillProfile(Integer profile_id) throws Exception{
        String sql = "insert into profile_item (profile_id,subject_id)\n"
                + "select profile.id,subject.id from profile,subject where profile.id="+profile_id;
        DataModule.execute(sql);
        
    }
    
    public static void fillShift(Integer shift_id) throws Exception{
        String sql = "insert into shift_detail(shift_id,day_id,bell_id) \n"
                + "select shift.id,day_no,bell_id from shift,day_list,bell_list\n"
                + "where shift.id="+shift_id;
        DataModule.execute(sql);
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
        DataModule.execute(sql, map);
        
    }
    
    public static void excluderGroupFromRoom(int depart_id,int subject_id,int group_id) 
            throws Exception{
        String sql ="update subject_group set default_room_id=null where depart_id=? and subject_id=? and group_id=?;";
        KeyMap map = new KeyMap();
        map.put(1, depart_id);
        map.put(2,subject_id);
        map.put(3,group_id);
        DataModule.execute(sql,map);
    }
    
    ///////////////////////////// TEACHER PANEL ////////////////////////////////
    
    public static void inclideGroupToTeacher(int depart_id,int subject_id,int group_id,int teacher_id)
            throws Exception{
        String sql = "update subject_group set default_teacher_id=?\n"
                   + " where depart_id=? and subject_id=? and group_id=?;";
        KeyMap map = new KeyMap();
        map.put(1, teacher_id);
        map.put(2, depart_id);
        map.put(3, subject_id);
        map.put(4, group_id);
        DataModule.execute(sql, map);
        sql = "update subject_group set default_room_id =(\n" +
              "select teacher_room_id from teacher where id=?)\n" +
              "where depart_id=? and subject_id=? and group_id=?;";
        map.clear();;
        map.put(1,teacher_id);
        map.put(2,depart_id);
        map.put(3,subject_id);
        map.put(4, group_id);
        DataModule.execute(sql, map);
                
//        dataModule.execute("update subject_group set default_room_id=");
        
        
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
        DataModule.execute(sql, map);
    }

    ///////////////////////////  CURRICULUM PANEL //////////////////////////////
    
    public static void includeSubjectFromCurriculumn(Integer curriculum_id, Integer subject_id) throws Exception{
       String sql = "insert into curriculum_detail (curriculum_id,subject_id,hour_per_week,hour_per_day,group_type_id )\n"+
                 "select ?,id,default_hour_per_week,default_hour_per_day,default_group_type_id from subject where id=?;";
       KeyMap map= new KeyMap();
       map.put(1, curriculum_id);
       map.put(2, subject_id);
       DataModule.execute(sql, map);
    }

    public static void excludeSubjectFromCurriculumn(Integer curriculum_id, Integer subject_id) throws Exception {
       String sql = "delete from curriculum_detail where curriculum_id=? and subject_id=?;";
       KeyMap map= new KeyMap();
       map.put(1, curriculum_id);
       map.put(2, subject_id);
       DataModule.execute(sql, map);
    }

    static void excludeSubjectFromProfile(Integer profile_id, Integer subject_id) throws Exception{
        String sql = "delete from profile_item where profile_id=? and subject_id=?";
        KeyMap map = new KeyMap();
        map.put(1, profile_id);
        map.put(2, subject_id);
        DataModule.execute(sql, map);
    }

    static void includeSubjectToProfile(Integer profile_id, Integer subject_id) throws Exception{
        String sql = "insert into profile_item(profile_id,subject_id) values(?,?)";
        KeyMap map = new KeyMap();
        map.put(1, profile_id);
        map.put(2, subject_id);
        DataModule.execute(sql, map);
    }
    
    ////////////////////////  DEPART PANEL /////////////////////////////////////
    /**
     * Добавление/разбиение группы в класс
     * @param depart_id
     * @param subject_id
     * @throws Exception 
     */
    static void addSubjectGroup(Integer depart_id,Integer subject_id) throws Exception{
        String sql = "insert into subject_group (group_id,subject_id,depart_id)\n"+
                "select max(group_id)+1,subject_id,depart_id from subject_group where depart_id=? and subject_id=?";
        KeyMap map = new KeyMap();
        map.put(1, depart_id);
        map.put(2, subject_id);
        try{
            DataModule.execute(sql, map);
            DataModule.commit();
        } catch (Exception e){
            DataModule.rollback();
            throw new Exception("ADD_SUBJECT_GROUP_ERROR\n"+e.getMessage());
        }
    }
    
    /**
     * Удаление группы
     * @param depart_id
     * @param subject_id
     * @param group_id
     * @throws Exception 
     */
    static void deleteSubjectGroup(Integer depart_id,Integer subject_id,Integer group_id) throws Exception{
        String sql ;
        KeyMap map = new KeyMap();
        map.put(1, depart_id);
        map.put(2,subject_id);
        map.put(3, group_id);
        sql = "select group_id from subject_group where depart_id="+depart_id+" and subject_id="+subject_id+";";
        Dataset dataset = DataModule.getSQLDataset(sql);
        dataset.open();
        if (dataset.size()==1){
            throw new Exception("DELETE_SUBJECT_GROUP_ERROR\n"+"Всего одна группа");
        }
        
        
        sql ="delete from subject_group where depart_id=? and subject_id=? and group_id=?";
        try{
            DataModule.execute(sql, map);
            DataModule.commit();
        } catch (Exception e){
            DataModule.rollback();
            throw new Exception("DELETE_SUBJECT_GROUP_ERROR\n"+e.getMessage());
        }
        
    }
    
    public static Integer createStream(String streamCaption,List<Integer[]> list) throws Exception{
        Integer stream_id;//,depart_id,subject_id,group_id;
        try{
            DataModule.execute("insert into stream (stream_caption) values ('"+streamCaption+"')");
            Recordset recordset = DataModule.getRecordet("select max(id) from stream;");
            stream_id= recordset.getInteger(0);
            String sql = "update subject_group set stream_id=? where depart_id=? and subject_id=? ;";
            KeyMap map = new KeyMap();
            map.put(1,stream_id);
                for (Integer[] data:list){

                map.put(2, data[0]);
                map.put(3, data[1]);
                DataModule.execute(sql, map);
            }
            DataModule.commit();
            return stream_id;
        } catch (Exception e){
            DataModule.rollback();
            throw new Exception("CREATE_STREAM_ERROR:\n"+e.getMessage());
        }
    }
    
    public static Integer createStream(Integer depart_id,Integer subject_id) throws Exception{
        try{
            DataModule.execute("insert into stream (stream_caption) values ('новый поток')");
            Recordset recordset = DataModule.getRecordet("select max(id) from stream;");
            Integer stream_id= recordset.getInteger(0);
            String sql = "update subject_group set stream_id=? where subject_id=? and depart_id=?;";
            KeyMap map = new KeyMap();
            map.put(1,stream_id);
            map.put(2, subject_id);
            map.put(3, depart_id);
            DataModule.execute(sql, map);
            DataModule.commit();
            return stream_id;
        } catch (Exception e){
            DataModule.rollback();
            throw new Exception("CREATE_STREAM_ERROR:\n"+e.getMessage());
        }
        
        
    }
    
    public static void excludeFromStream(Integer stream_id,Integer depart_id,Integer subject_id) throws Exception{
        DataModule.execute(String.format("update subject_group set stream_id=null where depart_id=%d and subject_id=%d", depart_id,subject_id));
    }
    
    public static void includeToStream(Integer stream_id,Integer depart_id,Integer subect_id) throws Exception{
        DataModule.execute(String.format("update subject_group set stream_id=%d where depart_id=%d and subject_id=%d", stream_id,depart_id,subect_id));
    }
    
    public static void deleteStream(Integer depart_id,Integer subject_id) throws Exception{
        try{
            Recordset recordset= DataModule.getRecordet("select distinct a.id from stream a inner join subject_group b on a.id=b.stream_id where b.depart_id="+depart_id+" and b.subject_id="+subject_id+";");
            Integer stream_id= recordset.getInteger(0);
            String sql = "update subject_group set stream_id=null where depart_id=? and subject_id=?;";
            KeyMap map = new KeyMap();
            map.put(1,depart_id);
            map.put(2, subject_id);
            DataModule.execute(sql, map);
            
            sql = "delete from stream where (select count(*) from subject_group where stream_id=stream.id)=0;";
            DataModule.execute(sql);
            DataModule.commit();
            
           
        } catch (Exception e){
            DataModule.rollback();
            throw new Exception("DELETE_STREAM_ERROR\n"+e.getMessage());
        }
        
    }
    
    /////////////////////////////  SHIFT ///////////////////////////////////////
    /**
     * Изменение дней и часов в графиках 
     * @param shift_id изменяемы график
     * @param included Список пар Integer[] day_id,bell_id
     * @param excluded Список пар Integer[] day_id,bell_id
     */
    public static void editShift(Integer shift_id,List<Integer[]> included,List<Integer[]> excluded) throws Exception{
        Integer day_id,bell_id;
        for (Integer[] n:excluded){
            day_id=n[0]+1;bell_id=n[1]+1;
            DataModule.execute(String.format("delete from shift_detail where shift_id=%d and day_id=%d and bell_id=%d;",shift_id,day_id,bell_id));
        }
        for (Integer[] n:included){
            day_id=n[0]+1;bell_id=n[1]+1;
            DataModule.execute(String.format("insert into shift_detail (shift_id,day_id,bell_id)values (%d,%d,%d);",shift_id,day_id,bell_id));
        }
    }
    
}
