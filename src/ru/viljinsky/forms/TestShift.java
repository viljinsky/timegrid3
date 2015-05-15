/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import ru.viljinsky.sqlite.DBComboBox;
import ru.viljinsky.sqlite.DataModule;
import ru.viljinsky.sqlite.Dataset;
import ru.viljinsky.sqlite.Grid;
import ru.viljinsky.sqlite.IDataset;

/**
 *   Dataset shift_type   комбобох
 *   Dataset shift        мастер таблица
 *   Dataset shift_detail подчинёная таблица
 * 
 * 
 *    String masterDataset = "profile";
 *    String slaveDataset = "profile_item";
 *    String refrenence = "id=profile_id";
 *    
 *    String comboDataset = "profile_type";
 *    String comboIdColumn = "id";
 *    String comboLookupKey = "profile_type_id";
 *    String comboLookupValue = "caption";
 *
 *    String masterDataset = "shift";
 *    String slaveDataset = "shift_detail";
 *    String refrenence = "id=shift_id";
 *    
 *    String comboDataset = "shift_type";
 *    String comboIdColumn = "id";
 *    String comboLookupKey = "shift_type_id";
 *    String comboLookupValue = "caption";
 * 
 * 
 * @author вадик
 */
public class TestShift extends Panel{
    Grid grid1 = new MasterGrid();
    Grid grid2 = new Grid();
    Controls controls = new Controls();
    ControlDetails controlDetails = new ControlDetails();
    
    String masterDataset;
    String slaveDataset;
    String refrenence;
    
    String comboDataset;
    String comboIdColumn;
    String comboLookupKey;
    String comboLookupValue;
    
    public TestShift(){
//        setPreferredSize(new Dimension(800,600));
        setLayout(new BorderLayout());
        JPanel details = new JPanel(new BorderLayout());
        details.add(new JScrollPane(grid2));
        details.add(controlDetails,BorderLayout.PAGE_START);
                
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(new JScrollPane(grid1));
        splitPane.setBottomComponent(details);
        splitPane.setResizeWeight(.5);
        
        add(controls,BorderLayout.PAGE_START);
        add(splitPane,BorderLayout.CENTER);
    }
    
    
    public void setParams(Map<String,String> params){

        masterDataset       = params.get("masterDataset");
        slaveDataset        = params.get("slaveDataset");
        refrenence          = params.get("refrenence");

        comboDataset        = params.get("comboDataset");
        comboIdColumn       = params.get("comboIdColumn");
        comboLookupKey      = params.get("comboLookupKey");
        comboLookupValue    = params.get("comboLookupValue");
    }
    

    
    class Act extends AbstractAction{

        public Act(String name) {
            super(name);
            putValue(ACTION_COMMAND_KEY, name);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            doCommand(e.getActionCommand());
        }
    }
    
