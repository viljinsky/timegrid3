/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.awt.Dimension;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import ru.viljinsky.DataModule;

/**
 *
 * @author вадик
 */
public class TestShift2 extends JFrame{

    class PanelProfile extends TestShift{

        public PanelProfile() {
            Map<String,String> params= new HashMap<>();
            params.put("masterDataset","profile");
            params.put("slaveDataset", "select a.*,b.subject_name from profile_item a inner join subject b on a.subject_id=b.id;");
            params.put("refrenence","id=profile_id");

            params.put("comboDataset","profile_type");
            params.put("comboIdColumn","id");
            params.put("comboLookupKey","profile_type_id");
            params.put("comboLookupValue","caption");
            setParams(params);
        }

            @Override
            public void doCommand(String command) {
                try{
                    switch(command){
                        case "FILL":
                            System.out.println(masterDataset+" FILL command");
                            Integer profile_id= grid1.getInegerValue("id");
                            DataTask.fillProfile(profile_id);
                            grid2.requery();
                            break;
                    default:
                        super.doCommand(command);
                    }
                } catch (Exception e){
                    JOptionPane.showMessageDialog(rootPane, e);
                }
            }



    }

    class PanelShift extends TestShift{

        public PanelShift() {
            Map<String,String> params= new HashMap<>();
            params.put("masterDataset","shift");
            params.put("slaveDataset","shift_detail");
            params.put("refrenence","id=shift_id");
            //    
            params.put("comboDataset","shift_type");
            params.put("comboIdColumn","id");
            params.put("comboLookupKey","shift_type_id");
            params.put("comboLookupValue","caption");
            setParams(params);
        }

            @Override
            public void doCommand(String command) {
                try{
                    switch(command){
                        case "FILL":
                            Integer shift_id=grid1.getInegerValue("id");
                            DataTask.fillShift(shift_id);
                            grid2.requery();
                    default:
                        super.doCommand(command);
                    }
                } catch (Exception e){
                    JOptionPane.showMessageDialog(rootPane, e.getMessage());
                }
            }


    }

    JTabbedPane tabbedPane = new JTabbedPane();
    TestShift[] panels;
    
    
    public TestShift2(){
        
        tabbedPane.setPreferredSize(new Dimension(500,400));
        setContentPane(tabbedPane);
        
        panels = new TestShift[2];
        
        panels[0]= new PanelProfile();
        panels[1] = new PanelShift();
        
        
        tabbedPane.addTab(panels[0].masterDataset, panels[0]);               
        tabbedPane.add(panels[1].masterDataset,panels[1]);
        
    }
    
    public void open(){
        for (TestShift panel:panels){
            panel.open();
        }
    }
    
    private static TestShift2 frame = null;
    public static void showShiftDialog(JComponent owner){
        if (frame == null){
            frame = new TestShift2();
            frame.setDefaultCloseOperation(HIDE_ON_CLOSE);
            frame.pack();
            if (owner !=null){
                Dimension d = owner.getSize();
                Point p = owner.getLocationOnScreen();
                frame.setLocation(p.x+(d.width-frame.getWidth())/2,p.y+(d.height-frame.getHeight())/2);
            }
            frame.open();
        }
        frame.setVisible(true);
    }
    
    public static void main(String[] args) throws Exception{
        TestShift2 frame = new TestShift2();
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        
        DataModule.getInstance().open();
        frame.open();
        
    }
    
}
