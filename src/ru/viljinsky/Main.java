/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author вадик
 */
class GridModel extends AbstractTableModel{
    IDataset dataset;
    public GridModel(Dataset dataset){
        this.dataset=dataset;
    }
    
    @Override
    public int getRowCount() {
        return dataset.getRowCount();
    }

    @Override
    public int getColumnCount() {
        return dataset.getColumnCount();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
//        Object[] rowset = dataset.get(rowIndex);
        Map<String,Object> values = dataset.getValues(rowIndex);
        return values.get(dataset.getColumn(columnIndex).columnName);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Map<String,Object> values = dataset.getValues(rowIndex);
        values.put(dataset.getColumn(columnIndex).columnName, aValue);
        try{
        dataset.setVlaues(rowIndex, values);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public String getColumnName(int column) {
        return dataset.getColumn(column).columnName;// getColumnName(column);
    }
    
    
}

//------------------------------------------------------------------------------
interface ICommand{
    public void doCommand(String command);
    public void updateAction(Action a);
    public void updateActionList();
    public JPopupMenu getPopup();
    public void addMenu(JMenu menu);
}

class GridCommand implements ICommand{
    Action[] actions = {new Act("add"),new Act("edit"),new Act("delete"),new Act("refresh")};
    Grid grid = null;
    
    class Act extends AbstractAction{

        public Act(String name) {
            super(name);
            putValue(ACTION_COMMAND_KEY, name);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            doCommand(e.getActionCommand());
        }
    }
    
    
    public GridCommand(Grid grid){
        this.grid=grid;
    }

    @Override
    public void doCommand(String command) {
        System.out.println(command);
        switch(command){
            case "add":
                grid.append();
                break;
            case "edit":
                grid.edit();
                break;
            case "delete":
                grid.delete();
                break;
            case "refresh":
                grid.refresh();
                break;
        }
        updateActionList();
    }

    @Override
    public void updateAction(Action a) {
        String command = (String)a.getValue(Action.ACTION_COMMAND_KEY);
        System.out.println(command);
    }

    @Override
    public void updateActionList() {
        for (Action a:actions){
            updateAction(a);
        }
    }

    @Override
    public JPopupMenu getPopup() {
        JPopupMenu result = new JPopupMenu();
        for (Action a:actions)
            if (a==null)
                result.addSeparator();
            else
                result.add(a);
        return result;
    }

    @Override
    public void addMenu(JMenu menu) {
        for (Action a:actions)
            if (a==null)
                menu.addSeparator();
            else
                menu.add(a);
    }
}
//------------------------------------------------------------------------------


public class Main extends JFrame{
    JTabbedPane tabs = new JTabbedPane();
    
    DataModule dataModule = DataModule.getInstance();
    public Main(){
        Container content = getContentPane();
        content.setPreferredSize(new Dimension(800,600));
        content.setLayout(new BorderLayout());
        content.add(tabs);
        
    }
    public void open(){
        Grid grid;
        Dataset dataset ;
        try{
            dataModule.open();
            for (DatasetInfo info :dataModule.infoList){
                dataset = dataModule.getTable(info.tableName);
                dataset.open();
                grid = new Grid();
                grid.owner=Main.this;
                grid.setDataset(dataset);
                tabs.addTab(dataset.getTableName(), new JScrollPane(grid));
                
            }

        } catch (Exception e){
            JOptionPane.showMessageDialog(rootPane, e.getMessage());
        }
    }
    
    public static void main(String[] args){
        Main frame = new Main();
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.open();
    }
}
