package ru.viljinsky.dialogs;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import ru.viljinsky.sqlite.IDataset;

/**
 *
 * @author вадик
 * 
 * 
 */
public abstract class SelectDialog extends BaseDialog {
    public static final String COLUMN_NOT_FOUND = "COLUMN_NOT_FOUND\n";
    public static final String SELECT_ALL = "SELECT_ALL";
    public static final String DESELECT_ALL = "DESELECT_ALL";
    
    JTable table;
    Model model;
    
    Set<Object> selected;
    Set<Object> oldSelected;

    class Model extends AbstractTableModel {

        IDataset dataset;
        String keyField;
        String columnName;

        public Model(IDataset dataset) {
            this.dataset = dataset;
        }

        @Override
        public int getRowCount() {
            return dataset.getRowCount();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Map<String, Object> values;
            switch (columnIndex) {
                case 0:
                    values = dataset.getValues(rowIndex);
                    return selected.contains(values.get(keyField));
                case 1:
                    values = dataset.getValues(rowIndex);
                    return values.get(columnName);
                default:
                    return null;
            }
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            Map<String, Object> values = dataset.getValues(rowIndex);
            Object keyValue = values.get(keyField);
            if (selected.contains(keyValue)) 
                selected.remove(keyValue);
             else 
                selected.add(keyValue);
            
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 0;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return Boolean.class;
                case 1:
                    return String.class;
                default:
                    return Object.class;
            }
        }

        @Override
        public String getColumnName(int column) {
            if (column > 0) 
                return columnName;
             else 
                return "";
        }
    }

    public SelectDialog() {
        super();
        selected = new HashSet<>();
        table = new JTable();
//        table.setBackground(Color.CYAN);//getBackground());
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(table));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JPanel bottons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btn;
        btn = new JButton(SELECT_ALL);
        btn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                selectAll();
            }
        });
        bottons.add(btn);
        btn = new JButton(DESELECT_ALL);
        btn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                unSelectedAll();
            }
        });
        bottons.add(btn);
        panel.add(bottons, BorderLayout.PAGE_END);
        add(panel);
        panel.setPreferredSize(new Dimension(400,300));
    }

    public void setDataset(IDataset dataset, String keyField, String columnName) throws Exception{
        if (!dataset.isActive()){
            dataset.open();
        }
        if (dataset.getColumn(keyField)==null)
            throw new Exception(COLUMN_NOT_FOUND+"\""+keyField+"\"");
        if (dataset.getColumn(columnName)==null)
            throw new Exception(COLUMN_NOT_FOUND+"\""+columnName+"\"");
        
        model = new Model(dataset);
        model.columnName = columnName;
        model.keyField = keyField;
        table.setModel(model);
        table.getColumnModel().getColumn(0).setMaxWidth(20);
        table.setShowGrid(false);
    }

    public void setSelected(Set<Object> values) {
        selected = new HashSet<>(values);
        oldSelected = new HashSet<>(values);
    }

    public Set<Object> getSelected() {
        return selected;
    }

    public Set<Object> getAdded() {
        Set<Object> result = new HashSet<>();
        for (Object a : selected) 
            if (!oldSelected.contains(a)) 
                result.add(a);
        return result;
    }

    public Set<Object> getRemoved() {
        Set<Object> result = new HashSet<>();
        for (Object a : oldSelected) 
            if (!selected.contains(a)) 
                result.add(a);
        return result;
    }
    
    protected void selectAll(){
        Map<String,Object> values;
        for (int i=0;i<model.dataset.getRowCount();i++){
            values=model.dataset.getValues(i);
            selected.add(values.get(model.keyField));
        }
        model.fireTableDataChanged();
    }
    
    protected void unSelectedAll(){
        selected.clear();
        model.fireTableDataChanged();
    }
    
}
