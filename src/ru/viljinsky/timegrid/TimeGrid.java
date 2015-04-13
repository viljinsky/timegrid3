/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.timegrid;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
    public void columnHeaderClick(int col);
    public void rowHeaderClick(int row);
    public void mouseOverCell(Cell cell);
}

abstract class TimeGridHeader extends JPanel{
    public static int HOR_HEIGHT = 30;
    public static int VER_WEDTH  = 60;
    
    public abstract int hitTest(int x,int y);
    
};

public class TimeGrid extends AbstractTimeGrid{
    
    ColumnHeader columnHeader = new ColumnHeader();
    RowHeader rowHeader = new RowHeader();
    
    public TimeGrid(int col,int row){
        super();
        setColCount(col);
        setRowCount(row);
        realign();
    }
    
    public JComponent getColumnHeader(){
        return columnHeader;
    }
    
    public JComponent getRowHeader(){
        return rowHeader;
    }

//    @Override
//    public Color getCellBackground(int col, int row) {
//        if (col%2==0)
//            return Color.LIGHT_GRAY;
//        else
//            return Color.WHITE;
//    }
    
    
    class ColumnHeader extends TimeGridHeader{
        public ColumnHeader(){
            setOpaque(true);
            setPreferredSize(new Dimension(800,HOR_HEIGHT));
            addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    TimeGrid.this.requestFocus();
                    int col = hitTest(e.getX(), e.getY());
                    if (col>=0)
                        columnHeaderClick(col);
                }
                
            });
        }
        
        public void setPrefferedWidth(int width){
            setPreferredSize(new Dimension(width,HOR_HEIGHT));
        }
        
        @Override
        public void paintComponent(Graphics g){
            Rectangle r =  new Rectangle(0,0,getWidth(),getHeight()) ;g.getClipBounds();
            g.setColor(getBackground());
            g.fillRect(r.x, r.y, r.width, r.height);
            g.setColor(Color.gray);
            g.drawLine(r.x, r.y+r.height-1, r.x+r.width,r.y+r.height-1);
            Rectangle r1 = new Rectangle(r);
            r1.height-=1;
            for (int i=0;i<getColumnCount();i++){
                r1.width=getColumnWidth(i);
                g.drawLine(r1.x, r1.y, r1.x, r1.y+r1.height);
                r1.x+=r1.width;
            }
            g.drawLine(r1.x, r1.y, r1.x, r1.y+r1.height);
        }
        
        @Override
        public int hitTest(int x,int y){
            Point p= new Point(x,y);
            Rectangle r = new Rectangle(0,0,10,HOR_HEIGHT);
            for (int col=0;col<colCount;col++){
                r.width=colWidths[col];
                if (r.contains(p))
                    return col;
                r.x+=r.width;
            }
            return -1;
        }
    }
    
    class RowHeader extends TimeGridHeader{
        public RowHeader(){
            setPreferredSize(new Dimension(VER_WEDTH,1000));
            setOpaque(true);
            addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    int row =hitTest(e.getX(),e.getY());
                    if (row>=0)
                        rowHeaderClick(row);
                }
            });
        }
        
        public void setPrefferedHeight(int height){
            setPreferredSize(new Dimension(VER_WEDTH,height));
        }
        
        @Override
        public void paintComponent(Graphics g){
            Rectangle r = new Rectangle(0,0,getWidth(),getHeight());//g.getClipBounds();
            g.setColor(getBackground());
            g.fillRect(r.x, r.y, r.width, r.height);
            g.setColor(Color.gray);
            g.drawLine(r.x+r.width-1, r.y, r.x+r.width-1, r.y+r.height);
            
            
            Rectangle r1 = new Rectangle(r);
            r1.width-=1;
            g.setColor(Color.gray);
            for (int i=0;i<getRowCount();i++){
                r1.height=getRowHeight(i);
                g.drawLine(r1.x, r1.y, r1.x+r1.width,  r1.y);
                r1.y+=r1.height;
            }
            g.drawLine(r1.x, r1.y, r1.x+r1.width,  r1.y);
            
        }

        @Override
        public int hitTest(int x, int y) {
            Rectangle r = new Rectangle(0,0,VER_WEDTH,10);
            Point p = new Point(x,y);
            for (int row=0;row<rowCount;row++){
                r.height=rowHeights[row];
                if (r.contains(p))
                    return row;
                r.y+=rowHeights[row];
            }
            return -1;
        }
    }
    
    
    @Override
    public void cellClick(int col,int row){
//        System.out.println(String.format("Cell click %d %d ", col,row));
    }
    
    @Override
    public void cellElementClick(CellElement ce){
//        System.out.println(String.format("Element click %s", ce.toString()));
    }
    
    @Override
    public void startDrag(int col,int row) throws Exception{
        System.out.println("Start Drag");
    }
    
    @Override
    public void stopDrag(int col,int row) throws Exception{
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
//        realign();
    }

    @Override
    public void removeElement(CellElement ce) {
        cells.remove(ce);
//        realign();
    }
    
    @Override
    public void columnHeaderClick(int col) {
        for (CellElement ce:cells){
            ce.selected =  (ce.col==col);
        }
        repaint();
    }

    @Override
    public void rowHeaderClick(int row) {
        for (CellElement ce:cells)
            ce.selected=ce.row==row;
        repaint();
    }
    
    
    
    protected void doCommand(String command){
        try{
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
        } catch (Exception e){
            JOptionPane.showMessageDialog(this, e.getMessage());
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
    

    public void delete() throws Exception{
        for (CellElement ce:cells.getSelected())
            cells.remove(ce);
    }
    
    public void clear() throws Exception{
        cells.clear();
    }
    
    public void load(){
    }
    
    public void save(){
    }


    public void close(){
        cells.clear();
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
        
        TimeGrid timeGrid = new TimeGrid(6,4);
        
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
