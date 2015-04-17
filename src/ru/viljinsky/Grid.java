/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky;

import ru.viljinsky.dialogs.BaseDialog;
import ru.viljinsky.dialogs.EntryDialog;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author вадик
 */

interface IGridConstants{
    public static String GRID_APPEND = "ADD";
    public static String GRID_EDIT = "EDIT";
    public static String GRID_DELETE = "DELETE";
    public static String GRID_REFRESH = "REFRESH";
    public static String GRID_REQUERY = "REQUERY";
}

class GridCommand implements ICommand,IGridConstants{
    public Action[] actions = {
        new Act(GRID_APPEND),
        new Act(GRID_EDIT),
        new Act(GRID_DELETE),
        new Act(GRID_REFRESH),
        new Act(GRID_REQUERY)
    };
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
        try{
        switch(command){            
            case GRID_APPEND:
                grid.append();
                break;
            case GRID_EDIT:
                grid.edit();
                break;
            case GRID_DELETE:
                grid.delete();
                break;
            case GRID_REFRESH:
                grid.refresh();
                break;
            case GRID_REQUERY:
                grid.requery();
        }
        } catch (Exception e){
            JOptionPane.showMessageDialog(grid, e.getMessage());
        }
        updateActionList();
    }

    @Override
    public void updateAction(Action a) {
        String command = (String)a.getValue(Action.ACTION_COMMAND_KEY);
        switch (command){
            case GRID_APPEND:case GRID_EDIT: case GRID_DELETE:
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
    
    public Values getValues(){
        if (getSelectedRow()>=0){
            return model.dataset.getValues(getSelectedRow());
        }
        return null;
    }
    
    public boolean isEditable(){
        return model!=null && model.dataset.isEditable();
    }
    
    abstract class EdtDialog extends EntryDialog{
        
        public EdtDialog(IDataset dataset,Map<String,Object> values){
            super();
            entryPanel.setDataset(dataset);
            if (values!=null)
                entryPanel.setValues(values);
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
            if (values==null)
                return null;
            Column column = dataset.getColumn(columnIndex);
            Object value = values.get(column.columnName);

            switch (column.columnTypeName){
                case "BLOB":
                    return (value==null?null:"TEXT");
                case "BINARY":
                    return (value==null?null:"IMAGE");
                default:
                    return value;

            }
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
            return dataset.getColumn(column).columnName;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            Column column = dataset.getColumn(columnIndex);
            //  !!!!????
            if (column.columnTypeName.equals("BOOLEAN"))
                return String.class;
            return column.getColumnClass();
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
                try{
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
                }} catch (Exception ee){
                    JOptionPane.showMessageDialog(Grid.this, ee.getMessage());
                }
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount()==2){
                    doublClick();
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

    public void doublClick(){
        edit();
    }

    
    public void setDataset(Dataset dataset) throws Exception{
        if (dataset==null){
            model = null;
            
            setModel(new DefaultTableModel());
            return;
        }
//        if (!dataset.isActive()){
//            dataset.open();
//        }
        model = new GridModel(dataset);
        setModel(model);
    }

    public IDataset getDataset(){
        if (model!=null)
            return model.dataset;
        else 
            return null;
    }
            
    public void append(Map<String,Object> values){
        
        BaseDialog dlg = new EdtDialog(model.dataset,values) {

            @Override
            public void doOnEntry() throws Exception {
                IDataset dataset = model.dataset;
                int row = dataset.appned(entryPanel.getValues());
                model.fireTableDataChanged();
                getSelectionModel().setSelectionInterval(row, row);
                scrollRectToVisible(getCellRect(row, getSelectedColumn(), true));
                getSelectionModel().setSelectionInterval(row, row);
                scrollRectToVisible(getCellRect(row, getSelectedColumn(), true));
            }
        };
        dlg.showModal(owner);
        
    }
    
    public void append() {
        append(null);
    }

    public void edit() {
        int row = getSelectedRow();
        if (row >= 0) {
            Values values = model.dataset.getValues(row);
            
            BaseDialog dlg = new EdtDialog(model.dataset, values) {

                @Override
                public void doOnEntry() throws Exception {
                    int row = getSelectedRow();
                    model.dataset.edit(row, entryPanel.getValues());
                }
                
            };
            dlg.setTitle(model.dataset.getTableName());
            if (dlg.showModal(owner)==BaseDialog.RESULT_OK){
                updateUI();
            };
        }
    }
    
    public void delete() throws Exception {
        IDataset dataset = model.dataset;
        int row = getSelectedRow();
        if (row >= 0 && JOptionPane.showConfirmDialog(owner, "Удалить","Внимание",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
            dataset.delete(row);
            model.fireTableRowsDeleted(row, row);
            if (row>=dataset.getRowCount()-1)
                row=dataset.getRowCount()-1;
            if (row>=0){
                getSelectionModel().setSelectionInterval(row, row);
            }
        }
    }

    public void requery() throws Exception{
        model.dataset.open();
        model.fireTableDataChanged();
    }

    public void refresh() {
        model.fireTableDataChanged();
    }

    public void gridSelectionChange() {
//        System.out.println("gridSelectionChange" + getSelectedRow());
//        int row = getSelectedRow();
//        if (row >= 0) {
//            Values map = model.dataset.getValues(row);
//            System.out.println(map);
//        }
    }
    
    public void setFilter(Map<String,Object> filter) throws Exception{
        model.dataset.setFilter(filter);
        model.dataset.open();
        model.fireTableDataChanged();
    }
    /**
     * Поиск первой строки соответвующей фильтру
     * @param filter
     * @return
     * @throws Exception 
     */
    public boolean locate(Map<String,Object> filter) throws Exception{
        Map<String,Object> values;
        Object v1,v2;
        boolean b;
        for (int i=0;i<model.dataset.getRowCount();i++){
            values = model.dataset.getValues(i);
            b=true;
            for (String columName:filter.keySet()){
                v1 = values.get(columName);
                v2 = filter.get(columName);
                b = v1.equals(v2);
                if (!b) break;
            }
            if (b){
                getSelectionModel().setSelectionInterval(i, i);
                scrollRectToVisible(getCellRect(i, getSelectedRowCount(), true));
                return true;
            }
        }
        return false;
    }
    
    ///////////////////////////// Получение значение грида //////////////////
    /**
     * Получение значений из выделенной строки
     * @return 
     * @throws Exception  если нет выделенной строки
     */
    private Values getSelectedValues() throws Exception{
        int row = getSelectedRow();
        if (row<0)
            throw new Exception("TABLE_HAS_NOT_SELECTED");
        Values map = model.dataset.getValues(row);
        return map;
    }
    
    public Integer getIntegerValue(String columnName) throws Exception{
        Values values = getSelectedValues();
        return values.getInteger(columnName);
    }
    
    public String getStringValue(String columnName) throws Exception{
        Values values = getSelectedValues();
        return values.getString(columnName);
    }
    
    public Object getObjectValue(String columnName) throws Exception{
        Values values = getSelectedValues();
        return values.getObject(columnName);
    }
    
}
