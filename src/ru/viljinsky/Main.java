/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

/**
 *
 * @author вадик
 */
//------------------------------------------------------------------------------



public class Main extends JFrame{
    JTabbedPane tabs = new JTabbedPane();
    
    DataModule dataModule = DataModule.getInstance();
    public Main(){
        Container content = getContentPane();
        content.setPreferredSize(new Dimension(800,600));
        content.setLayout(new BorderLayout());
        content.add(tabs);
        
    }
    public void open(){
        Grid grid;
        Dataset dataset ;
        try{
            dataModule.open();
            for (DatasetInfo info :dataModule.infoList){
//                if (!info.tableName.equals("depart"))
//                    continue;
                dataset = dataModule.getTable(info.tableName);
                dataset.open();
                grid = new Grid();
                grid.owner=Main.this;
                grid.setDataset(dataset);
                tabs.addTab(dataset.getTableName(), new JScrollPane(grid));
                
                for (int i=0;i<dataset.getColumnCount();i++){
                    System.out.println(dataset.getColumn(i).toString()+"\n");
                }
                
            }

        } catch (Exception e){
            JOptionPane.showMessageDialog(rootPane, e.getMessage());
        }
    }
    
    public static void main(String[] args){
        Main frame = new Main();
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.open();
    }
}
