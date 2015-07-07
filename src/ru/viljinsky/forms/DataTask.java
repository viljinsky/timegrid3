/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.awt.Point;
import java.sql.PreparedStatement;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ru.viljinsky.sqlite.DataModule;
import ru.viljinsky.sqlite.Dataset;
import ru.viljinsky.sqlite.KeyMap;
import ru.viljinsky.sqlite.Recordset;
//import static ru.viljinsky.timetree.Depart.sql;

/**
 *
 * @author вадик
 */
interface IDataTaskConstants{
}

interface IDataTask{
    
}


public class DataTask implements IDataTask, IDataTaskConstants{
    
    public static Integer getLastId(String tableName) throws Exception{
        Recordset r = DataModule.getRecordet("select max(id) from "+tableName);
        return r.getInteger(0);
    }
    
    public static Integer getDefaultBuildingId() throws Exception{
        
        Recordset r=DataModule.getRecordet("select id from building limit 1");
        return r.getInteger(0);
        
    }
    
    /**
     * Поиск профиля по умолчаниию для профиля
     * @param profile_id
     * @return
     * @throws Exception 
     */
    public static Integer getDefaultProfileId(Integer profile_id) throws Exception{
        String sql = "select default_profile_id from profile_type a inner join profile b on a.id=b.profile_type_id where b.id=%profile_id";
        Recordset r = DataModule.getRecordet(sql.replace("%profile_id", profile_id.toString()));
        return r.getInteger(0);
    }
    
    public static Integer getProfileTypeId(Integer profile_id) throws Exception{
        String sql = "select profile_type_id from profile where id=%profile_id";
        Recordset r = DataModule.getRecordet(sql.replace("%profile_id", profile_id.toString()));
        return r.getInteger(0);
    }
        
    public static Integer getDefaultProfileId(String tableName) throws Exception{
        Recordset r;
        String sql ;
        switch (tableName){
            case "teacher":
                sql = "select default_profile_id from profile_type where id=1";
                break;
            case "room":
                sql = "select default_profile_id from profile_type where id=2";
                break;
            default:
                throw new Exception("UNKNOW_TABLE_NAME\n"+tableName);
        }
        r=DataModule.getRecordet(sql);
        return r.getInteger(0);
    };
    
    public static Integer getDefaultShiftId(String tableName) throws Exception{
        Recordset r;
        String sql;
        switch (tableName){
            case "teacher":
                sql = "select default_shift_id from shift_type where id=2";
                break;
            case "room":
                sql = "select default_shift_id from shift_type where id=3";
                break;
            case "depart":
                sql = "select default_shift_id from shift_type where id=1";
                break;
            default:
                throw new Exception ("INKNOW_TABLE_NAME_ERROR\n"+tableName);
        };
        r = DataModule.getRecordet(sql);
        return r.getInteger(0);
    }
    
