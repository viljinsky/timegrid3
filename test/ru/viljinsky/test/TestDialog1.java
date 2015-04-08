/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.test;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ru.viljinsky.BaseDialog;
import ru.viljinsky.DataModule;
import ru.viljinsky.Dataset;
import ru.viljinsky.IDataset;
import ru.viljinsky.Recordset;
import ru.viljinsky.forms.ShiftDialog;
import ru.viljinsky.SelectDialog;





public class TestDialog1  extends JFrame implements ActionListener{
    DataModule dataModule = DataModule.getInstance();
    
    public TestDialog1(){
        Container content = getContentPane();
        content.setPreferredSize(new Dimension(500,400));
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton button = new JButton("BUTTON1");
        button.addActionListener(this);
        panel.add(button);
        button = new JButton("BUTTON2");
        button.addActionListener(this);
        panel.add(button);
        content.add(panel,BorderLayout.PAGE_START);
    }
    
    private void testButton1(){
        SelectDialog dlg = new SelectDialog() {

            @Override
            public void doOnEntry() throws Exception {
                for (Object value:getSelected()){
                    System.out.println(value.toString());
                }
                for (Object a:getRemoved()){
                    System.out.println("DELETE "+a.toString());
                }
                for (Object a:getAdded()){
                    System.out.println("APPEND "+a.toString());
                }

            }
        };
        Dataset dataset = null;
        try{
            dataset = dataModule.getDataset("subject");

            Set<Object> set = new HashSet<>();
            set.add(new Integer(1));set.add(new Integer(2));set.add(new Integer(3));


            dlg.setDataset((IDataset)dataset,"id","subject_name");
            dlg.setSelected(set);
            dlg.showModal(this);


            if (dlg.modalResult == BaseDialog.RESULT_OK)
                System.out.println("OK");
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }
    
    public void testButton2() throws Exception{
        final Integer shift_id=2;
        ShiftDialog dlg = new ShiftDialog(){

            @Override
            public void doOnEntry() throws Exception {
                int day,bell;
                try{
                    for (Integer[] n:getAdded()){
                        System.out.println("ADDED :"+n[0]+" "+n[1]);
                        day=n[0]+1;bell=n[1]+1;
                        dataModule.execute("insert into shift_detail(shift_id,day_id,bell_id) values("+shift_id+","+day+","+bell+")");
                    }
                    for (Integer[] n:getRemoved()){
                        day=n[0]+1;bell=n[1]+1;
                        System.out.println("REMOVED :"+n[0]+" "+n[1]);
                        dataModule.execute("delete from shift_detail where shift_id="+shift_id+" and day_id="+day+" and bell_id="+bell + ";");
                    }
                    dataModule.commit();
                } catch (Exception e){
                    dataModule.rollback();
                    throw new Exception("EDIT_SHIFT_ERROR\n"+e.getMessage());
                }
            }

        };
        Recordset rs = dataModule.getRecordet("select day_id,bell_id from shift_detail where shift_id="+shift_id);
        List list = new ArrayList();
        Object[] v;
        for (int i=0;i<rs.size();i++){
            v=rs.get(i);
            list.add(new Integer[]{(Integer)v[0]-1,(Integer)v[1]-1});
        }
        dlg.setSelected(list);
        dlg.showModal(rootPane);
        
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        doCommand(e.getActionCommand());
        
    }
    
    public void doCommand(String command){
        try{
            switch(command){
                case "BUTTON1":
                    testButton1();
                    break;
                case "BUTTON2":
                    testButton2();
                    break;
            }
        } catch (Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(rootPane, e.getMessage());
        }
    }
    
    public static void main(String[] args) throws Exception{
        DataModule.getInstance().open();
        TestDialog1 frame = new TestDialog1();
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((d.width-frame.getWidth())/2, (d.height-frame.getHeight())/2);
        frame.setVisible(true);
    }

    
}
