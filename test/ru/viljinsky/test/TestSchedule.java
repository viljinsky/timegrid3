/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.test;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import ru.viljinsky.CommandMngr;
import ru.viljinsky.DataModule;
import ru.viljinsky.Dataset;
import ru.viljinsky.Grid;
import ru.viljinsky.Recordset;
import ru.viljinsky.SelectDialog;
import ru.viljinsky.forms.DataTask;

/**
 *
 * @author вадик
 */


abstract class StreamDialog extends SelectDialog{
    JTextField field = new JTextField(20);
    Integer stream_id;
            
    public StreamDialog() {
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

interface ITestSchedule{
    public static final String CREATE_STREAM = "CREATE_STREAM";
    public static final String EDIT_STREAM = "EDIT_STREAM";
    public static final String REMOVE_STREAM = "REMOVE_STREAM";
//    public static final String EXCLUDE_FROM_STREAM = "EXCLUDE_FROM_STREAM";        
}

public class TestSchedule extends JFrame implements ITestSchedule{
    Grid grid;
    DataModule dataModule = DataModule.getInstance();
    CommandMngr commands;
   
    
    public TestSchedule() {
        commands = new CommandMngr() {

            @Override
            public void updateAction(Action a) {
                try{
                boolean b1 = grid.getSelectedRow()>=0;
                boolean b2 = b1 && (grid.getObjectValue("stream_id")!=null);
                
                String command = (String)a.getValue(Action.ACTION_COMMAND_KEY);
                switch (command){
                    case CREATE_STREAM:
                        a.setEnabled(b1 && !b2);
                        break;
                    case EDIT_STREAM:
                        a.setEnabled(b2);
                        break;
                    case REMOVE_STREAM:
                        a.setEnabled(b2);
                        break;
                }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void doCommand(String command) {
                TestSchedule.this.doCommand(command);
            }
        };
        
        commands.setCommandList(new String[]{CREATE_STREAM,EDIT_STREAM,REMOVE_STREAM});
        grid = new Grid();
        grid.setAutoCreateRowSorter(true);
        grid.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                commands.updateActionList();
            }
        });
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (Action a:commands.getActionList()){
            panel.add(new JButton(a));
        }
        Container content = getContentPane();
        content.add(new JScrollPane(grid));
        content.add(panel,BorderLayout.PAGE_START);
        commands.updateActionList();
        
    }
    
    public void open() throws Exception{
        String sql = "select b.subject_name,\n"
//                + "case group_id when 1 then 'M' when 2 then 'Д.' end as label1,\n"
//                + "case group_type_id when 0 then '' when 1 then '' when 2 then 'Гр.'||group_id end as label2,\n"
                + "case when group_type_id=0 then '' \n"
                + "   when group_type_id=1 and group_id=1 then 'M' \n"
                + "   when group_type_id=1 and group_id=2 then 'Д' \n"
                + "   when group_type_id=2 then 'ГР.' || group_id \n"
                + "end as label3,\n"
                + "a.*\n"
                + " from v_subject_group_on_schedule a inner join subject b on a.subject_id=b.id";
        
        
        Dataset dataset = dataModule.getSQLDataset(sql);
        dataset.open();
        grid.setDataset(dataset);
    }
    
    public void doCommand(String command){
        try{
            switch(command){
                case CREATE_STREAM:
                    createStream();
                    break;
                case EDIT_STREAM:
                    editStream();
                    break;
                case REMOVE_STREAM:
                    break;
            }
            
        } catch(Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(rootPane, e.getMessage());
        }
    }
    
    String sqlSubjectGroupToStream = "select a.depart_id || '-' || a.subject_id || '-' ||a.group_id as key,"
                + " c.label ||' ' || b.subject_name  || a.subject_id || a.group_id as value \n"
                + "from subject_group a inner join subject b on a.subject_id=b.id inner join depart c on c.id=a.depart_id order by a.subject_id,a.depart_id ;";
    
    public void createStream() throws Exception{
        String sql = sqlSubjectGroupToStream;
        StreamDialog dlg = new StreamDialog(){

            @Override
            public void doOnEntry() throws Exception {
                Integer depart_id,group_id,subject_id;
                List<Integer[]> list = new ArrayList<>();
                
                for (Object obj:getSelected()){
                    String s=(String)obj;
                    String[] ss = s.split("-");
                    depart_id=Integer.valueOf(ss[0]);
                    subject_id=Integer.valueOf(ss[1]);
                    group_id=Integer.valueOf(ss[2]);
                    System.out.println(String.format("depart %d group_id %d subject_id %d ", depart_id,group_id,subject_id));
                    list.add(new Integer[]{depart_id,subject_id,group_id});
                }
                DataTask.createStream(CREATE_STREAM, list);
            }
        };
        Integer depart_id,group_id,subject_id;
        depart_id = grid.getInegerValue("depart_id");
        subject_id = grid.getInegerValue("subject_id");
        group_id = grid.getInegerValue("group_id");
        
        String s = String.format("%d-%d-%d", depart_id,subject_id,group_id);
        Dataset dataset = dataModule.getSQLDataset(sql);
        dlg.setDataset(dataset, "key", "value");
        Set set = new HashSet();
        set.add(s);
        dlg.setSelected(set);
        dlg.showModal(this.rootPane);
        if (dlg.modalResult==SelectDialog.RESULT_OK)
            grid.requery();
    }
    
    public void editStream() throws Exception{
        StreamDialog dlg = new StreamDialog() {

            @Override
            public void doOnEntry() throws Exception {
                Integer depart_id,subject_id,group_id;
                String objString;
                String[] v;
                if (getSelected().size()<2)
                    throw new Exception("WRONG_STREAM");
                try{
                    for (Object obj:getAdded()){
                        objString = (String)obj;
                        v = objString.split("-");
                        depart_id=Integer.valueOf(v[0]);
                        subject_id=Integer.valueOf(v[1]);
                        group_id=Integer.valueOf(v[2]);
                        
                        System.out.println("ADDED:"+obj);
                        DataTask.includeToStream(stream_id, depart_id, subject_id);
                    }
                    for (Object obj:getRemoved()){
                        System.out.println("REMOVED"+obj);
                        objString = (String)obj;
                        v = objString.split("-");
                        depart_id=Integer.valueOf(v[0]);
                        subject_id=Integer.valueOf(v[1]);
                        group_id=Integer.valueOf(v[2]);
                        DataTask.excludeFromStream(stream_id, depart_id, subject_id);
                       
                    }
                    dataModule.commit();
                } catch (Exception e){
                    dataModule.rollback();
                    throw new Exception("EDIT_STREAM_ERROR\n"+e.getMessage());
                }
            }
        };
        
        Integer stream_id=grid.getInegerValue("stream_id");
        Recordset rs = dataModule.getRecordet("select depart_id||'-'||subject_id ||'-'||group_id from subject_group where stream_id="+stream_id);
        Set set = new HashSet();
        for (int i=0;i<rs.size();i++){
            set.add(rs.get(i)[0]);
        }
        
        Dataset dataset = dataModule.getSQLDataset(sqlSubjectGroupToStream);
        dlg.setDataset(dataset, "key", "value");
        dlg.setSelected(set);
        dlg.stream_id=stream_id;
        dlg.showModal(TestSchedule.this.rootPane);
        if (dlg.modalResult==SelectDialog.RESULT_OK)
            grid.requery();
        
        
    }
    public static void main(String[] args) throws Exception{
        TestSchedule frame = new TestSchedule();
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        
        try{
        DataModule.getInstance().open();
        frame.open();
        } catch (Exception e){
            e.printStackTrace();
        }
                
    }
    
}