    /**
     * Доболение все предметов в учебный план
     * @param curriculum_id
     * @throws Exception 
     */
    public static void fillCurriculumn(Integer curriculum_id,Integer skill_id) throws Exception{
        String sql = "insert into curriculum_detail (curriculum_id,skill_id,subject_id,hour_per_day,hour_per_week,group_type_id)\n"+
                     "select curriculum.id,?,subject.id,subject.default_hour_per_day,subject.default_hour_per_week,subject.default_group_type_id \n"+
                     "from curriculum ,subject where curriculum.id=? ;";
        KeyMap map = new KeyMap();
        map.put(1, skill_id);
        map.put(2, curriculum_id);
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
    public static void removeCurriculum(Integer curriculumn_id,Integer skill_id) throws Exception{
        String sql = "delete from curriculum_detail where curriculum_id=? and skill_id=?;";
        KeyMap map = new KeyMap();
        map.put(1, curriculumn_id);
        map.put(2, skill_id);
        try{
            DataModule.execute(sql, map);
            DataModule.commit();
        } catch (Exception e){
            DataModule.rollback();
            throw new Exception("REMOVE_CURRICULUM_ERROR\n"+e.getMessage());
        }
    }
    
    public static void addSubjectToDepart(Integer depart_id,Integer subject_id) throws Exception{
        String sql = "insert into subject_group (group_id,depart_id,subject_id)values (1,%d,%d)";
        DataModule.execute(String.format(sql,depart_id,subject_id));
        
    }
    
    public static void removeSubjectFromDepart(Integer depart_id,Integer subject_id) throws Exception{
     String sql  = "delete from subject_group where depart_id=%d and subject_id=%d";
     DataModule.execute(String.format(sql, depart_id,subject_id));
    }
    
    public static void fillSubjectGroup2(Integer depart_id) throws Exception{
        String sql = 
                "select a.subject_id,a.group_type_id,a.a.hour_per_day,a.hour_per_week from curriculum_detail a "
                + "inner join depart b on a.curriculum_id=b.curriculum_id and a.skill_id=b.skill_id "
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
//                System.out.println(values);
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
            
            sql = "update subject_group set week_id=group_id where subject_id in (\n"
                    +"select a.subject_id \n" +
                    "from curriculum_detail a \n" +
                    "  inner join depart b \n" +
                    "    on a.curriculum_id = b.curriculum_id\n" +
                    "   and a.group_sequence_id>0 \n" +
                    "   where b.id=subject_group.depart_id\n" +
                    "   and b.id=%d " 
                    + " );";
            DataModule.execute(String.format(sql,depart_id));            
            DataModule.commit();
            
        } catch (Exception e){
            
            DataModule.rollback();
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
        
        sql = "update schedule set room_id=? where depart_id=? and subject_id=? and group_id=?";
        DataModule.execute(sql,map);
        
    }
    
    public static boolean scheduleDepartIsUsed(Integer depart_id) throws Exception{
        Recordset r = DataModule.getRecordet("select schedule_state_id from depart where id="+depart_id);
        if (r.getInteger(0)==4){
            throw new Exception("DEPART_SCHEDULE_IS_USED");
        }
        return false;
    }
    
    public static void excluderGroupFromRoom(int depart_id,int subject_id,int group_id) 
            throws Exception{
        if (scheduleDepartIsUsed(depart_id))
            return;
        String sql ="update subject_group set default_room_id=? where depart_id=? and subject_id=? and group_id=?;";
        KeyMap map = new KeyMap();
        map.put(1,null);
        map.put(2, depart_id);
        map.put(3,subject_id);
        map.put(4,group_id);
        DataModule.execute(sql,map);
        
        sql ="update schedule set room_id=? where depart_id=? and subject_id=? and group_id=?;";
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
        map.clear();
        map.put(1,teacher_id);
        map.put(2,depart_id);
        map.put(3,subject_id);
        map.put(4, group_id);
        DataModule.execute(sql, map);
        
        sql = "update schedule set teacher_id=? where depart_id=? and subject_id=? and group_id=? and room_id is null";
        DataModule.execute(sql,map);
                
//        dataModule.execute("update subject_group set default_room_id=");
        
        
    }

    public static void excludeGroupFromTeacher(int depart_id,int subject_id,int group_id) 
            throws Exception{
        if (scheduleDepartIsUsed(depart_id))
            return;
        String sql = "update subject_group set default_teacher_id=?\n"
                   + "where depart_id=? and subject_id=? and group_id=?;";
        KeyMap map = new KeyMap();
        map.put(1, null);
        map.put(2, depart_id);
        map.put(3, subject_id);
        map.put(4, group_id);
        DataModule.execute(sql, map);
        
        sql = "update schedule set teacher_id=?\n"
                   + "where depart_id=? and subject_id=? and group_id=?;";
        DataModule.execute(sql, map);
    }

    ///////////////////////////  CURRICULUM PANEL //////////////////////////////
    private static final String SQL_INCLUDE_SUBJECT_TO_CURRICULUM =
        "insert into curriculum_detail (curriculum_id,skill_id,subject_id,hour_per_week,hour_per_day,group_type_id )\n"+
        "select ?,?,id,default_hour_per_week,default_hour_per_day,default_group_type_id from subject where id=?;";
    
    public static void includeSubjectToCurriculumn(Integer curriculum_id, Integer skill_id,Integer subject_id) throws Exception{
//       String sql = "insert into curriculum_detail (curriculum_id,skill_id,subject_id,hour_per_week,hour_per_day,group_type_id )\n"+
//                 "select ?,?,id,default_hour_per_week,default_hour_per_day,default_group_type_id from subject where id=?;";
       KeyMap map= new KeyMap();
       map.put(1, curriculum_id);
       map.put(2, skill_id);
       map.put(3, subject_id);
       DataModule.execute(SQL_INCLUDE_SUBJECT_TO_CURRICULUM, map);
       
       Recordset r = DataModule.getRecordet(String.format("select id from depart where skill_id=%d and curriculum_id=%d",skill_id,curriculum_id));
       Integer depart_id;
       for (int i=0;i<r.size();i++){
            depart_id=(Integer)r.get(i)[0];
            addSubjectToDepart(depart_id, subject_id);
       }
    }

    private static final String SQL_EXCLUDE_SUBJECT_FROM_CURRICULUM =
            "delete from curriculum_detail where curriculum_id=? and skill_id=? and subject_id=?;";
    public static void excludeSubjectFromCurriculumn(Integer curriculum_id, Integer skill_id, Integer subject_id) throws Exception {
       KeyMap map= new KeyMap();
       map.put(1, curriculum_id);
       map.put(2, skill_id);
       map.put(3, subject_id);       
       DataModule.execute(SQL_EXCLUDE_SUBJECT_FROM_CURRICULUM, map);
       
       Recordset r = DataModule.getRecordet(String.format("select id from depart where skill_id=%d and curriculum_id=%d",skill_id,curriculum_id));
       Integer depart_id;
       for (int i=0;i<r.size();i++){
            depart_id=(Integer)r.get(i)[0];
            removeSubjectFromDepart(depart_id, subject_id);
       }
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
    public static void editShift(Integer shift_id,Set<Point> included,Set<Point> excluded) throws Exception{
        Integer day_id,bell_id;
        for (Point n:excluded){
            day_id=n.x+1;bell_id=n.y+1;
            DataModule.execute(String.format("delete from shift_detail where shift_id=%d and day_id=%d and bell_id=%d;",shift_id,day_id,bell_id));
        }
        for (Point n:included){
            day_id=n.x+1;bell_id=n.y+1;
            DataModule.execute(String.format("insert into shift_detail (shift_id,day_id,bell_id)values (%d,%d,%d);",shift_id,day_id,bell_id));
        }
    }
    
    private static final String SQL_TEACHER_AVALABLE_CELLS =
        "select day_id-1,bell_id-1 from shift_detail a inner join " +
        "teacher b on a.shift_id=b.shift_id where b.id=%d;";
            ;
    public static Set<Point> getTeacherAvalableCells(Integer teacher_id) throws Exception{
        Set<Point> result = new HashSet();
        Point p;
        Object[] r;
        Recordset recordset = DataModule.getRecordet(String.format(SQL_TEACHER_AVALABLE_CELLS, teacher_id));
        for (int i = 0; i < recordset.size(); i++) {
            r = recordset.get(i);
            result.add(new Point((Integer) r[0], (Integer) r[1]));
        }
        return result;
    }
    
    private static final String SQL_ROOM_AVALABLE_CELLS =
            "select day_id-1,bell_id-1 from shift_detail a inner join " +
            "room b on a.shift_id=b.shift_id where b.id=%d;";
    
    public static Set<Point> getRoomAvalableCells(Integer room_id) throws Exception{
        Set<Point> result = new HashSet<>();
        Point p;
        Object[] r;
        Recordset recordset = DataModule.getRecordet(String.format(SQL_ROOM_AVALABLE_CELLS, room_id));
        for (int i = 0; i < recordset.size(); i++) {
            r = recordset.get(i);
            result.add(new Point((Integer) r[0], (Integer) r[1]));
        }
        return result;
    }
    
    private static final String SQL_DEPART_AVALABLE_CELLS =
            "select day_id-1,bell_id-1 from shift_detail a inner join " +
            "depart b on a.shift_id=b.shift_id where b.id=%d;";
    
    public static Set<Point> getDepartAvalableCells(Integer depart_id) throws Exception{
        Set<Point> result = new HashSet<>();
        Object[] p;
        try {
            Recordset resordset = DataModule.getRecordet(
                    String.format(SQL_DEPART_AVALABLE_CELLS, depart_id));
            for (int i = 0; i < resordset.size(); i++) {
                p = resordset.get(i);
                result.add(new Point((Integer) p[0], (Integer) p[1]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
