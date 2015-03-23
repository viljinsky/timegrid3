/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
//import static javax.swing.Action.ACTION_COMMAND_KEY;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author вадик
 */

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
        switch (command){
            case "add":case "edit": case "delete":
                a.setEnabled(grid.isEditable());
                break;
        }
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


public class Grid extends JTable {
    Component owner = null;
    GridModel model;
    ICommand commands = null;
    
    public boolean isEditable(){
        return model!=null && model.dataset.isEditable();
    }
    abstract class AppendDialog extends DataEntryDialog {

        public AppendDialog(IDataset datset) {
            super();
            panel.setDataset(datset);
        }

    }
    
    abstract class EdtDialog extends DataEntryDialog{
        
        public EdtDialog(IDataset dataset,Map<String,Object> values){
            super();
            panel.setDataset(dataset);
            panel.setValues(values);
        }

    }
    
    
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

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            Column column = dataset.getColumn(columnIndex);
            return column.getColumnClass();
//            System.out.println("-->"+column.columnClassName);
                    
//            return super.getColumnClass(columnIndex); //To change body of generated methods, choose Tools | Templates.
        }
    
    
    
    
}

    
    

    public Grid() {
        super();
        commands = new GridCommand(this);
        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    gridSelectionChange();
                }
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        edit();
                        break;
                    case KeyEvent.VK_INSERT:
                        append();
                        break;
                    case KeyEvent.VK_DELETE:
                        delete();
                        break;
                }
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount()==2){
                    edit();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                showPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showPopup(e);
            }

            public void showPopup(MouseEvent e) {
                if (e.isPopupTrigger() && commands != null) {
                    commands.updateActionList();
                    JPopupMenu popupMenu = commands.getPopup();
                    popupMenu.show(Grid.this, e.getX(), e.getY());
                }
            }
        });
    }

    public void setDataset(Dataset dataset) {
        model = new GridModel(dataset);
        setModel(model);
    }

    public IDataset getDataset(){
        return model.dataset;
    }
            
    public void append() {
        BaseDialog dlg = new AppendDialog(model.dataset) {

            @Override
            public void doOnEntry() throws Exception {
                IDataset dataset = model.dataset;
                int row = dataset.appned(panel.getValues());
                model.fireTableDataChanged();
                getSelectionModel().setSelectionInterval(row, row);
                scrollRectToVisible(getCellRect(row, getSelectedColumn(), true));
                getSelectionModel().setSelectionInterval(row, row);
                scrollRectToVisible(getCellRect(row, getSelectedColumn(), true));
            }
        };
        dlg.showModal(owner);
    }

    public void edit() {
        int row = getSelectedRow();
        if (row >= 0) {
            Map<String, Object> values = model.dataset.getValues(row);
            
            BaseDialog dlg = new EdtDialog(model.dataset, values) {

                @Override
                public void doOnEntry() throws Exception {
                    int row = getSelectedRow();
                    model.dataset.edit(row, panel.getValues());
                }
                
            };
            dlg.setTitle(model.dataset.getTableName());
            if (dlg.showModal(owner)==BaseDialog.RESULT_OK){
                updateUI();
            };
        }
    }
    
    public void delete() {
        IDataset dataset = model.dataset;
        int row = getSelectedRow();
        if (row >= 0 && JOptionPane.showConfirmDialog(owner, "Удалить","Внимание",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
            dataset.delete(row);
            model.fireTableRowsDeleted(row, row);
            if (row>=dataset.getRowCount())
                row=dataset.getColumnCount()-1;
            if (row>=0){
                getSelectionModel().setSelectionInterval(row, row);
            }
        }
    }


    public void refresh() {
        model.fireTableDataChanged();
    }

    public void gridSelectionChange() {
        System.out.println("gridSelectionChange" + getSelectedRow());
        int row = getSelectedRow();
        if (row >= 0) {
            Map<String, Object> map = model.dataset.getValues(row);
            System.out.println(map);
        }
    }
    
}
