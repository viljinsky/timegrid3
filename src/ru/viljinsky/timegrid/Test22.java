/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.timegrid;

import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;

/**
 *
 * @author вадик
 */
public class Test22 extends JFrame{
    TG timeGrid ;
    
    class TG extends AbstractTimeGrid{

        public TG() {
            super();
            setColCount(10);
//            setRowCount(10);
        }
        
        

        @Override
        public void cellClick(int col, int row) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void addElement(CellElement ce) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void removeElement(CellElement ce) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void cellElementClick(CellElement ce) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void startDrag(int col, int row) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void stopDrag(int col, int row) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void drag(int dx, int dy) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }


        @Override
        public void columnHeaderClick(int col) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void rowHeaderClick(int row) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
    public Test22(){
        timeGrid = new TG();
        JScrollPane scrollPane = new JScrollPane(timeGrid);
        scrollPane.setPreferredSize(new Dimension(800, 600));
//        scrollPane.setViewportView(timeGrid);
        scrollPane.getViewport().setOpaque(false);
        setContentPane(scrollPane);
    }
    
    public static void main(String[] args){
        Test22 frame = new Test22();
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
//        frame.setPreferredSize(new Dimension(500, 400));
        frame.pack();
        frame.setVisible(true);
        
    }
}
