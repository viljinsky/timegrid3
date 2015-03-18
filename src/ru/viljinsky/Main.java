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
        return true;
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

class Grid extends JTable{
    Component owner = null;
    GridModel model;
    ICommand commands = null;
    
    public Grid(){
        super();
        commands = new GridCommand(this);
        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()){
                    gridSelectionChange();
                }
            }
        });
        
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                    showPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showPopup(e);
            }
            
            
            
            public void showPopup(MouseEvent e){
                if (e.isPopupTrigger() && commands!=null){
                    JPopupMenu popupMenu = commands.getPopup();
                    popupMenu.show(Grid.this, e.getX(),e.getY());
                }
            }
            
            
        });
    }
    
    
    public void setDataset(Dataset dataset){
        model = new GridModel(dataset);
        setModel(model);
    }
    
    abstract class AppendDialog extends DataEntryDialog{

        public AppendDialog(IDataset datset) {
            super();
            panel.setDataset(datset);
        }

        @Override
        public void doOnEntry() throws Exception {
            onAddValues();
        }
        
        public abstract void onAddValues() throws Exception;
        
        
    }
    
    public void append(){
        
        BaseDialog dlg = new AppendDialog(model.dataset) {
            
            @Override
            public void onAddValues() throws Exception{
                IDataset dataset = model.dataset;
                int row = dataset.appned(panel.getValues());
                model.fireTableDataChanged();
                getSelectionModel().setSelectionInterval(row, row);
                scrollRectToVisible(getCellRect(row, getSelectedColumn(), true));
            }
        };
        
        dlg.showModal(owner);
        
    }
    
    public void delete(){
        IDataset dataset = model.dataset;
        int row = getSelectedRow();
        if (row>=0){
            dataset.delete(row);
            model.fireTableRowsDeleted(row, row);
        }
    }
    
    class EditDialog extends DataEntryDialog{

        public EditDialog(IDataset dataset,Map<String,Object> values) {
            super();
            panel.setDataset(dataset);
            panel.setValues(values);
        }

        
        @Override
        public void doOnEntry() throws Exception {
            try{
                System.out.println(panel.getValues());
            } catch(Exception e){
            }
        }
    }
    
    public void edit(){
        
        int row = getSelectedRow();
        if (row>=0){
            Map<String,Object> values = model.dataset.getValues(row);
            EditDialog dlg = new EditDialog(model.dataset,values);
            if (dlg.showModal(owner)==BaseDialog.RESULT_OK){
                JOptionPane.showMessageDialog(null, "OK");
            }
            
        }
        
    }
    
    public void refresh(){
    }
    
    
    public void gridSelectionChange(){
        System.out.println("gridSelectionChange"+getSelectedRow());
        int row = getSelectedRow();
        if (row>=0){
            Map<String,Object> map = model.dataset.getValues(row);
            System.out.println(map);
        }
    }
}

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
