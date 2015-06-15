/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.dialogs;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author вадик
 */

abstract class LabelButton extends JLabel{
    Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
    Cursor handpointCursor = new Cursor(Cursor.HAND_CURSOR);

    public LabelButton(String text) {
        super(text);
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                mouseClick();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(defaultCursor);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setCursor(handpointCursor);
            }
            
        });
    }
    
    public abstract void mouseClick();
    
}

interface IFieldControl{
    public static final String FC_FIELD = "FC_FIELD";
    public static final String FC_TEXT = "FC_TEXT";
    public static final String FC_NUMBER = "FC_NUMBER";
    public static final String FC_LIST = "FC_LIST";
    public static final String FC_BOOLEAN = "FC_BOOLEAN";
    public static final String FC_PATH = "FC_PATH";
    public static final String FC_DIR = "FC_DIR";
    
    public static final String FC_DATE = "FC_DATE";
    public static final String FC_TIME = "FC_TIME";
    
    public abstract void setValue(Object value);
    public abstract Object getValue();
    public abstract Integer getIntegerValue();
    public abstract Boolean getBooleanValue();
}

class DefaultFieldControl extends JTextField implements IFieldControl{

    public DefaultFieldControl() {
        super(20);
    }
    

    @Override
    public void setValue(Object value) {
        if (value==null)
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
    public Integer getIntegerValue() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean getBooleanValue() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

class TimeControl extends JPanel implements IFieldControl,ChangeListener{
    JSpinner fldHour = new JSpinner(new SpinnerNumberModel(0,0,24,1));
    JSpinner fldMinute = new JSpinner(new SpinnerNumberModel(0,0,59,1));
    Calendar calendar = Calendar.getInstance();
    
    public TimeControl(){
        Integer h = calendar.get(Calendar.HOUR_OF_DAY);
        Integer m = calendar.get(Calendar.MINUTE);
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+4:00"));
        
        
        
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        
        add(fldHour);
        add(Box.createHorizontalStrut(6));
        add(new JLabel("час."));
        add(Box.createHorizontalStrut(6));
        
        add(fldMinute);
        add(Box.createHorizontalStrut(6));
        add(new JLabel("мин."));        
        add(Box.createHorizontalGlue());
        
        fldHour.setValue(h);
        fldMinute.setValue(m);
        calendar.setTime(calendar.getTime());
        
        fldHour.addChangeListener(this);
        fldMinute.addChangeListener(this);
    }

    public void clear(){
        fldHour.setValue(0);
        fldMinute.setValue(0);
//        calendar.clear();
    }
    
    @Override
    public void setValue(Object value) {
        clear();
        if (value==null){
            return;
        }
            
        if (value instanceof Date){
            calendar.setTime((Date)value);
            fldHour.setValue(calendar.get(Calendar.HOUR_OF_DAY));
            fldMinute.setValue(calendar.get(Calendar.MINUTE));
        } else if (value instanceof String){
            String[] s = ((String)value).split(":");
            if (s.length==2){
                int h=Integer.valueOf(s[0]);
                fldHour.setValue(h);
                int m = Integer.valueOf(s[1]);
                fldMinute.setValue(m);
//                calendar.set(Calendar.HOUR, h);
//                calendar.set(Calendar.MINUTE, m);
            }
            
        }
        
    }

    @Override
    public Object getValue() {
        calendar.set(Calendar.HOUR, (Integer)fldHour.getValue());
        calendar.set(Calendar.MINUTE,(Integer)fldMinute.getValue());
        return calendar.getTime();
    }

    @Override
    public Integer getIntegerValue() {
        return null;
    }

    @Override
    public Boolean getBooleanValue() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void stateChanged(ChangeEvent e) {
         SpinnerNumberModel model ;
         int h = (Integer)fldHour.getValue();
         int m = (Integer)fldMinute.getValue();
         System.out.println(String.format("%2d %2d",h,m));
    }
    
}

/**
 * Отображение даты
 * @author вадик
 */
class DateControl extends JPanel implements IFieldControl{
    Calendar calendar = Calendar.getInstance();
    JTextField textYear = new JTextField(4);
    JTextField textMonth = new JTextField(2);
    JTextField textDate = new JTextField(2);
    
    public DateControl(){
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(textYear);
        add(Box.createHorizontalStrut(6));
        add(textMonth);
        add(Box.createHorizontalStrut(6));
        add(textDate);
        add(Box.createHorizontalGlue());
        
        int y = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH);
        int d = calendar.get(Calendar.DAY_OF_MONTH);
        textYear.setText(String.valueOf(y));
        textMonth.setText(String.valueOf(m));
        textDate.setText(String.valueOf(d));
    }
    
    private void clear(){
        textYear.setText("");
        textMonth.setText("");
        textDate.setText("");
    }

    @Override
    public Integer getIntegerValue() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean getBooleanValue() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setValue(Object value) {
        clear();
        if (value == null)
            return;
        if (value instanceof Date){
            setDate((Date)value);
        } else if (value instanceof String){
            String[] s = ((String)value).split("-");
            int y=Integer.valueOf(s[0]);
            int m=Integer.valueOf(s[1]);
            int d =Integer.valueOf(s[2]);
            setYear(y);
            setMonth(m);
            setDay(d);
        }

    }
    
    public void setDate(Date date){
        calendar.setTime(date);
        setYear(calendar.get(Calendar.YEAR));
        setMonth(calendar.get(Calendar.MONTH));
        setDay(calendar.get(Calendar.DAY_OF_MONTH));
    }
    
    private void setDay(Integer day_of_month){
        if (day_of_month==null)
            textDate.setText("");
        else
            textDate.setText(String.valueOf(day_of_month));
    }
    
    private void setMonth(Integer month){
        if (month==null)
            textMonth.setText("");
        else
            textMonth.setText(String.valueOf(month));
    }
    private void setYear(Integer year){
        if (year==null)
            textYear.setText("");
        else
            textYear.setText(String.valueOf(year));
    }
    
    public Date getDate(){
        if (textYear.getText().isEmpty() || textDate.getText().isEmpty() || textMonth.getText().isEmpty())
            return null;
        Integer y = Integer.valueOf(textYear.getText());
        Integer m = Integer.valueOf(textMonth.getText());
        Integer d = Integer.valueOf(textDate.getText());
        calendar.set(Calendar.YEAR, y);
        calendar.set(Calendar.MONTH, m);
        calendar.set(Calendar.DAY_OF_MONTH, d);
        return calendar.getTime();
    }

    @Override
    public Object getValue() {
        return getDate();
    }
    
}

class DirControl extends JPanel implements IFieldControl{
    File file = null;
    JTextField text = new JTextField();
    LabelButton label = new LabelButton(" ... "){

        @Override
        public void mouseClick() {
            selectDir();
        }
        
    };
    
