/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.timegrid;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.TransferHandler;

/**
 *
 * @author вадик
 */

public class Test21 extends JFrame{
    TG timeGrid = new TG();
    JList list = new JList(new String[]{"One","Two","Three"});
    
    class TG extends TimeGrid{
        int col0,row0;


        @Override
        public void stopDrag(int col, int row) {
            for (CellElement ce:getSelectedElements()){
                    System.out.println(ce.toString());
                    ce.moveCell(col-col0, row-row0);
            }
        }
        
        @Override
        public void startDrag(int col, int row) {
            System.out.println("Start");
            col0=col;row0=row;
            for (CellElement ce:getSelectedElements()){
                     
                    System.out.println(ce.toString());
            }
            
        }

        @Override
        public void rowHeaderClick(int row) {
            JOptionPane.showMessageDialog(rootPane, "rowClick "+row);
        }

        @Override
        public void columnHeaderClick(int col) {
            JOptionPane.showMessageDialog(rootPane, "columClick "+col);
        }
        
        

        @Override
        public void cellElementClick(CellElement ce) {
            System.out.println("->"+ce.col);
        }
        
        

    }
    
    public Test21(){
        timeGrid.setRowCount(5);
        timeGrid.setColCount(8);
        timeGrid.setFocusable(true);
        
        list.setPreferredSize(new Dimension(120,120));
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        JScrollPane scrollPane = new JScrollPane(timeGrid);
        scrollPane.setColumnHeaderView(timeGrid.getColumnHeader());
        scrollPane.setRowHeaderView(timeGrid.getRowHeader());
        splitPane.setRightComponent(scrollPane);
        splitPane.setLeftComponent(list);
        splitPane.setPreferredSize(new Dimension(600,400));
        setContentPane(splitPane);
        
        list.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount()==2){
                    CellElement ce = new CellElement();
                    if (timeGrid.getSelectedCell()==null)
                        JOptionPane.showMessageDialog(Test21.this, "not selected");
                    else{                                
                        ce.setCell(timeGrid.selectedCol,timeGrid.selectedRow);
                        timeGrid.addElement(ce);
                        timeGrid.repaint();
                    }
                }   
                  
            }

        });
        
        
    }
    
    public static void main(String[] args){
        Test21 frame = new Test21();
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
