/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.sqlite;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.event.ListDataListener;

/**
 *
 * @author вадик
 */
public class DBComboBox extends JComboBox<String> {
    Model model;
    protected String label = "combobox";

    class Model implements ComboBoxModel {

        Object value;
        Map<Object, String> values;

        public Model(Dataset dataset, String columnName, String lookupColumn) throws Exception {
            values = new HashMap<>();
            Map<String, Object> v;
            if (!dataset.isActive())
                dataset.open();
            values.put(columnName,"");  // NULL value
            for (int i = 0; i < dataset.getRowCount(); i++) {
                v = dataset.getValues(i);
                values.put(v.get(columnName), v.get(lookupColumn).toString());
            }
        }

        @Override
        public void setSelectedItem(Object anItem) {
            if (anItem.equals("")){
                value = null;
            } else 
            for (Object s : values.keySet()) 
                if (values.get(s).equals(anItem)) 
                    value = s;
        }

        @Override
        public Object getSelectedItem() {
            return values.get(value);
        }

        @Override
        public int getSize() {
            return values.size();
        }

        @Override
        public Object getElementAt(int index) {
            int i = 0;
            for (Object s : values.keySet()) {
                if (i++ == index)
                    return values.get(s);
            }            
            return null;
        }

        @Override
        public void addListDataListener(ListDataListener l) {
            //        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void removeListDataListener(ListDataListener l) {
            //        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    public DBComboBox() {
        addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    onValueChange();
                }
            }
        });
    }
    Dataset dataset;
    String columnName;
    String lookupColumnName;
    
    public void requery() throws Exception{
        dataset.open();
        model = new Model(dataset, columnName, lookupColumnName);
        setModel(model);
        
    }
    
    public String getLabel(){
        return label;
    }

    public void setDataset(Dataset dataset, String columnName, String lookupColumnName) throws Exception{
        this.dataset = dataset;
        this.columnName=columnName;
        this.lookupColumnName = lookupColumnName;
        try{
            model = new Model(dataset, columnName, lookupColumnName);
            setModel(model);
        } catch (Exception e){
            e.printStackTrace();
            throw new Exception("Ошбка при открытии DBComboBox");
        }
    }

    public void setValue(Object value) {
        model.value = value;
        onValueChange();
    }

    public Object getValue() {
        return model.value;
    }

    public void onValueChange() {
//        System.out.println("-->" + getValue());
    }
    
}