    protected void selectDir(){
        JFileChooser fc = new JFileChooser(file);
        if (file!=null){
            fc.setSelectedFile(file);
        }
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int retVal = fc.showOpenDialog(null);
        if (retVal==JFileChooser.APPROVE_OPTION){
            setValue(fc.getSelectedFile());
        }
    }
    
    public DirControl(){
        setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
        add(text);
        add(label);
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof File){
            file = (File)value;
        } else if (value instanceof String){
            file = new File((String)value);
        }
        if (file==null){
            text.setText("");
        } else {
            text.setText(file.getPath());
        }
    }

    @Override
    public Object getValue() {
        return file;
    }

    @Override
    public Integer getIntegerValue() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean getBooleanValue() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

/**
 * Отображает путь
 * @author вадик
 */
class PathControl extends JPanel implements IFieldControl{
    File file = null;
    JTextField textField = new JTextField();
    LabelButton label = new LabelButton(" ... "){

        @Override
        public void mouseClick() {
            select();
        }
    
    };
    
    public PathControl(){
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(textField);
        add(label);
    }

    @Override
    public void setValue(Object value) {
        if (value!=null){
            if (value instanceof File){
                file=(File)value;
            } else if (value instanceof String){
                file = new File((String)value);
            }
            textField.setText(file.getPath());
        } else {
            file = null;
            textField.setText("");
        }
    }
    
    public void select(){
        JFileChooser fileChooser = new JFileChooser(file);
        if (file!=null)
            fileChooser.setSelectedFile(file);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        int retVal = fileChooser.showDialog(null,"Селект");
        if (retVal==JFileChooser.APPROVE_OPTION){
            setValue(fileChooser.getSelectedFile());
        }
    }

    @Override
    public Object getValue() {
        return file;
    }

    @Override
    public Integer getIntegerValue() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean getBooleanValue() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

class BooleanControl extends JCheckBox implements IFieldControl{

    @Override
    public void setValue(Object value) {
        if (value==null){
            setSelected(false);
        } else {
            if (value instanceof Boolean){
                setSelected((Boolean)value);
            }
        }
            
    }

    @Override
    public Object getValue() {
        return getBooleanValue();
    }

    @Override
    public Integer getIntegerValue() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean getBooleanValue() {
        return isSelected();
    }
}



class ValuePanel extends JPanel{
    Map<String,IFieldControl> fields = new HashMap<>();
    public ValuePanel(){
        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
    }
    
    /**
     * Сформировать список полей из мапа
     * @param fields 
     */
    public void setFields(Map<String,String> fields){
    }
    
