/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.timegrid;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;

/**
 *
 * @author вадик
 */

interface ITimeGrid{
    public void setRowCount(Integer rowCount);
    public void setColCount(Integer colCount);
    
    public void elementClick(CellElement element);
    public void elementStartMove(CellElement element);
    public void elementStopMove(CellElement element);
    
    public void cellClick(int col,int row);
    public void mouseOverCell(Cell cell);
}

public class TimeGrid extends AbstractTimeGrid{
    
    @Override
    public void cellClick(int col,int row){
//        System.out.println(String.format("Cell click %d %d ", col,row));
    }
    
    @Override
    public void cellElementClick(CellElement ce){
//        System.out.println(String.format("Element click %s", ce.toString()));
    }
    
    @Override
    public void startDrag(int col,int row){
        System.out.println("Start Drag");
    }
    
    @Override
    public void stopDrag(int col,int row){
        Cell cell = getSelectedCell();
        for (CellElement ce:cells.getSelected()){
            ce.col += col-cell.col;
            ce.row += row-cell.row;
        }
    }
    
    @Override
    public void drag(int dx ,int dy){
//        System.out.println(String.format("%d %d",dx,dy));
    }

    @Override
    public void addElement(CellElement ce) {
        cells.add(ce);
    }

    @Override
    public void removeElement(CellElement ce) {
        cells.remove(ce);
    }
    
    
    
    
    protected void doCommand(String command){
        switch (command){
            case "add":
                add();
                break;
            case "delete":
                delete();
                break;
            case "load":
                load();
                break;
            case "save":
                save();
                break;
            case "clear":
                clear();
                break;
                
        }
        System.out.println("command : '"+command+"'");
        repaint();
    }
    
    public void add(){
        CellElement ce = new CellElement();
        ce.col = selectedCol;
        ce.row = selectedRow;
        cells.add(ce);
        cells.setSelected(new CellElement[]{ce});
    }
    

    public void delete(){
        
        for (CellElement ce:cells.getSelected())
            cells.remove(ce);
//        List<CellElement> deleted = new ArrayList<>();
//        for (CellElement ce:cells){
//            if (ce.selected){
//                deleted.add(ce);
//            }
//        }
//        for (CellElement ce:deleted){
//            cells.remove(ce);
//        }
        
    }
    
    public void clear(){
        cells.clear();
    }
    
    
//    public void addElement(CellElement element){
//        cells.add(element);
//    }
    
    
    public void load(){
    }
    
    public void save(){
    }

    
    
    class TimeGridAction extends AbstractAction{

        public TimeGridAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            doCommand(e.getActionCommand());
        }
    }
    
    public JMenu getGridMenu() {
        JMenu menu = new JMenu("Grid");
        
        menu.add(new TimeGridAction("save"));
        menu.add(new TimeGridAction("load"));
        menu.add(new TimeGridAction("clear"));
        
        return menu;
    }
    
    public JMenu getCellMenu(){
        JMenu menu = new JMenu("Cells");
        menu.add(new TimeGridAction("add"));
        menu.add(new TimeGridAction("delete"));
        menu.add(new TimeGridAction("enabled"));
        menu.add(new TimeGridAction("selectAll"));
        menu.add(new TimeGridAction("deselectAll"));
        
        return menu;
    }
    
    public static void main(String[] args){
        TimeGrid timeGrid = new TimeGrid();
        timeGrid.setPreferredSize(new Dimension(timeGrid.colCount*CellElement.WIDTH,timeGrid.rowCount*CellElement.HEIGHT));
        
        JFrame frame = new JFrame("Test 'TimeGrid'");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new JScrollPane(timeGrid));
        timeGrid.addElement(new CellElement());
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(timeGrid.getGridMenu());
        menuBar.add(timeGrid.getCellMenu());
        frame.setJMenuBar(menuBar);
        
        frame.pack();
        frame.setVisible(true);
    }
    
}
