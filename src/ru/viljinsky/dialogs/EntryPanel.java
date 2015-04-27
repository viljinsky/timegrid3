/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.dialogs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListDataListener;
import ru.viljinsky.Column;
import ru.viljinsky.ColumnMap;
import ru.viljinsky.IDataset;
import ru.viljinsky.Values;

/**
 *
 * @author вадик
 */

interface IEntryControl{
    public String getColumnName();
    public JComponent getComponent();
    public void setValue(Object value);
    public Object getValue();
}
class ColorControl extends JLabel implements IEntryControl{
    Color color = new Color(255,255,255);
    Column column;
    
    public ColorControl(Column column){
        setOpaque(true);
        setText("255 255 255");
        this.column=column;
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                selectColor();
            }
            
        });
    }
    
    private void selectColor(){
        ColorDialog dlg = new ColorDialog(color) {
            
            @Override
            public void doOnEntry() throws Exception {
                Color g =colorChooser.getColor();
                setValue(String.format("%d %d %d", g.getRed(),g.getGreen(),g.getBlue()));
            }
        };
        dlg.showModal(null);
    }
    
    @Override
    public String getColumnName() {
        return column.getColumnName();
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public void setValue(Object value) {
        String sValue;
        if (value == null){
            value = new String("255 255 255");
        }
        sValue = (String)value;
        if (sValue.isEmpty())
            sValue = new String("125 125 125");
            
        
        setText(sValue);
        setBorder(new LineBorder(Color.BLACK));
        String[] rgb = sValue.split(" ");
        color = new Color(Integer.valueOf(rgb[0]), Integer.valueOf(rgb[1]),Integer.valueOf(rgb[2]));
        this.setBackground(color);
    }

    @Override
    public Object getValue() {
        int blue = color.getBlue();
        int red =color.getRed();
        int green =color.getGreen();
        return String.format("%d %d %d", red,green,blue);
    }
}
class IntegerControl extends JSpinner implements IEntryControl{
    Column column;
    SpinnerNumberModel model = new SpinnerNumberModel();
    
    public IntegerControl(Column column){
        super();
        this.column = column;
        model.setMaximum(Integer.MAX_VALUE);
        model.setMinimum(Integer.MIN_VALUE);
        setModel(model);
        
    }