    protected IFieldControl createField(String[] params) throws Exception{
        if (params.length>2){
            switch (params[2]){
                case (IFieldControl.FC_BOOLEAN):
                    return new BooleanControl();
                case (IFieldControl.FC_PATH):
                    return new PathControl();
                case (IFieldControl.FC_DIR):
                    return new DirControl();
                case (IFieldControl.FC_DATE):
                    return new DateControl();
                case (IFieldControl.FC_TIME):
                    return new TimeControl();
                default:
                    throw new Exception("UNKNOW_FIRL_TYPE\n"+params[2]);
            }
        }
        return new DefaultFieldControl();
    };
    
    /**
     * Сформировать список полей из строки
     * @param fields 
     */
    public void setFields(String[] fieldNames) throws Exception{
        IFieldControl fieldControl;
        Box box;
        String fieldName;
        String fieldCaption;
        String fieldType;
    
        String[] params ;
        for (String field:fieldNames){
            params=field.split(";");
            fieldName = params[0];
            fieldCaption=(params.length>1?params[1]:params[0]);
            if (fieldCaption.isEmpty()) fieldCaption=fieldName;
            
            
            box = Box.createHorizontalBox();
            box.add(new JLabel(fieldCaption));
            box.add(Box.createHorizontalStrut(6));
            fieldControl= createField(params);
            box.add((JComponent)fieldControl);
            box.add(Box.createHorizontalGlue());
            fields.put(fieldName, fieldControl);
            add(box);
            add(Box.createVerticalStrut(12));
        }
        setBorder(new EmptyBorder(12,6,12,6));
    }
    
    public void clear(){
        for (String controlName:fields.keySet()){
            fields.get(controlName).setValue(null);
        }
    }
    public void setValues(Map<String,Object> values){
        clear();
        if (values==null)
            return;
        for (String fieldName:values.keySet()){
            if (fields.containsKey(fieldName)){
                IFieldControl control = fields.get(fieldName);
                control.setValue(values.get(fieldName));
            } else {
                System.out.println("FIELD_NOT_FOUND");
            }
        }
    }
    
    public void setValue(String fieldName,Object value) throws Exception{
        IFieldControl control =  fields.get(fieldName);
        if (control==null){
            throw new Exception("FIELD_NOT_FOUND\n"+fieldName);
        }
        control.setValue(value);
                
    }
    
    public Map<String,Object> getValues(){
        Map<String,Object> result = new HashMap<>();
        for (String fieldName:fields.keySet()){
            result.put(fieldName, fields.get(fieldName).getValue());
        }
        return result;
    }
}

public class EntryForm extends BaseDialog{
    
    ValuePanel valuesPanel;
    
    @Override
    public void doOnEntry() throws Exception {
       
        doCommand("save");
    }
    
    
    JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    
    public void setFields(String[] fieldNames) throws Exception{
        valuesPanel.setFields(fieldNames);
    }
    
    public void setValues(Map<String,Object> values){
        valuesPanel.setValues(values);
    }
    
    public void setValue(String fieldName,Object value){
        try{
            valuesPanel.setValue(fieldName,value);
        } catch (Exception e){
            JOptionPane.showMessageDialog(rootPane, e.getMessage());
        }
    }
    
    public Object getValue(String fieldName){
        Object result = null;
        try{
            result= valuesPanel.fields.get(fieldName).getValue();
        } catch (Exception e){
            JOptionPane.showMessageDialog(rootPane, e.getMessage());
        }
        return result;
    }
    
    public Map<String,Object> getValues(){
        return valuesPanel.getValues();
    }


    @Override
    public Container getPanel() {
        valuesPanel=new ValuePanel();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(valuesPanel,BorderLayout.PAGE_START);
        return panel;
    }
    
    public void doCommand(String command){
        System.out.println(command);
        System.out.println(getValues().toString());
    }
    
    public static void main(String[] args){
        String[] sFields = {
            "field1;Поле1",
            "field2;;FC_BOOLEAN",
            "field3;Поле3",
            "field4;Источник;FC_PATH",
            "field5;Путь;FC_PATH",
            "field6;Дата;FC_DATE",
            "field7;Время;FC_TIME"
        };
        
        Map<String,Object> values= new HashMap<>();
        values.put("field3",123.4);
        Calendar c=Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GTM+4:00"));
        values.put("field7",c.getTime());
        
        EntryForm frame = new EntryForm();
        try{
            frame.setFields(sFields);
        frame.setValues(values);
        frame.setValue("field2", true);
        if (frame.showModal(null)==BaseDialog.RESULT_OK){
            System.out.println(frame.getValues());
        }
        frame.dispose();
        frame=null;
        System.out.println("OK");
        } catch (Exception e){
            e.printStackTrace();
        }
        
        
    }

    
}
