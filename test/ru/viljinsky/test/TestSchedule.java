/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.test;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import ru.viljinsky.CommandMngr;
import ru.viljinsky.DataModule;
import ru.viljinsky.Dataset;
import ru.viljinsky.Grid;
import ru.viljinsky.forms.Dialogs;

/**
 *
 * @author вадик
 */



interface ITestSchedule{
    public static final String CREATE_STREAM = "CREATE_STREAM";
    public static final String EDIT_STREAM = "EDIT_STREAM";
    public static final String REMOVE_STREAM = "REMOVE_STREAM";
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
        Integer stream_id,depart_id,subject_id,group_id;
        try{
            switch(command){
                case CREATE_STREAM:
                    depart_id=grid.getIntegerValue("depart_id");
                    subject_id=grid.getIntegerValue("subject_id");
                    group_id=grid.getIntegerValue("group_id");
                    if (Dialogs.createStream(rootPane, depart_id, subject_id, group_id))
                            grid.requery();
                    break;
                case EDIT_STREAM:
                    stream_id = grid.getIntegerValue("stream_id");
                    if (Dialogs.editStream(rootPane, stream_id))
                            grid.requery();
                    break;
                case REMOVE_STREAM:
                    depart_id=grid.getIntegerValue("depart_id");
                    subject_id=grid.getIntegerValue("subject_id");
                    if (Dialogs.removeStream(rootPane, depart_id,subject_id))
                        grid.requery();
                    break;
            }
            
        } catch(Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(rootPane, e.getMessage());
        }
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
