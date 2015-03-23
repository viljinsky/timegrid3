/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

/**
 *   Dataset shift_type   комбобох
 *   Dataset shift        мастер таблица
 *   Dataset shift_detail подчинёная таблица
 * 
 * @author вадик
 */
public class TestShift extends Panel{
    Grid grid1 = new MasterGrid();
    Grid grid2 = new Grid();
    Controls controls = new Controls();
            
    
    class Controls extends JPanel{
        Combo combo;
        JButton btnAdd;
        JButton btnEdit;
        JButton btnDelete;
        
        class Combo extends DBComboBox{

            @Override
            public void onValueChange() {
                System.out.println("****ValueChange");
                Map<String,Object> filter = new HashMap<>();
                filter.put("shift_type_id", getValue());
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
            int row = getSelectedRow();
            if (row>=0){
                IDataset dataset = getDataset();
                try {
                    Map<String,Object> values = dataset.getValues(row);
                    filter.put("shift_id", values.get("id"));
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
    
    public TestShift(){
        setPreferredSize(new Dimension(800,600));
        setLayout(new BorderLayout());
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(new JScrollPane(grid1));
        splitPane.setBottomComponent(new JScrollPane(grid2));
        splitPane.setResizeWeight(.5);
        
        add(controls,BorderLayout.PAGE_START);
        add(splitPane,BorderLayout.CENTER);
        open();
    }
    
    public void open(){
        DataModule dataModule= DataModule.getInstance();
        try{
            Dataset dataset1 = dataModule.getDataset("shift");
            dataset1.test();
            grid1.setDataset(dataset1);
            
            Dataset dataset2 = dataModule.getDataset("shift_detail");
            dataset2.test();
            grid2.setDataset(dataset2);
            
            dataset1.open();
            
            Dataset dataset3 = dataModule.getDataset("shift_type");
            dataset3.open();
            controls.combo.setDataset(dataset3, "id", "caption");
            if (!dataset3.isEmpty()){
                controls.combo.setValue(dataset3.getValues(0).get("id"));
            }
            
        } catch (Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
        
    }
    
    public static void main(String[] args) throws Exception{
        DataModule.getInstance().open();
        JFrame frame = new JFrame("TestShift");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new TestShift());
        frame.pack();;
        frame.setVisible(true);
    }
    
}