    public void doCommand(String command){
        try{
            switch (command ){
                case "INCLUDE":
                    break;
                case "EXCLUDE":
                    break;
                case "FILL":
                    break;
                case "ADD":
                    break;
                case "EDIT":
                    break;
                case "DELETE":
                    break;
                    
            }
        } catch (Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
    /**
     * Управление подчинённым датасетом
     */
    class ControlDetails extends JPanel{
        Action[] deatilsAction = {new Act("FILL"),new Act("INCLUDE"),new Act("EXCLUDE")};
        
        public ControlDetails(){
            super(new FlowLayout(FlowLayout.LEFT));
            JButton btn;
            for (Action a:deatilsAction){
                btn=new JButton(a);
                add(btn);
            }
        }
    }
    
    class Controls extends JPanel{
        Combo combo;
        JButton btnAdd;
        JButton btnEdit;
        JButton btnDelete;
        
        class Combo extends DBComboBox{

            @Override
            public void onValueChange() {
//                System.out.println("****ValueChange");
                Map<String,Object> filter = new HashMap<>();
                filter.put(comboLookupKey, getValue());
                IDataset dataset = grid1.getDataset();
                try{
                    dataset.setFilter(filter);
                    dataset.open();
                    grid1.refresh();
                    if (!dataset.isEmpty()){
                        grid1.getSelectionModel().setSelectionInterval(0, 0);
                    };
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        
        public Controls(){
            super(new FlowLayout(FlowLayout.LEFT));
            
            combo = new Combo();
            btnAdd= new JButton("add");
            btnEdit= new JButton("edit");
            btnDelete = new JButton("delete");
            
            add(combo);
            add(btnAdd);
            add(btnEdit);
            add(btnDelete);
        }
    }
    
    class MasterGrid extends Grid{
        Map<String,Object> filter = new HashMap<>();
       
        
        @Override
        public void gridSelectionChange() {
            String[] ss = refrenence.split("=");
            
            int row = getSelectedRow();
            if (row>=0){
                IDataset dataset = getDataset();
                try {
                    Map<String,Object> values = dataset.getValues(row);
                    filter.put(ss[1], values.get(ss[0]));
                    System.out.println("filter:"+filter);
                    grid2.getDataset().setFilter(filter);
                    grid2.getDataset().open();
                    grid2.refresh();
                } catch (Exception e){
                    JOptionPane.showMessageDialog(this, e.getMessage());
                }
            }
        }
        
    }
    
    
    public void open(){
        DataModule dataModule= DataModule.getInstance();
        try{
            Dataset dataset1 = dataModule.getDataset(masterDataset);
            dataset1.test();
            grid1.setDataset(dataset1);
            
            Dataset dataset2;
            if (dataModule.isTableExists(slaveDataset))
                dataset2 = dataModule.getDataset(slaveDataset);
            else 
                dataset2 = dataModule.getSQLDataset(slaveDataset);
                
            dataset2.test();
            grid2.setDataset(dataset2);
            
//            dataset1.open();
            
            Dataset dataset3 = dataModule.getDataset(comboDataset);
            dataset3.open();
            controls.combo.setDataset(dataset3, comboIdColumn, comboLookupValue);
            if (!dataset3.isEmpty()){
                controls.combo.setValue(dataset3.getValues(0).get(comboIdColumn));
            }
            
        } catch (Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
        
    }
    
    
    private static  Map<String,String> params1,params2;
    public static void initParams(){
    

        // Параметры для profile_type,profile,profile_item
        params1 = new HashMap<>();
                
        params1.put("masterDataset","profile");
        params1.put("slaveDataset", "profile_item");
        params1.put("refrenence","id=profile_id");

        params1.put("comboDataset","profile_type");
        params1.put("comboIdColumn","id");
        params1.put("comboLookupKey","profile_type_id");
        params1.put("comboLookupValue","caption");
        
        // Параметры для shift_type,shift,shift_detail
        params2=new HashMap<>();

        params2.put("masterDataset","shift");
        params2.put("slaveDataset","shift_detail");
        params2.put("refrenence","id=shift_id");
        //    
        params2.put("comboDataset","shift_type");
        params2.put("comboIdColumn","id");
        params2.put("comboLookupKey","shift_type_id");
        params2.put("comboLookupValue","caption");
    
    }

    public static void createAndShow(){
    }
    
    
    public static void main(String[] args) throws Exception{
        DataModule.getInstance().open();
        initParams();
                
        TestShift panel = new TestShift();
        panel.setParams(params1);
        panel.open();
        
        TestShift panel2 = new TestShift();
        panel2.setParams(params2);
        panel2.open();
        
        JFrame frame = new JFrame("TestShift");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setPreferredSize(new Dimension(500,400));
        tabbedPane.addTab("profile",panel);
        tabbedPane.addTab("shift",panel2);
        frame.setContentPane(tabbedPane);
        frame.pack();
        frame.setVisible(true);
    }
    
}
