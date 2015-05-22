/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.sqlite;

import ru.viljinsky.dialogs.BaseDialog;
import ru.viljinsky.dialogs.EntryDialog;
import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import ru.viljinsky.forms.CommandListener;
import ru.viljinsky.forms.CommandMngr;
import ru.viljinsky.forms.IAppCommand;

/**
 *
 * @author вадик
 */


public class Grid extends JTable implements CommandListener,IAppCommand {
    
    
    protected Component owner = null;
    protected GridModel model;
    CommandMngr commands = new CommandMngr();
    Boolean realNames = false;
    List<Action> extAction = new ArrayList<>();

    public void setRealNames(Boolean realNames) {
        this.realNames = realNames;
    }
    
    public Values getValues(){
        int row = getSelectedRow();
        if (row>=0){
            return model.dataset.getValues(convertRowIndexToModel(row));
        }
        return null;
    }
    
    public void addExtAction(Action action){
        extAction.add(action);
    }
    
    public void setAction(String actionName,Action action){
        commands.setAction(actionName,action);
    }
    
    public void setValues(Values v) throws Exception{
        int row = getSelectedRow();
        if (row>=0){
            model.dataset.setVlaues(convertRowIndexToModel(row),v);
            model.fireTableDataChanged();
            getSelectionModel().setSelectionInterval(row, row);
        } else
            throw new Exception ("GRID_HAS_NOT_SELECTED_ROW");
    }
    
    public boolean isEditable(){
        return model!=null && model.dataset.isEditable();
    }

    public void close() throws Exception{
        if (model!=null){
            model.dataset.close();
            model.fireTableDataChanged();
            
        }
    }

    @Override
    public void doCommand(String command) {
        try{
        switch(command){            
            case GRID_APPEND:
                append();
                break;
            case GRID_EDIT:
                edit();
                break;
            case GRID_DELETE:
                delete();
                break;
            case GRID_REFRESH:
                refresh();
                break;
            case GRID_REQUERY:
                requery();
        }
        } catch (Exception e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
        
    }

    @Override
    public void updateAction(Action action) {
        String command = (String)action.getValue(Action.ACTION_COMMAND_KEY);
        switch (command){
            case GRID_APPEND:case GRID_EDIT: case GRID_DELETE:
                action.setEnabled(isEditable());
                break;
        }
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
        Dataset dataset;
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
        commands.setCommands(new String[]{GRID_APPEND,GRID_EDIT,GRID_DELETE,GRID_REFRESH,GRID_REQUERY});
        commands.addCommandListener(this);
        commands.updateActionList();
        
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
//                        edit();
                        doCommand(GRID_EDIT);
                        break;
                    case KeyEvent.VK_INSERT:
                        doCommand(GRID_APPEND);
//                        append();
                        break;
                    case KeyEvent.VK_DELETE:
                        doCommand(GRID_DELETE);
//                        delete();
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
                    JPopupMenu popupMenu = getPopupMenu();
                    popupMenu.show(Grid.this, e.getX(), e.getY());
                }
            }
        });
    }
    
    public JPopupMenu getPopupMenu(){
        JPopupMenu result = new JPopupMenu();
        if (extAction.size()>0){
            for (Action a:extAction){
                result.add(a);
            }
            result.addSeparator();
        }
        for (Action a:commands.getActions()){
            result.add(a);
        }
        return result;
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
        
        model = new GridModel(dataset);
        setModel(model);
        TableColumnModel cmodel = getColumnModel();
        TableColumn tcolumn;
        Column column;
        String[] params; 
        for (int i=0;i<cmodel.getColumnCount();i++){
            tcolumn = cmodel.getColumn(i);
            column = dataset.getColumn(i);
            tcolumn.setIdentifier(dataset.getColumn(i));
            if (!realNames){
                params = ColumnMap.getParams(new String(column.tableName+"."+column.columnName));
                if (params!=null){
                    tcolumn.setHeaderValue(params[0].isEmpty()?column.columnName:params[0]);
                    if (params.length>1 && params[1].equals("false")){
                        tcolumn.setMinWidth(0);
                        tcolumn.setMinWidth(0);
                        tcolumn.setPreferredWidth(0);
                    }
                }
            }
        }
    }

    public Dataset getDataset(){
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
        Dataset dataset = model.dataset;
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
        int row = getSelectedRow();
        model.dataset.open();
        model.fireTableDataChanged();
        if (row>=0){
            getSelectionModel().setSelectionInterval(row, row);
        }
    }
    
    public void requery(Values values) throws Exception{
        model.dataset.open();
        model.fireTableDataChanged();
        Integer row = model.dataset.locate(values);
        if (row>=0){
            getSelectionModel().setSelectionInterval(row, row);
            scrollRectToVisible(getCellRect(row, getSelectedColumn(), true));
        }
        
    }

    public void refresh() {
        model.fireTableDataChanged();
    }
    
    public void removeSelectedRow(){

        try{
            int[] rows  = getSelectedRows();
            Set<Object[]> values = new HashSet<>();
            for (int r: rows){
                values.add(model.dataset.get(convertRowIndexToModel(r)));
            }

            for (Object[] o:values){
                model.dataset.remove(o);
            }
            model.fireTableDataChanged();
        } catch(Exception e){
            e.printStackTrace();
            
        }
        
//        int row = getSelectedRow();
//        if (row>=0){
//            model.dataset.remove(convertRowIndexToModel(row));
//            model.fireTableDataChanged();
//            if (row>getRowCount()-1){
//                row = getRowCount()-1;
//            }
//            if (row>=0){
//                getSelectionModel().setSelectionInterval(row, row);
//            }
//        }
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
