/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListDataListener;

/**
 *
 * @author вадик
 */
public abstract class BaseDialog extends JDialog implements ActionListener{
    public static int RESULT_NONE = 0;
    public static int RESULT_OK = 1;
    public static int RESULT_CANCEL = 2;
    public static int RESULT_IGNORE = 3;
    
    public int modalResult = RESULT_NONE;
    
    public Integer getResult(){
        return modalResult;
    }
    
    public Integer showModal(Component owner){
        setMinimumSize(new Dimension(400,300));
        pack();
        
        int x,y;
        Point p;
        Dimension d;
        
        if (owner!=null){
            p = owner.getLocationOnScreen();
            d = owner.getSize();
            x = p.x+(d.width-getWidth())/2;
            y = p.y+(d.height-getHeight())/2;
        } else {
            d = getToolkit().getScreenSize();
            x = (d.width-getWidth())/2;
            y = (d.height-getHeight())/2;
        }
        
        setLocation(x,y);
        setVisible(true);
        return modalResult;
    }
    
    public BaseDialog(){
        setModal(true);
        Container content = getContentPane();
        content.setLayout(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton buton;
        
        buton= new JButton("OK");
        buton.addActionListener(this);
        buttonPanel.add(buton);
        
        buton= new JButton("CANCEL");
        buton.addActionListener(this);
        buttonPanel.add(buton);
        JPanel p = (JPanel)getPanel();
        content.add(p);
        content.add(buttonPanel,BorderLayout.PAGE_END);
        
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()){
            case "OK":
                try{
                    doOnEntry();
                    modalResult=RESULT_OK;
                    setVisible(false);
                } catch(Exception ex){
                    JOptionPane.showMessageDialog(rootPane, ex.getMessage());
                }
                break;
            case "CANCEL":
                modalResult=RESULT_CANCEL;
                setVisible(false);
                break;
        }
    }
    
    public abstract void doOnEntry() throws Exception;
    
    
    public Container getPanel(){
        JPanel panel = new JPanel();
        panel.setBackground(Color.red);
        return panel;
    }
    
}


//interface IEntryControl{
//    public String getColumnName();
//    public JComponent getComponent();
//    public void setValue(Object value);
//    public Object getValue();
//}
//
//class ComboControl extends JComboBox implements IEntryControl{
//    
//    Map<Object,String> map = new HashMap<>();
//    Object value;
//    Column column;
//    
//    class Model implements ComboBoxModel{
//        
//        @Override
//        public void setSelectedItem(Object anItem) {
//            for (Object k:map.keySet()){
//                if (map.get(k).equals(anItem)){
//                    value=k;
//                    break;
//                }
//            }
//        }
//
//        @Override
//        public Object getSelectedItem() {
//            return map.get(value);
//        }
//
//        @Override
//        public int getSize() {
//            return map.size();
//        }
//
//        @Override
//        public Object getElementAt(int index) {
//            Object[] k = map.keySet().toArray();
//            return map.get(k[index]);
//        }
//
//        @Override
//        public void addListDataListener(ListDataListener l) {
//        }
//
//        @Override
//        public void removeListDataListener(ListDataListener l) {
//        }
//    }
//    
//    
//    public ComboControl(Column column,Map<Object,String> lookup){
//        this.column = column;
//        this.map=lookup;
//        Model model = new Model();
//        setModel(model);
//    }
//
//    @Override
//    public String getColumnName() {
//        return column.columnName;
//    }
//
//    @Override
//    public JComponent getComponent() {
//        return this;
//    }
//
//    @Override
//    public void setValue(Object value) {
//        this.value=value;
//        
//    }
//
//    @Override
//    public Object getValue() {
//        return value;
//    }
//}
//
//class BoolControl extends JCheckBox implements IEntryControl{
//    Column column;
//    
//    public BoolControl(Column column){
//        this.column=column;
//    }
//
//    @Override
//    public String getColumnName() {
//        return column.columnName;
//    }
//
//    @Override
//    public JComponent getComponent() {
//        return this;
//    }
//
//    @Override
//    public void setValue(Object value) {
//        if (value!=null){
//            setSelected(value.toString().equals("true"));
//        }
//    }
//
//    @Override
//    public Object getValue() {
//        if (isSelected())
//            return "true";
//        else
//            return "false";
//    }
//}
//class TextControl extends JTextArea implements IEntryControl{
//    String columnName;
//    public TextControl(String columnName){
//        super(7, 25);
//        this.columnName=columnName;
//    }
//
//    @Override
//    public String getColumnName() {
//        return columnName;
//    }
//
//    @Override
//    public JComponent getComponent() {
//        return new JScrollPane(this);
//    }
//
//    @Override
//    public void setValue(Object value) {
//        if (value== null)
//            setText("");
//        else
//            setText(value.toString());
//    }
//
//    @Override
//    public Object getValue() {
//        if (getText().isEmpty())
//            return null;
//        else
//            return getText();
//                    
//    }
//}
//
//class EditControl extends JTextField implements IEntryControl{
//    String columnName;
//    public EditControl(String columnName){
//        super(10);
//        this.columnName=columnName;
//    }
//
//    @Override
//    public JComponent getComponent() {
//        return this;
//    }
//
//    @Override
//    public void setValue(Object value) {
//        if(value==null)
//            setText("");
//        else 
//            setText(value.toString());
//    }
//
//    @Override
//    public Object getValue() {
//        if (getText().isEmpty())
//            return null;
//        else 
//            return getText();
//                    
//    }
//
//    @Override
//    public String getColumnName() {
//        return columnName;
//    }
//}


abstract class DataEntryDialog extends BaseDialog{
    protected EntryPanel panel;

    @Override
    public Container getPanel() {
        panel = new EntryPanel();
        return panel;
    }
    
    
    public void setDataset(IDataset dataset){
        panel.setDataset(dataset);
    }

    
}