/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author вадик
 */
public class TestSQL extends JFrame {
    DataModule dataModule = DataModule.getInstance();
    Grid grid;
    public TestSQL(){
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(800,600));
        grid = new Grid();
        grid.owner=this;
        panel.add(new JScrollPane(grid));
        setContentPane(panel);
    }
    
    public void open(){
        try{
        for (String tableName :dataModule.getTableNames()){
            System.out.println(tableName);
        }
            Dataset dataset = dataModule.getQuery("select * from teacher inner join profile on teacher.profile_id=profile.id");
            dataset.open();
            grid.setDataset(dataset);
            for (int i=0;i<dataset.getColumnCount();i++){
                System.out.println(dataset.getColumn(i));
            }
        } catch (Exception e){
            JOptionPane.showMessageDialog(rootPane, e.getMessage());
        }
        
    }
    
    public static void main(String[] args){
        try{
            DataModule.getInstance().open();
            TestSQL frame = new TestSQL();
            frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
            frame.open();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
}
