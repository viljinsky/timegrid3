/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.awt.BorderLayout;
import java.awt.Container;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import ru.viljinsky.dialogs.BaseDialog;
import ru.viljinsky.reports.ScheduleParams;
import ru.viljinsky.sqlite.DataModule;

/**
 *
 * @author вадик
 */
public class TestAttr extends BaseDialog{
    public TestAttr() {
        setTitle("Параметры расписания");
    }
  
    
//    ScheduleParams params = ScheduleParams.getInstance();

    @Override
    public void doOnEntry() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Container getPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel boxPanel = new JPanel();
        boxPanel.setLayout(new BoxLayout(boxPanel, BoxLayout.Y_AXIS));
        try{
            for (String param_name :ScheduleParams.getParamNames()){
                Box box = Box.createHorizontalBox();
                box.add(new JLabel(param_name));
                box.add(Box.createHorizontalStrut(6));
                JTextField field = new JTextField(12);
                field.setText(ScheduleParams.getStringParamByName(param_name));
                box.add(field);
                
                boxPanel.add(box);
                boxPanel.add(Box.createVerticalStrut(12));
                
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        boxPanel.setBorder(new EmptyBorder(6,12,6,12));
        panel.add(boxPanel,BorderLayout.PAGE_START);
        return panel;
    }
    
    public static void main(String[] args){
        try{
            DataModule.open();
        } catch (Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
        
        BaseDialog dlg = new TestAttr();
        dlg.showModal(null);
        dlg.dispose();
        System.out.println("OK");
        
    }
}
