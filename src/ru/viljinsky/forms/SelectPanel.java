/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import ru.viljinsky.sqlite.Grid;

/**
 *
 * @author вадик
 */
abstract class SelectPanel extends JPanel {
    protected static final String INCLUDE = "INCLUDE";
    protected static final String EXCLUDE = "EXCLUDE";
    protected static final String INCLUDE_ALL = "INCLUDE_ALL";
    protected static final String EXCLUDE_ALL = "EXCLUDE_ALL";
    
    Grid sourceGrid;
    Grid destanationGrid;
    
    class MyGrid extends Grid{

        @Override
        public void gridSelectionChange() {
            SelectPanel.this.updateActionList();
        }

    }
    
    CommandMngr commands = new CommandMngr() {

        @Override
        public void updateAction(Action a) {
            String command = (String)a.getValue(Action.ACTION_COMMAND_KEY);
            switch (command){
                case INCLUDE:
                    a.setEnabled(sourceGrid.getSelectedRow()>=0);
                    break;
                case EXCLUDE:
                    a.setEnabled(destanationGrid.getSelectedRow()>=0);
                    break;
                case INCLUDE_ALL:
                    a.setEnabled(sourceGrid.getRowCount()>0);
                    break;
                case EXCLUDE_ALL:
                    a.setEnabled(destanationGrid.getRowCount()>0);
                    break;
            }
        }

        @Override
        public void doCommand(String command) {
            SelectPanel.this.doCommand(command);
        }
    };
    
    protected JCheckBox chProfileOnly;

    public SelectPanel() {
        setPreferredSize(new Dimension(500, 200));
        setLayout(new BorderLayout());
        sourceGrid = new MyGrid();
        
        sourceGrid.setAutoCreateRowSorter(true);
        destanationGrid = new MyGrid();
        
        destanationGrid.setAutoCreateRowSorter(true);
        chProfileOnly = new JCheckBox("Только по профилю",null,true);
        chProfileOnly.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    requery();
                } catch (Exception ee){
                    ee.printStackTrace();
                }
            }
        });
        
        String[] buttons = {INCLUDE,EXCLUDE,INCLUDE_ALL,EXCLUDE_ALL};
        commands.setCommandList(buttons);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        Box box;
        box = Box.createVerticalBox();
        box.add(new JScrollPane(sourceGrid));
        panel.add(box);
        add(Box.createHorizontalStrut(6));
        box = Box.createVerticalBox();
        Box box1;
        JButton btn;
        for (String sButton:buttons){
            btn = new JButton(commands.getAction(sButton));
            box1 = Box.createVerticalBox();
            box1.setAlignmentX(CENTER_ALIGNMENT);
            box1.add(btn);
            box.add(box1);
            box.add(Box.createVerticalStrut(12));
        }
            
        panel.add(box);
        panel.add(Box.createHorizontalStrut(6));
        box = Box.createHorizontalBox();
        box.add(new JScrollPane(destanationGrid));
        panel.add(box);
        panel.setBorder(new EmptyBorder(12,6,12,6));
        add(panel);
        add(chProfileOnly,BorderLayout.PAGE_END);
        updateActionList();
    }

    public void updateActionList(){
        commands.updateActionList();
    }
    
    public void doCommand(String command) {
        try {
            switch (command) {
                case INCLUDE:
                    include();
                    break;
                case EXCLUDE:
                    exclude();
                    break;
                case INCLUDE_ALL:
                    includeAll();
                    break;
                case EXCLUDE_ALL:
                    excludeAll();
                    break;
                default:
                    System.err.println(command);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    public void close() throws Exception{
        sourceGrid.setDataset(null);
        destanationGrid.setDataset(null);
    }
    
    public abstract void include() throws Exception;

    public abstract void exclude() throws Exception;

    public abstract void includeAll() throws Exception;

    public abstract void excludeAll() throws Exception;

    public abstract void requery() throws Exception;
    
}
