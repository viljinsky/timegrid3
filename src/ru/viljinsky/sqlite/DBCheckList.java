/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.sqlite;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author вадик
 */
public class DBCheckList extends JPanel{
    Grid grid = new Grid();
    public DBCheckList(){
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(400,200));
        add(new JScrollPane(grid));
    }
    
    public void setDataset(Dataset dataset){
        try{
            grid.setDataset(dataset);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    public Values[] getSelected(){
        Set<Values> result = new HashSet<>();
        for (int row:grid.getSelectedRows()){
            result.add(grid.getDataset().getValues(row));
        }
        return result.toArray(new Values[result.size()]);
    }
    
}
