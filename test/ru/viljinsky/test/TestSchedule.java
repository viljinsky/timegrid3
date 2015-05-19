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
import ru.viljinsky.forms.CommandListener;
import ru.viljinsky.forms.CommandMngr;
import ru.viljinsky.sqlite.DataModule;
import ru.viljinsky.sqlite.Dataset;
import ru.viljinsky.sqlite.Grid;
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


public class TestSchedule extends JFrame implements ITestSchedule,CommandListener{
    Grid grid;
    DataModule dataModule = DataModule.getInstance();
    CommandMngr commands;
   
    
    public TestSchedule() {
        commands = new CommandMngr();
        commands.setCommands(new String[]{CREATE_STREAM,EDIT_STREAM,REMOVE_STREAM});
        commands.addCommandListener(this);
        grid = new Grid();
        grid.setAutoCreateRowSorter(true);
        grid.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                commands.updateActionList();
            }
        });
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (Action a:commands.getActions()){
            panel.add(new JButton(a));
        }
        Container content = getContentPane();
        content.add(new JScrollPane(grid));
        content.add(panel,BorderLayout.PAGE_START);
        commands.updateActionList();
        
    }
    
    public void open() throws Exception{

        String sql = "select a.depart_id,b.subject_name,a.group_label,a.placed,a.unplaced,a.default_teacher_id,a.default_room_id,\n"
                + "a.subject_id,a.group_id,a.group_type_id,a.stream_id from v_subject_group_on_schedule a inner join subject b on a.subject_id=b.id" ;
        
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
    
}
