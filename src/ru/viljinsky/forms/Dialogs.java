/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import ru.viljinsky.DataModule;
import ru.viljinsky.Dataset;
import ru.viljinsky.EntryPanel;
import ru.viljinsky.IDataset;
import ru.viljinsky.KeyMap;
import ru.viljinsky.Recordset;
import ru.viljinsky.SelectDialog;

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
    }
    
    
    
}

abstract class AbstractProfileDialog extends SelectDialog{
    Integer profile_id;
    EntryPanel entryPanel = new EntryPanel();
    
    public AbstractProfileDialog(Integer profile_id) {
        super();
        this.profile_id=profile_id;
        add(entryPanel,BorderLayout.PAGE_START);
    }
    
}

abstract class AbstractStreamDialog extends SelectDialog{
    JTextField field = new JTextField(20);
    Integer stream_id;

    public AbstractStreamDialog() {
        super();
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Stream");
        field.setText("Новый поток");
        panel.add(label);
        panel.add(field);
        add(panel,BorderLayout.PAGE_START);
    }

    public void setStreamCaption(String caption){
        field.setText(caption);
    }

    public String getStreamCaption(){
        return field.getText();
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
            
    private static DataModule dataModule = DataModule.getInstance();
    
    public static Integer createProfile(JComponent owner,Integer profile_id) throws Exception{
        AbstractProfileDialog dlg = new AbstractProfileDialog(profile_id) {
            
            @Override
            public void doOnEntry() throws Exception {
                
                String profile_name=(String)entryPanel.getValues().get("profile_name");
                dataModule.execute("update profile set profile_name='"+profile_name+"' where id="+profile_id);
                KeyMap map = new KeyMap();
                map.put(1, profile_id);
                for (Object obj:getSelected()){
                    map.put(2, obj);
                    dataModule.execute("insert into profile_item (profile_id,subject_id) values (?,?);",map);
                }
            }
        };
        
        dataModule.execute("insert into profile (profile_type_id,profile_name) select profile_type_id,'*new profile*' from profile where id="+profile_id);
        Dataset dataset;
        dataset = dataModule.getSQLDataset("select * from profile where id=(select max(id) from profile)");
        dataset.open();
        Map<String,Object> map = dataset.getValues(0);
        
        dlg.profile_id=(Integer)map.get("id");
        dlg.entryPanel.setDataset(dataset);
        dlg.entryPanel.setValues(map);
        dataset = dataModule.getDataset("subject");
        dlg.setDataset(dataset, "id", "subject_name");
        
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
                    String profile_name=(String)entryPanel.getValues().get("profile_name");
                    dataModule.execute("update profile set profile_name='"+profile_name+"' where id="+profile_id);
                    for (Object k:getRemoved()){
                        DataTask.excludeSubjectFromProfile(profile_id,(Integer)k);
                    }
                    for (Object k:getAdded()){
                        DataTask.includeSubjectToProfile(profile_id,(Integer)k);
                    }
                    dataModule.commit();
                } catch (Exception e){
                    dataModule.rollback();
                    throw new Exception(INCLUDE_EXCLUDE_PROFILE_ERROR+e.getMessage());
                }
            }
        };
        
        IDataset dataset = dataModule.getSQLDataset("select * from profile where id="+profile_id);
        dataset.open();
        dlg.entryPanel.setDataset(dataset);
        if (!dataset.isEmpty()){
            Map<String,Object> map;
            map = dataset.getValues(0);
            dlg.entryPanel.setValues(map);
        }


        Set<Object> set = new HashSet<>();
        Recordset rs = dataModule.getRecordet("select subject_id from profile_item where profile_id="+profile_id);
        for (int i=0;i<rs.size();i++){
            set.add(rs.get(i)[0]);
        }
        dataset = dataModule.getDataset("subject");
        dlg.setDataset(dataset, "id", "subject_name");
        dlg.setSelected(set);
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
                    String profile_name=(String)entryPanel.getValues().get("profile_name");
                    dataModule.execute("update profile set profile_name='"+profile_name+"' where id="+shift_id);
                    DataTask.editShift(shift_id, getAdded(),getRemoved());
                    DataModule.commit();
                } catch (Exception e){
                    DataModule.rollback();
                    throw new Exception("ON_ENTRY_ERROR\n"+e.getMessage());
                }
            }
            
        };


        dataModule.execute("insert into shift(shift_type_id,shift_name) select shift_type_id,'new shift name' from shift where id="+shift_id);
        Dataset dataset = dataModule.getSQLDataset("select * from shift where id=(select max(id) from shift)");
        dataset.open();
        Map<String,Object> map = dataset.getValues(0);
        dlg.entryPanel.setDataset(dataset);
        dlg.entryPanel.setValues(map);
        dlg.shift_id = (Integer)map.get("id");
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
                    dataModule.execute("update shift set shift_name='"+shift_name+"' where id="+shift_id);
                    DataTask.editShift(shift_id, getAdded(),getRemoved());
                    dataModule.commit();
                } catch(Exception e){
                    dataModule.rollback();
                    throw new Exception(EDIT_SHIFT_ERROR+e.getMessage());
                }
            }

        };
        
        Dataset dataset = dataModule.getSQLDataset("select * from shift where id="+shift_id);
        dataset.open();
        dlg.entryPanel.setDataset(dataset);
        dlg.entryPanel.setValues(dataset.getValues(0));
        
        Recordset rs = dataModule.getRecordet(String.format("select day_id,bell_id from shift_detail where shift_id=%d;",shift_id));
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
    
    private static final String sqlSubjectGroupToStream = "select a.depart_id || '-' || a.subject_id || '-' ||a.group_id as key," + " c.label ||' ' || b.subject_name  || a.subject_id || a.group_id as value \n" + "from subject_group a inner join subject b on a.subject_id=b.id inner join depart c on c.id=a.depart_id order by a.subject_id,a.depart_id ;";

    public static boolean createStream(JComponent owner, Integer depart_id, Integer subject_id, Integer group_id) throws Exception {
        String sql = sqlSubjectGroupToStream;
        AbstractStreamDialog dlg = new AbstractStreamDialog() {
            @Override
            public void doOnEntry() throws Exception {
                Integer depart_id,subject_id,group_id;
                List<Integer[]> list = new ArrayList<>();
                for (Object obj : getSelected()) {
                    String s = (String) obj;
                    String[] ss = s.split("-");
                    depart_id = Integer.valueOf(ss[0]);
                    subject_id = Integer.valueOf(ss[1]);
                    group_id = Integer.valueOf(ss[2]);
                    System.out.println(String.format("depart %d group_id %d subject_id %d ", depart_id, group_id, subject_id));
                    list.add(new Integer[]{depart_id, subject_id, group_id});
                }
                DataTask.createStream("StreanNew", list);
            }
        };
        String s = String.format("%d-%d-%d", depart_id, subject_id, group_id);
        Dataset dataset = dataModule.getSQLDataset(sql);
        dlg.setDataset(dataset, "key", "value");
        Set set = new HashSet();
        set.add(s);
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
                    dataModule.commit();
                } catch (Exception e) {
                    dataModule.rollback();
                    throw new Exception(EDIT_STREAM_ERROR + e.getMessage());
                }
            }
        };
        Recordset rs = dataModule.getRecordet("select depart_id||'-'||subject_id ||'-'||group_id from subject_group where stream_id=" + stream_id);
        Set set = new HashSet();
        for (int i = 0; i < rs.size(); i++) {
            set.add(rs.get(i)[0]);
        }
        Dataset dataset = dataModule.getSQLDataset(sqlSubjectGroupToStream);
        dlg.setDataset(dataset, "key", "value");
        dlg.setSelected(set);
        dlg.stream_id = stream_id;
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
    
}
