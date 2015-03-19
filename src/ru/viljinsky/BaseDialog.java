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
import java.awt.event.KeyEvent;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import sun.nio.ch.FileChannelImpl;

/**
 *
 * @author вадик
 */
public class BaseDialog extends JDialog implements ActionListener{
    public static int RESULT_NONE = 0;
    public static int RESULT_OK = 1;
    public static int RESULT_CANCEL = 2;
    public static int RESULT_IGNORE = 3;
    
    int modalResult = RESULT_NONE;
    
    public Integer getResult(){
        return modalResult;
    }
    
    public Integer showModal(Component owner){
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
        
        
//        p.setFocusable(true);
//        InputMap inputMap = p.getInputMap();
//        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
//        
//        inputMap.put(keyStroke, "key1");
//        ActionMap actionMap = p.getActionMap();
//        actionMap.put("key1",new AbstractAction() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                modalResult=RESULT_CANCEL;
//                System.out.println("OK");
//                setVisible(false);
//            }
//        });
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()){
            case "OK":
                try{
                    doOnEntry();
                } catch(Exception ex){
                    JOptionPane.showMessageDialog(rootPane, ex.getMessage());
                }
                modalResult=RESULT_OK;
                setVisible(false);
                break;
            case "CANCEL":
                modalResult=RESULT_CANCEL;
                setVisible(false);
                break;
        }
    }
    
    public void doOnEntry() throws Exception{
    }
    
    public Container getPanel(){
        JPanel panel = new JPanel();
        panel.setBackground(Color.red);
        return panel;
    }
    
    
    public static BaseDialog createEntreDialog(IDataset dataset,Map<String,Object> values){
        DataEntryDialog dlg = new DataEntryDialog();
        dlg.setTitle(dataset.getTableName());
        dlg.setDataset(dataset);
        if (values!=null){
            dlg.panel.setValues(values);
        }
        return dlg;
    }
    
}


interface IEntryControl{
    public String getColumnName();
    public JComponent getComponent();
    public void setValue(Object value);
    public Object getValue();
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

class EntryPanel extends JPanel{
    IDataset dataset;
    IEntryControl[] controls;
    
    public void setDataset(IDataset dataset){
        this.dataset=dataset;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        controls = new IEntryControl[dataset.getColumnCount()];
        for (int i=0;i<controls.length;i++){
            EditControl cntr = new EditControl(dataset.getColumn(i).columnName);
            controls[i]=cntr;
            
            Box box = Box.createHorizontalBox();
            box.add(new JLabel(dataset.getColumn(i).columnName));
            box.add(Box.createHorizontalStrut(6));
            box.add(cntr);
            box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
            add(box);
            add(Box.createVerticalStrut(12));
            
        }
        setBorder(new EmptyBorder(10,10,10,10));
    }
    
    public IEntryControl findControl(String columnName){
        for (IEntryControl control:controls){
            if (control.getColumnName().equals(columnName))
                return control;
        }
        return null;
    }
    
    public void setValues(Map<String,Object> map){
        for (String columnName:map.keySet()){
            IEntryControl control = findControl(columnName);
            if (control!=null){
                control.setValue(map.get(columnName));
            }
        };
        
    }
    
    public Map<String,Object> getValues(){
        Map<String,Object> map= new HashMap<>();
        for (IEntryControl control:controls){
            map.put(control.getColumnName(), control.getValue());
        }
        return map;
    }
}

class DataEntryDialog extends BaseDialog{
    protected EntryPanel panel;

    @Override
    public Container getPanel() {
        panel = new EntryPanel();
        return panel;
    }
    
    
    public void setDataset(IDataset dataset){
        panel.setDataset(dataset);
    }

    @Override
    public void doOnEntry() throws Exception {
        System.out.println("88888"+panel.getValues());
    }
    
    
}