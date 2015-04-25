/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import ru.viljinsky.dialogs.BaseDialog;
import ru.viljinsky.dialogs.EntryDialog;
import ru.viljinsky.DataModule;
import ru.viljinsky.Dataset;
import ru.viljinsky.dialogs.EntryPanel;
import ru.viljinsky.IDataset;
import ru.viljinsky.KeyMap;
import ru.viljinsky.Recordset;
import ru.viljinsky.SelectDialog;
import ru.viljinsky.Values;

/**
 *
 * @author вадик
 */
abstract class AbstractShiftDialog extends ShiftDialog{
    Integer shift_id ;
    EntryPanel entryPanel = new EntryPanel();
    
    public AbstractShiftDialog(Integer shift_id) {
        super();
        this.shift_id=shift_id;        
        getContentPane().add(entryPanel,BorderLayout.PAGE_START);
        try{
            Dataset dataset = DataModule.getDataset("shift");
            entryPanel.setDataset(dataset);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    public void setValues(Values values){
        entryPanel.setValues(values);
    }
    
    public Values getValues(){
        return entryPanel.getValues();
    }
    
}

abstract class AbstractProfileDialog extends SelectDialog{
    Integer profile_id;
    EntryPanel entryPanel = new EntryPanel();
    
    public AbstractProfileDialog(Integer profile_id) {
        super();
        this.profile_id=profile_id;
        add(entryPanel,BorderLayout.PAGE_START);
        try{
            Dataset dataset = DataModule.getDataset("profile");
            entryPanel.setDataset(dataset);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
}

abstract class AbstractStreamDialog extends SelectDialog{
    JTextField field = new JTextField(20);
    Integer stream_id;
    EntryPanel entryPanel = new EntryPanel();
    
    public void setStreamId(Integer stream_id){
        this.stream_id= stream_id;
        if (stream_id!=null){
            try{
                Recordset recordset = DataModule.getRecordet("select id,stream_caption,subject_id from stream where id="+stream_id);
                Values values = new Values();
                values.put("id", recordset.get(0)[0]);
                values.put("stream_caption", recordset.get(0)[1]);
                values.put("subject_id", recordset.get(0)[2]);
                entryPanel.setValues(values);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public AbstractStreamDialog() {
        super();
        add(entryPanel,BorderLayout.PAGE_START);
        try{
            Dataset dataset = DataModule.getDataset("stream");
            entryPanel.setDataset(dataset);
        } catch (Exception e){
            e.printStackTrace();
        }
        
    }

    public void setStreamCaption(String caption){
        field.setText(caption);
    }

    public String getStreamCaption(){
        return field.getText();
    }

}

abstract class CurriculumDialog extends SelectDialog{
    EntryPanel entryPanel = new EntryPanel();
    public CurriculumDialog() {
        super();
        getContentPane().add(entryPanel,BorderLayout.PAGE_START);
        try{
            Dataset dataset = DataModule.getDataset("curriculum");
            entryPanel.setDataset(dataset);
        } catch (Exception e){
            e.printStackTrace();
        }

    }
    
    public void setValues(Values values){
        entryPanel.setValues(values);
    }
    
    public Values getValues(){
        return entryPanel.getValues();
    }

}

        
public class Dialogs {
    public static final String NOT_READY_YET = "Не готово ещё";
    
    public static final String INCLUDE_EXCLUDE_PROFILE_ERROR = "INCLUDE_EXCLUDE_PROFILE_ERROR\n";
    public static final String CREATE_PROFILE_ERROR = "CREATE_PROFILE_ERROR\n";
    public static final String REMOVE_PROFILE_ERROR = "REMOVE_PROFILE_ERROR\n";
    
    public static final String CREATE_SHIFT_ERROR = "CREATE_SHIFT_ERROR\n";
    public static final String EDIT_SHIFT_ERROR = "EDIT_SIFT_ERROR\n";
    public static final String REMOVE_SIFT_ERROR = "REMOVE_SIFT_ERROR\n";
    
    public static final String WRONG_STREAM = "WRONG_STREAM";
    public static final String EDIT_STREAM_ERROR = "EDIT_STREAM_ERROR\n";
    public static final String CONFIRM_REMOVE_STREAM = "CONFIRM_REMOVE_STREAM";
            
    public static Integer createProfile(JComponent owner,Integer profile_id) throws Exception{
        AbstractProfileDialog dlg = new AbstractProfileDialog(profile_id) {
            
            @Override
            public void doOnEntry() throws Exception {
                try{
                    String profile_name = (String)entryPanel.getValues().get("profile_name");                
                    Integer profile_type_id = entryPanel.getValues().getInteger("profile_type_id");
                    String sql = "insert into profile (profile_type_id,profile_name) values (%d,'%s');";
                    DataModule.execute(String.format(sql,profile_type_id,profile_name));
                    Recordset recordset = DataModule.getRecordet("select max(id) from profile");
                    profile_id = (Integer)recordset.get(0)[0];

                    KeyMap map = new KeyMap();
                    map.put(1, profile_id);
                    for (Object obj:getSelected()){
                        map.put(2, obj);
                        DataModule.execute("insert into profile_item (profile_id,subject_id) values (?,?);",map);
                    }
                    DataModule.commit();
                } catch (Exception e){
                    DataModule.rollback();
                    throw new Exception("CREATE_PROFILE_ERROR\n"+e.getMessage());
                }
            }
        };

        
        Dataset dataset;
        dataset = DataModule.getDataset("subject");
        dlg.setDataset(dataset, "id", "subject_name");
        
        Recordset recordset = DataModule.getRecordet("select a.caption,\n" +
        "(select count(*) from profile where profile_type_id=a.id) as count,\n" +
        "b.profile_type_id from profile_type a inner join profile b\n" +
        "on a.id=b.profile_type_id\n" +
        "where b.id="+profile_id+";");
        Values values = new Values();
        values.put("profile_name",recordset.getString(0)+"("+recordset.getString(1)+")");
        values.put("profile_type_id", recordset.getInteger(2));
        dlg.entryPanel.setValues(values);
        dlg.showModal(owner);
        if (dlg.modalResult==SelectDialog.RESULT_OK)
            return dlg.profile_id;
        return null;
    }
    
    public static boolean editProfile(JComponent owner,Integer profile_id) throws Exception{
        AbstractProfileDialog dlg = new AbstractProfileDialog(profile_id) {

            @Override
            public void doOnEntry() throws Exception {
                try{
                    Values values = entryPanel.getValues();
                    profile_id = values.getInteger("id");
                    Dataset dataset = DataModule.getDataset("profile");
                    Map<String,Object> filter = new HashMap<>();
                    filter.put("id", profile_id);
                    dataset.open(filter);
                    dataset.edit(0, values);
                    
                    for (Object k:getRemoved()){
                        DataTask.excludeSubjectFromProfile(profile_id,(Integer)k);
                    }
                    for (Object k:getAdded()){
                        DataTask.includeSubjectToProfile(profile_id,(Integer)k);
                    }
                    DataModule.commit();
                } catch (Exception e){
                    DataModule.rollback();
                    throw new Exception(INCLUDE_EXCLUDE_PROFILE_ERROR+e.getMessage());
                }
            }
        };
        
        IDataset dataset = DataModule.getSQLDataset("select * from profile where id="+profile_id);
        dataset.open();
//        dlg.entryPanel.setDataset(dataset);
        if (!dataset.isEmpty()){
            Map<String,Object> map;
            map = dataset.getValues(0);
            dlg.entryPanel.setValues(map);
        }


        Set<Object> set = new HashSet<>();
        Recordset rs = DataModule.getRecordet("select subject_id from profile_item where profile_id="+profile_id);
        for (int i=0;i<rs.size();i++){
            set.add(rs.get(i)[0]);
        }
        dataset = DataModule.getDataset("subject");
        dlg.setDataset(dataset, "id", "subject_name");
        dlg.setSelected(set);
        dlg.profile_id=profile_id;
        return (dlg.showModal(owner)==SelectDialog.RESULT_OK);
        
    }
    
    public static boolean removeProfile(JComponent owner,Integer profile_id) throws Exception{
        throw new UnsupportedOperationException(NOT_READY_YET);
    }
    
    public static Integer  createShift(JComponent owner,Integer shift_id) throws Exception{
        AbstractShiftDialog dlg = new AbstractShiftDialog(shift_id) {

            @Override
            public void doOnEntry() throws Exception {
                try{
                    Values values = getValues();
                    Dataset dataset = DataModule.getDataset("shift");
                    dataset.open();
                    dataset.appned(values);
                    shift_id = values.getInteger("id");
                    DataTask.editShift(shift_id, getAdded(),getRemoved());
                    DataModule.commit();
                } catch (Exception e){
                    DataModule.rollback();
                    throw new Exception("ON_ENTRY_ERROR\n"+e.getMessage());
                }
            }
            
        };

        Recordset recordset = DataModule.getRecordet("select a.caption,b.shift_type_id,(select count(*) from shift where shift_type_id=a.id) as count\n" +
        " from shift_type a inner join shift b on a.id=b.shift_type_id\n" +
        "where b.id="+shift_id+";");
        
        Values values = new Values();
        values.put("shift_name", recordset.getString(0)+"("+recordset.getString(2)+")");
        values.put("shift_type_id", recordset.getInteger(1));
        dlg.setValues(values);
        dlg.showModal(owner);
        if (dlg.modalResult==SelectDialog.RESULT_OK){
            return dlg.shift_id;
        }
        return null;
    }
    
    public static boolean editShift(JComponent owner,Integer shift_id) throws Exception{
        
        AbstractShiftDialog dlg = new AbstractShiftDialog(shift_id){

            @Override
            public void doOnEntry() throws Exception {
                try{
                    String shift_name = (String)entryPanel.getValues().get("shift_name");
                    DataModule.execute("update shift set shift_name='"+shift_name+"' where id="+shift_id);
                    DataTask.editShift(shift_id, getAdded(),getRemoved());
                    DataModule.commit();
                } catch(Exception e){
                    DataModule.rollback();
                    throw new Exception(EDIT_SHIFT_ERROR+e.getMessage());
                }
            }

        };
        
        Dataset dataset = DataModule.getSQLDataset("select * from shift where id="+shift_id);
        dataset.open();
//        dlg.entryPanel.setDataset(dataset);
        dlg.entryPanel.setValues(dataset.getValues(0));
        
        Recordset rs = DataModule.getRecordet(String.format("select day_id,bell_id from shift_detail where shift_id=%d;",shift_id));
        Object[] values;
        int day_id,bell_id;
        List<Integer[]> list = new ArrayList<>();
        for (int i=0;i<rs.size();i++){
            values = rs.get(i);
            day_id=(Integer)values[0]-1;
            bell_id=(Integer)values[1]-1;
            list.add(new Integer[]{day_id,bell_id});
                    
        }
        
        dlg.setSelected(list);
        dlg.showModal(owner);
        return (dlg.modalResult==SelectDialog.RESULT_OK);
    }
    
    public static boolean removeShift() throws Exception{
        throw new UnsupportedOperationException(NOT_READY_YET);
    }
    
    ///////////////////////////////////////////////////////////////////////////
    
    private static final String sqlSubjectGroupToStream = 
            "select a.depart_id || '-' || a.subject_id || '-' ||a.group_id as key," 
          + " c.label ||' ' || b.subject_name  || a.subject_id || a.group_id as value \n" 
          + "from subject_group a "
          + "   inner join subject b on a.subject_id=b.id "
          + "   inner join depart c on c.id=a.depart_id "
          + "   where a.subject_id=%d "  
          + "   order by a.subject_id,a.depart_id ;";

    public static boolean createStream(JComponent owner, Integer depart_id, Integer subject_id, Integer group_id) throws Exception {
        String sql = sqlSubjectGroupToStream;
        AbstractStreamDialog dlg = new AbstractStreamDialog() {
            @Override
            public void doOnEntry() throws Exception {
                Integer depart_id,subject_id,group_id;
//                List<Integer[]> list = new ArrayList<>();
                for (Object obj : getSelected()) {
                    String s = (String) obj;
                    String[] ss = s.split("-");
                    depart_id = Integer.valueOf(ss[0]);
                    subject_id = Integer.valueOf(ss[1]);
                    group_id = Integer.valueOf(ss[2]);
                    System.out.println(String.format("depart %d group_id %d subject_id %d ", depart_id, group_id, subject_id));
                    DataModule.execute(String.format("update subject_group set stream_id=%d where depart_id=%d and subject_id=%d and group_id=%d",stream_id,depart_id,subject_id,group_id));
//                    list.add(new Integer[]{depart_id, subject_id, group_id});
                }
                
//                DataTask.createStream("StreanNew", list);
            }
        };
        // моздание записи нового стрима
        DataModule.execute("insert into stream (stream_caption,subject_id) values ('поток "+subject_id+"',"+subject_id+")");
        Recordset recordset = DataModule.getRecordet("select id from stream where id=(select max(id) from stream)");
        
        String s = String.format("%d-%d-%d", depart_id, subject_id, group_id);
        Dataset dataset = DataModule.getSQLDataset(String.format(sql,subject_id));
        dlg.setDataset(dataset, "key", "value");
        Set set = new HashSet();
        set.add(s);
        
        dlg.setStreamId(recordset.getInteger(0));
        dlg.setSelected(set);
        dlg.showModal(owner);
        return dlg.modalResult == SelectDialog.RESULT_OK;
    }

    public static boolean editStream(JComponent owner, Integer stream_id) throws Exception {
        AbstractStreamDialog dlg = new AbstractStreamDialog() {
            @Override
            public void doOnEntry() throws Exception {
                Integer depart_id,subject_id,group_id;
                String objString;
                String[] v;
                if (getSelected().size() < 2) {
                    throw new Exception(WRONG_STREAM);
                }
                try {
                    for (Object obj : getAdded()) {
                        objString = (String) obj;
                        v = objString.split("-");
                        depart_id = Integer.valueOf(v[0]);
                        subject_id = Integer.valueOf(v[1]);
                        group_id = Integer.valueOf(v[2]);
                        System.out.println("ADDED:" + obj);
                        DataTask.includeToStream(stream_id, depart_id, subject_id);
                    }
                    for (Object obj : getRemoved()) {
                        System.out.println("REMOVED" + obj);
                        objString = (String) obj;
                        v = objString.split("-");
                        depart_id = Integer.valueOf(v[0]);
                        subject_id = Integer.valueOf(v[1]);
                        group_id = Integer.valueOf(v[2]);
                        DataTask.excludeFromStream(stream_id, depart_id, subject_id);
                    }
                    DataModule.commit();
                } catch (Exception e) {
                    DataModule.rollback();
                    throw new Exception(EDIT_STREAM_ERROR + e.getMessage());
                }
            }
        };
        Integer subject_id=6;
        Recordset rs = DataModule.getRecordet("select depart_id||'-'||subject_id ||'-'||group_id from subject_group where stream_id=" + stream_id);
        Set set = new HashSet();
        for (int i = 0; i < rs.size(); i++) {
            set.add(rs.get(i)[0]);
        }
        
        Dataset dataset = DataModule.getSQLDataset(String.format(sqlSubjectGroupToStream,subject_id));
        dlg.setDataset(dataset, "key", "value");
        dlg.setSelected(set);
        dlg.setStreamId(stream_id);
        dlg.showModal(owner);
        return dlg.modalResult == SelectDialog.RESULT_OK;
    }

    public static boolean removeStream(JComponent owner, Integer depart_id, Integer subject_id) throws Exception {
        if (JOptionPane.showConfirmDialog(owner, CONFIRM_REMOVE_STREAM, "TITLE", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            DataTask.deleteStream(depart_id, subject_id);
            return true;
        }
        return false;
    }
    
///////////////////////   CURRICULUM   ////////////////////////////////////////
    
    public static Integer createCurriculum(JComponent owner) throws Exception {
        String curriculumn_name = (String)JOptionPane.showInputDialog(owner, "Новый уч план", "Введите название", JOptionPane.PLAIN_MESSAGE, null, null, "Учебный план");
        if (curriculumn_name!=null){
            try{
                DataModule.execute("insert into curriculum(caption) values ('"+curriculumn_name+"')");
                Recordset r=DataModule.getRecordet("select max(id) from curriculum");
                DataModule.commit();
                return (Integer)r.get(0)[0];
            } catch (Exception e){
                DataModule.rollback();
                throw new Exception("CREATE_CURRICULUM_ERROR\n"+e.getMessage());
            }
        }
        return null;
    }

    public static Boolean editCurriculum(JComponent owner, Integer curriculum_id) throws Exception {
        String caption ;
        Recordset r = DataModule.getRecordet("select caption from curriculum where id="+curriculum_id);
        caption = r.getString(0);
        String curriculumn_name = (String)JOptionPane.showInputDialog(owner, "Учебный план", "Введите название", JOptionPane.PLAIN_MESSAGE, null, null, caption);
        if (curriculumn_name==null)
            return false;
        return true;
    }
    
    public static boolean deleteCurriculum(JComponent owner, Integer curriculum_id) throws Exception{
        if (JOptionPane.showConfirmDialog(owner,"Удалить учебный план","Внимание",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
            try{
                DataModule.execute("delete from curriculum where id ="+curriculum_id+";");
                DataModule.commit();
                return true;
            } catch (Exception e){
                DataModule.rollback();
                throw new Exception("DELETE_CURRICULUM_ERROR\n"+e.getMessage());
            }
        }
        return false;
    }
    
    
    /////////////////////// TEACHER ////////////////////////////////////////////

  
    
    public static Integer createTeacher(JComponent owner) throws Exception{
         Dataset dataset = DataModule.getDataset("teacher");
         dataset.test();
         EntryDialog entryDialog = new EntryDialog() {

            @Override
            public void doOnEntry() throws Exception {
                try{
                    getDataset().appned(getValues());
                    DataModule.commit();
                } catch (Exception e){
                    DataModule.rollback();
                    throw new Exception("CREATE_TEACHER_ERROR\n"+e.getMessage());
                }
            }
        };
        Values values = new Values();
        values.put("shift_id",DataTask.getDefaultShiftId("teacher"));
        values.put("profile_id", DataTask.getDefaultProfileId("teacher"));
        entryDialog.setDataset(dataset);
        entryDialog.setValues(values);
        
        if (entryDialog.showModal(owner)==BaseDialog.RESULT_OK)
            return DataTask.getLastId("teacher") ;
        
        return null;
    }
    
    public static Boolean editTeacher(JComponent owner,Integer teacher_id) throws Exception{
        Dataset dataset = DataModule.getDataset("teacher");
        EntryDialog entryDialog = new EntryDialog() {

            @Override
            public void doOnEntry() throws Exception {
                try{
                    getDataset().edit(0, getValues());
                    DataModule.commit();
                } catch (Exception e){
                    DataModule.rollback();
                    throw new Exception("EDIT_TEACHER_ERROR\n"+e.getMessage());
                }
            }
        };
        Map<String,Object> filter = new HashMap<>();
        filter.put("id", teacher_id);
        dataset.open(filter);
        Values values = dataset.getValues(0);
        entryDialog.setDataset(dataset);
        entryDialog.setValues(values);
        return entryDialog.showModal(owner)==BaseDialog.RESULT_OK;
        
    }
    
    public static boolean deleteTeacher(JComponent owner,Integer teacher_id) throws Exception{
        if (JOptionPane.showConfirmDialog(owner, "Удалить преподавателя","Внимание",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
            try{
                DataModule.execute("delete from teacher where id="+teacher_id+";");
                DataModule.commit();
                return true;
            } catch (Exception e){
                DataModule.rollback();
                throw new Exception("DELETE_TEACHER_ERROR\n"+e.getMessage());
            }
        }
        return false;
    }
    
    
    ///////////////////////    ROOM  ///////////////////////////////////////////
    
    public static Integer createRoom(JComponent owner) throws Exception{
        Recordset r;
        Dataset dataset = DataModule.getDataset("room");
        dataset.test();
//        r = DataModule.getRecordet("select default_shift_id from shift_type where id=3");
//        Integer shift_id=r.getInteger(0);
//        r = DataModule.getRecordet("select default_profile_id from profile_type where id=2");        
//        Integer profile_id=r.getInteger(0);
        Integer building_id;
        r=DataModule.getRecordet("select id from building limit 1");
        building_id = r.getInteger(0);
        
        EntryDialog dlg = new EntryDialog() {

            @Override
            public void doOnEntry() throws Exception {
                try{
                    getDataset().appned(getValues());
                    DataModule.commit();
                } catch (Exception e){
                    DataModule.rollback();
                    throw new Exception("CREATE_ROOM_ERROR\n"+e.getMessage());
                }
            }
        };
        
        Values values = new Values();
        values.put("shift_id",DataTask.getDefaultShiftId("room"));
        values.put("profile_id",DataTask.getDefaultProfileId("room"));
        values.put("building_id",building_id);
        
        dlg.setDataset(dataset);
        dlg.setValues(values);
        
        if (dlg.showModal(owner)==BaseDialog.RESULT_OK)
            return DataTask.getLastId("room");
        
        return null;
    }
    
    public static Boolean editRoom(JComponent owner,Integer room_id) throws Exception{
        Dataset dataset = DataModule.getDataset("room");
        EntryDialog dlg = new EntryDialog() {

            @Override
            public void doOnEntry() throws Exception {
                try{
                    getDataset().edit(0, getValues());
                    DataModule.commit();
                } catch (Exception e){
                    DataModule.rollback();
                    throw new Exception("EDIT_ROOM_ERROR\n"+e.getMessage());
                }
            }
        };
        Map<String,Object> filter = new HashMap<>();
        filter.put("id",room_id);
        dataset.open(filter);
        Values values = dataset.getValues(0);
        dlg.setDataset(dataset);
        dlg.setValues(values);
        return (dlg.showModal(owner)==BaseDialog.RESULT_OK);
        
    }
    
    public static boolean deleteRoom(JComponent owner,Integer room_id) throws Exception{
        if (JOptionPane.showConfirmDialog(owner, "Удалить помещение","Внимание",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
            try{
                DataModule.execute("delete from room where id="+room_id+";");
                DataModule.commit();
                return true;
            } catch (Exception e){
                DataModule.rollback();
                throw new Exception("DELETE_ROOM_ERROR\n"+e.getMessage());
            }
        }
        return false;
    }
    
    //////////////////////  DEPART /////////////////////////////////////////////
    
    
    public static Integer createDepart(JComponent owner,Integer curriculum_id,Integer skill_id) throws Exception{
        Dataset dataset = DataModule.getDataset("depart");
        dataset.test();
        
        EntryDialog entryDialog = new EntryDialog() {

            @Override
            public void doOnEntry() throws Exception {
                try{
                    Dataset dataset = DataModule.getDataset("depart");
                    dataset.test();
                    dataset.appned(getValues());
                    DataModule.commit();
                } catch (Exception e){
                    DataModule.rollback();
                    throw new Exception("CREATE_DEPART_ERROR\n"+e.getMessage());
                }
            }
        };
    
        
        
        Recordset r = DataModule.getRecordet("select caption,(select count(*) from depart where skill_id=a.id)+1 as count from skill a where a.id="+skill_id+";");
        String label = r.getString(0)+" ("+r.getString(1)+")";
        
        r = DataModule.getRecordet(String.format("select count(*) from curriculum_detail where skill_id=%d and curriculum_id=%d",skill_id,curriculum_id));
        if (r.getInteger(0)==0){
            throw new Exception("CURRICULUM_IS_EMPTY");
        }
        
//        r=DataModule.getRecordet("select default_shift_id from shift_type where id=1");
//        Integer shift_id=r.getInteger(0);
//        
        
        entryDialog.setDataset(dataset);
        Values values = new Values();
        values.put("curriculum_id",curriculum_id);
        values.put("skill_id", skill_id);
        values.put("label", label);
        values.put("shift_id",DataTask.getDefaultShiftId("depart"));
        entryDialog.setValues(values);
        if (entryDialog.showModal(owner)==SelectDialog.RESULT_OK){
            r=DataModule.getRecordet("select max(id) from depart");
            return r.getInteger(0);
        }
        return null;
    }
    
    public static Boolean editDepart(JComponent owner,Integer depart_id) throws Exception{
        Dataset dataset = DataModule.getDataset("depart");
        EntryDialog dlg = new EntryDialog() {

            @Override
            public void doOnEntry() throws Exception {
                try{
                    Values values = getValues();
                    Dataset ds = DataModule.getDataset("depart");
                    ds.test();
                    Map<String,Object> filter = new HashMap<>();
                    filter.put("id",values.getInteger("id"));
                    ds.open(filter);
                    ds.edit(0, values);
                    DataModule.commit();
                } catch (Exception e){
                    DataModule.rollback();
                    throw new Exception("EDIT_DEPART_ERROR\n"+e.getMessage());
                }
            }
        };
        Map<String,Object> filter = new Hashtable<>();
        filter.put("id",depart_id);
        dataset.open(filter);
        Values values = dataset.getValues(0);
        
        dlg.setDataset(dataset);
        dlg.setValues(values);
        return (dlg.showModal(owner)==BaseDialog.RESULT_OK);
    }
    
    public static boolean deleteDepart(JComponent owner,Integer depart_id) throws Exception{
        if (JOptionPane.showConfirmDialog(owner, "Удалить подразделение","Внимание",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
            try{
                DataModule.execute("delete from depart where id="+depart_id+";");
                DataModule.commit();
                return true;
            } catch (Exception e){
                DataModule.rollback();
                throw new Exception("DELETE_DEPART_ERROR\n"+e.getMessage());
            }
        }
        return false;
    }

    public static boolean editSubjectGroup(JComponent owner,Integer depart_id, Integer subject_id, Integer group_id) throws Exception {
        EntryDialog dlg = new EntryDialog() {

            @Override
            public void doOnEntry() throws Exception {
                try{
                    Values values = getValues();
                    Map<String,Object> filter = new HashMap<>();
                    filter.put("depart_id", values.getInteger("depart_id"));
                    filter.put("subject_id", values.getInteger("subject_id"));
                    filter.put("group_id", values.getInteger("group_id"));
                    
                    Dataset dataset = DataModule.getDataset("subject_group");
                    dataset.open(filter);
                    dataset.edit(0, values);
                    DataModule.commit();
                } catch (Exception e){
                    DataModule.rollback();
                    throw new Exception("EDIT_SUBJECT_GROUP_ERROR\n"+e.getMessage());
                }
            }
        };
        Dataset dataset = DataModule.getSQLDataset(String.format("select * from subject_group where depart_id=%d and subject_id=%d and group_id=%d",depart_id,subject_id,group_id));
        dataset.open();
        Values values= dataset.getValues(0);
        dlg.setDataset(DataModule.getDataset("subject_group"));
        dlg.setValues(values);
        return dlg.showModal(owner)==BaseDialog.RESULT_OK;
    }

    protected static void updateGroupSequence(Values values) throws Exception{
        String sql = 
                "update subject_group set week_id=%group_id\n" +
                "where subject_id=%subject_id and depart_id in (\n" +
                "  select a.id from depart a inner join curriculum_detail b on a.skill_id=b.skill_id \n" +
                "  and a.curriculum_id=b.curriculum_id\n" +
                "  where b.curriculum_id=%curriculum_id and b.skill_id=%skill_id\n" +
                ");";
        if (values.getInteger("group_sequence_id")==1){
            DataModule.execute(
                     sql.replace("%subject_id",values.getString("subject_id"))
                    .replace("%skill_id", values.getString("skill_id"))
                    .replace("%curriculum_id", values.getString("curriculum_id"))
                    .replace("%group_id", "group_id"));
        }  else {
            DataModule.execute(
                    sql.replace("%subject_id",values.getString("subject_id"))
                    .replace("%skill_id", values.getString("skill_id"))
                    .replace("%curriculum_id", values.getString("curriculum_id"))
                    .replace("%group_id", "0"));
        }
    }
    
    public static boolean editCurriculumDetail(JComponent owner, Integer curriculum_id, Integer skill_id, Integer subject_id) throws Exception{
        EntryDialog dlg = new EntryDialog() {

            @Override
            public void doOnEntry() throws Exception {
                try{
                    Dataset dataset = DataModule.getDataset("curriculum_detail");
                    Values values = getValues();
                    Map<String,Object> filter = new HashMap<>();
                    filter.put("curriculum_id", values.getInteger("curriculum_id"));
                    filter.put("skill_id", values.getInteger("skill_id"));
                    filter.put("subject_id", values.getInteger("subject_id"));
                    dataset.open(filter);
                    dataset.edit(0, values);
                    
                    updateGroupSequence(values);
                    
                    DataModule.commit();
                } catch (Exception e){
                    DataModule.rollback();
                    throw new Exception("EDIT_CURRICULUM_ERROR\n"+e.getMessage());
                }
                
            }
        };
        Values values = new Values();
        values.put("curriculum_id",curriculum_id);
        values.put("skill_id",skill_id);
        values.put("subject_id",subject_id);
        
        
        Dataset dataset = DataModule.getDataset("curriculum_detail");
        dataset.open(values);
        values = dataset.getValues(0);
        dlg.setDataset(dataset);
        dlg.setValues(values);
        return dlg.showModal(owner)==EntryDialog.RESULT_OK;
    }

}
