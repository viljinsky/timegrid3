/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import ru.viljinsky.DataModule;
import ru.viljinsky.Dataset;
import ru.viljinsky.Grid;
import ru.viljinsky.IDataModule;

/**
 *
 * @author вадик
 */
public class Main3  extends JFrame{
    IDataModule dataModule = DataModule.getInstance();
    Grid grid1;
    Grid grid2;
    JTabbedPane tabs = new JTabbedPane();
    public Main3(){
        JPanel panel =new JPanel();
        panel.setPreferredSize(new Dimension(800,600));
        panel.setLayout(new BorderLayout());
        grid1 = new Grid();
        grid2 = new Grid();
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(new JScrollPane(grid1));
        splitPane.setBottomComponent(tabs);
        splitPane.setResizeWeight(0.5);
        panel.add(splitPane);
        setContentPane(panel);
        
    }
    
    public void open(){
        try{
            Dataset dataset = dataModule.getDataset("teacher");
            dataset.open();
            grid1.setDataset(dataset);
            
            Map<String,String> childs = dataset.getDetails();
            for (String tableName:childs.keySet()){
            
                Grid grid = new Grid();
                dataset = dataModule.getDataset(tableName);
                dataset.open();
                grid.setDataset(dataset);
                tabs.addTab(tableName, new JScrollPane(grid));
            }
            
            
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) throws Exception{
        DataModule.getInstance().open();
        Main3 frame = new Main3();
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.open();
                
    }
    
}
