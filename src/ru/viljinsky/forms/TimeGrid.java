/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.AbstractTableModel;
import ru.viljinsky.DataModule;
import ru.viljinsky.Dataset;

/**
 *
 * @author вадик
 */

class TimeGridModel extends AbstractTableModel{
    String[] day_list;
    String[] bell_list;
    Dataset dataset;
    
    public TimeGridModel(){
        DataModule dataModule =DataModule.getInstance();
        Dataset d ;
        try{
        dataset = dataModule.getSQLDataset("select * from shift_detail where shift_id=3;");
        dataset.open();
            
        d = dataModule.getDataset("day_list");
        d.open();               
        day_list=new String[d.size()];        
        for (int i=0;i<day_list.length;i++){
            day_list[i]=d.getValues(i).get("day_caption").toString();
        }
        
        d = dataModule.getDataset("bell_list");
        d.open();
        bell_list=new String[d.size()];
        for (int i=0;i<bell_list.length;i++){
            bell_list[i] = d.getValues(i).get("time_start").toString();
        }
        
        } catch (Exception e){
            e.printStackTrace();
        }
        
        
    }

    @Override
    public int getRowCount() {
        return day_list.length;
    }

    @Override
    public int getColumnCount() {
        return bell_list.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return new Boolean(true);
    }
}
public class TimeGrid  extends JTable{
    TimeGridModel model;
    public TimeGrid(){
        model = new TimeGridModel();
        setModel(model);
        getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()){
                    System.out.println(getSelectedRow()+"   "+getSelectedColumn());
                }
            }
        });
        
        getColumnModel().addColumnModelListener(new TableColumnModelListener() {

            @Override
            public void columnAdded(TableColumnModelEvent e) {}

            @Override
            public void columnRemoved(TableColumnModelEvent e) {}

            @Override
            public void columnMoved(TableColumnModelEvent e) { }

            @Override
            public void columnMarginChanged(ChangeEvent e) {}

            @Override
            public void columnSelectionChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()){
                    System.out.println(getSelectedRow()+" + "+getSelectedColumn());
                }
            }
        });
    }
    
    public static void main(String[] args) throws Exception{
        DataModule.getInstance().open();
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new JScrollPane(new TimeGrid()));
        frame.pack();
        frame.setVisible(true);
    }
    
}