    @Override
    public String getColumnName() {
        return column.getColumnName();
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public void setValue(Object value) {
        if (value!=null)
        super.setValue(value); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getValue() {
        return super.getValue(); //To change body of generated methods, choose Tools | Templates.
    }
    
    

}
class ComboControl extends JComboBox implements IEntryControl{
    
    Map<Object,String> map = new HashMap<>();
    Object value;
    Column column;
    
    class Model implements ComboBoxModel{
        
        @Override
        public void setSelectedItem(Object anItem) {
            for (Object k:map.keySet()){
                if (map.get(k).equals(anItem)){
                    value=k;
                    break;
                }
            }
        }

        @Override
        public Object getSelectedItem() {
            return map.get(value);
        }

        @Override
        public int getSize() {
            return map.size();
        }

        @Override
        public Object getElementAt(int index) {
            Object[] k = map.keySet().toArray();
            return map.get(k[index]);
        }

        @Override
        public void addListDataListener(ListDataListener l) {
        }

        @Override
        public void removeListDataListener(ListDataListener l) {
        }
    }
    
    
    public ComboControl(Column column,Map<Object,String> lookup){
        this.column = column;
        this.map=lookup;
        Model model = new Model();
        setModel(model);
    }

    @Override
    public String getColumnName() {
        return column.getColumnName();
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public void setValue(Object value) {
        this.value=value;
        
    }

    @Override
    public Object getValue() {
        return value;
    }
}

class BoolControl extends JCheckBox implements IEntryControl{
    Column column;
    
    public BoolControl(Column column){
        this.column=column;
    }

    @Override
    public String getColumnName() {
        return column.getColumnName();
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public void setValue(Object value) {
        if (value!=null){
            setSelected(value.toString().equals("true"));
        }
    }

    @Override
    public Object getValue() {
        if (isSelected())
            return "true";
        else
            return "false";
    }
}
class TextControl extends JTextArea implements IEntryControl{
    String columnName;
    public TextControl(String columnName){
        super(7, 25);
        this.columnName=columnName;
    }

    @Override
    public String getColumnName() {
        return columnName;
    }

    @Override
    public JComponent getComponent() {
        return new JScrollPane(this);
    }

    @Override
    public void setValue(Object value) {
        if (value== null)
            setText("");
        else
            setText(value.toString());
    }

    @Override
    public Object getValue() {
        if (getText().isEmpty())
            return null;
        else
            return getText();
                    
    }
}

class EditControl extends JTextField implements IEntryControl{
    String columnName;
    public EditControl(String columnName){
        super(10);
        this.columnName=columnName;
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public void setValue(Object value) {
        if(value==null)
            setText("");
        else 
            setText(value.toString());
    }

    @Override
    public Object getValue() {
        if (getText().isEmpty())
            return null;
        else 
            return getText();
                    
    }

    @Override
    public String getColumnName() {
        return columnName;
    }
}

public class EntryPanel extends JPanel {
    IDataset dataset;
    IEntryControl[] controls;

    public void setDataset(IDataset dataset) {
        this.dataset = dataset;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        controls = new IEntryControl[dataset.getColumnCount()];
        Column column;
        IEntryControl cntr;
        Map<Object, String> lookupValues = null;
        Box box;
        String[] params;
        String columnLabel;
        Boolean columnVisible;
        for (int i = 0; i < controls.length; i++) {
            column = dataset.getColumn(i);
            params = ColumnMap.getParams(new String(column.getTableName()+"."+column.getColumnName()));
            if (params==null){
                columnLabel=column.getColumnName();
                columnVisible=true;
            }  else {
                columnLabel=params[0].isEmpty()?column.getColumnName():params[0];
                if (params.length>1 && params[1].equals("false"))
                    columnVisible=false;
                else
                    columnVisible=true;
            }
            
            try {
                lookupValues = dataset.getLookup(column.getColumnName());
            } catch (Exception e) {
                lookupValues = null;
                e.printStackTrace();
            }
            
            if (lookupValues != null) {
                cntr = new ComboControl(column, lookupValues);
            } else if (column.getColumnName().equals("color")){
                cntr= new ColorControl(column);
            } else if (column.isPrimary()){
                cntr = new EditControl(column.getColumnName());
            } else  {
                switch (column.getColumTypeName()) {
                    case "BOOLEAN":
                        cntr = new BoolControl(column);
                        break;
                    case "BLOB":
                        cntr = new TextControl(column.getColumnName());
                        break;
                    case "INTEGER":
                        cntr = new IntegerControl(column);
                        break;
                    default:
                        cntr = new EditControl(column.getColumnName());
                }
            }
            controls[i] = cntr;
            if (columnVisible){
                if (!column.getColumTypeName().equals("BLOB")) {
                    box = Box.createHorizontalBox();
                    box.add(new JLabel(columnLabel));//params[0].isEmpty()?column.getColumnName():params[0]));//dataset.getColumn(i).getColumnName()));
                    box.add(Box.createHorizontalStrut(6));
                    box.add(cntr.getComponent());
                    box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
                    add(box);
                } else {
                    box = Box.createHorizontalBox();
                    box.add(new JLabel(columnLabel));
                    box.add(Box.createHorizontalGlue());
                    add(box);
                    box = Box.createHorizontalBox();
                    box.add(cntr.getComponent());
                    add(box);
                }
                add(Box.createVerticalStrut(12));
            }
        }
        setBorder(new EmptyBorder(10, 10, 10, 10));
    }

    public IEntryControl findControl(String columnName) {
        for (IEntryControl control : controls) {
            if (control.getColumnName().equals(columnName)) {
                return control;
            }
        }
        return null;
    }

    public void setValues(Map<String, Object> map) {
        for (String columnName : map.keySet()) {
            IEntryControl control = findControl(columnName);
            if (control != null) {
                control.setValue(map.get(columnName));
            }
        }
        ;
    }

    public Values getValues() {
        Values map = new Values();
        String columnName;
        Object value;
        for (IEntryControl control : controls) {
            columnName = control.getColumnName();
            value = control.getValue();
            map.put(columnName, value);
        }
        return map;
    }
    
}
