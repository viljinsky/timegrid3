/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import ru.viljinsky.DataModule;
import ru.viljinsky.IDataset;
import ru.viljinsky.Recordset;
import ru.viljinsky.SelectDialog;

/**
 *
 * @author вадик
 */
abstract class AbstractShiftDialog extends ShiftDialog{
    Integer shift_id ;
    JTextField text = new JTextField(20);
    
    public AbstractShiftDialog() {
        super();
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(text);
        getContentPane().add(panel,BorderLayout.PAGE_START);
    }
    
    public String getShiftCaption(){
        return text.getText();
    }
    
    public void setShiftCaption(String shiftCaption){
        text.setText(shiftCaption);
    }
    
}

abstract class AbstractProfileDialog extends SelectDialog{
    Integer profile_id;
    public AbstractProfileDialog(Integer profile_id) {
        super();
        this.profile_id=profile_id;
    }
    
}
        
public class Dialogs {
    private static DataModule dataModule = DataModule.getInstance();
    
    public static boolean editProfile(JComponent owner,Integer profile_id) throws Exception{
        AbstractProfileDialog dlg = new AbstractProfileDialog(profile_id) {

            @Override
            public void doOnEntry() throws Exception {
                try{
                    for (Object k:getRemoved()){
                        DataTask.excludeSubjectFromProfile(profile_id,(Integer)k);
                    }
                    for (Object k:getAdded()){
                        DataTask.includeSubjectToProfile(profile_id,(Integer)k);
                    }
                    dataModule.commit();
                } catch (Exception e){
                    dataModule.rollback();
                    throw new Exception("INCLUDE_EXCLUDE_PROFILE_ERROR\n"+e.getMessage());
                }
            }
        };

        Set<Object> set = new HashSet<>();
        Recordset rs = dataModule.getRecordet("select subject_id from profile_item where profile_id="+profile_id);
        for (int i=0;i<rs.size();i++){
            set.add(rs.get(i)[0]);
        }
        IDataset dataset = dataModule.getDataset("subject");
        dlg.setDataset(dataset, "id", "subject_name");
        dlg.setSelected(set);
        return (dlg.showModal(owner)==SelectDialog.RESULT_OK);
        
    }
    
    public static boolean editShift(JComponent owner,Integer shift_id) throws Exception{
        
        AbstractShiftDialog dlg = new AbstractShiftDialog(){

            @Override
            public void doOnEntry() throws Exception {
                try{
                    DataTask.editShift(shift_id, getAdded(),getRemoved());
                    dataModule.commit();
                } catch(Exception e){
                    dataModule.rollback();
                    throw new Exception("EDIT_ROOM_SIFT_ERROR\n"+e.getMessage());
                }
            }

        };
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
        dlg.shift_id=shift_id;
        dlg.setSelected(list);
        dlg.showModal(owner);
        return (dlg.modalResult==SelectDialog.RESULT_OK);
    }
}
