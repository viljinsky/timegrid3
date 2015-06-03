/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import ru.viljinsky.forms.CommandListener;
import ru.viljinsky.forms.CommandMngr;
import ru.viljinsky.sqlite.DataModule;
import ru.viljinsky.sqlite.Dataset;
import ru.viljinsky.sqlite.Grid;
import ru.viljinsky.sqlite.Values;

/**
 *
 * @author вадик
 */


///////////////////////////////////////////////////////////////////////////////
public class TestShift  extends JFrame implements CommandListener{
    
    public static final String CMD_SAVE = "SAVE";
    public static final String CMD_CANCEL = "CANCEL";
    public static final String CMD_EDIT = "EDIT";
    
    DBShiftPanel shiftPanel = new DBShiftPanel();
    CommandMngr mmngr = new CommandMngr();
    
    Grid grid = new Grid(){

        @Override
        public void gridSelectionChange() {
            Values v = getValues();
            if (v!=null) 
            try{
                shiftPanel.setTeacherId(v.getInteger("teacher_id"));
                mmngr.updateActionList();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    };
    
    public void initComponents(){
        mmngr.setCommands(new String[]{CMD_EDIT,CMD_SAVE,CMD_CANCEL});
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(800,600));
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(shiftPanel);
        splitPane.setRightComponent(new JScrollPane(grid));
        
        JPanel commandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (Action a:mmngr.getActions()){
            commandPanel.add(new JButton(a));
        }
        mmngr.addCommandListener(this);
        mmngr.updateActionList();
                
        
        panel.add(splitPane);
        panel.add(commandPanel,BorderLayout.PAGE_START);
        setContentPane(panel);
        
    }
    
    public void open() throws Exception{
        shiftPanel.open();
        Dataset dataset = DataModule.getSQLDataset(
                "select a.last_name,a.first_name,a.id as teacher_id,a.shift_id,b.* "+
                "from teacher a inner join shift b on a.shift_id=b.id");
        grid.setDataset(dataset);
        dataset.open();
    }
    
    public static void main(String[] args) throws Exception{
        
        DataModule.open();
        
        TestShift frame = new TestShift();
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.initComponents();
        frame.open();
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void doCommand(String command) {
       switch (command){
           case CMD_EDIT:
               shiftPanel.setAllowEdit(true);
               break;
           case CMD_SAVE:
               shiftPanel.setAllowEdit(false);
               System.out.println("ADDED");
               for (Point p:shiftPanel.getAdded()){
                   System.out.println(p);
               }
               System.out.println("REMOVED");
               for (Point p:shiftPanel.getRemoved()){
                   System.out.println(p);
               }
               break;
           case CMD_CANCEL:
               shiftPanel.setPoints(shiftPanel.oldPoints);
               shiftPanel.setAllowEdit(false);
               break;
       }
    }

    @Override
    public void updateAction(Action action) {
        String command = (String)action.getValue(Action.ACTION_COMMAND_KEY);
        switch (command){
           case CMD_EDIT:
               action.setEnabled(!shiftPanel.allowEdit);
               break;
           case CMD_SAVE:
               action.setEnabled(shiftPanel.allowEdit);
               break;
           case CMD_CANCEL:
               action.setEnabled(shiftPanel.allowEdit);
               break;
        }
    }
    
}
