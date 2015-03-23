/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky;

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
class DBComboBox extends JComboBox<String> {
    Model model;

    class Model implements ComboBoxModel {

        Object value;
        Map<Object, String> values;

        public Model(Dataset dataset, String columnName, String lookupColumn) {
            values = new HashMap<>();
            Map<String, Object> v;
            for (int i = 0; i < dataset.getRowCount(); i++) {
                v = dataset.getValues(i);
                values.put(v.get(columnName), v.get(lookupColumn).toString());
            }
        }

        @Override
        public void setSelectedItem(Object anItem) {
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

    public void setDataset(Dataset dataset, String columnName, String lookupColumnName) {
        model = new Model(dataset, columnName, lookupColumnName);
        setModel(model);
    }

    public void setValue(Object value) {
        model.value = value;
    }

    public Object getValue() {
        return model.value;
    }

    public void onValueChange() {
//        System.out.println("-->" + getValue());
    }
    
}
