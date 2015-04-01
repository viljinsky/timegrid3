/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.timegrid;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author вадик
 */

class Cell{
    int col;
    int row;
    public Cell(int col,int row){
        this.col=col;
        this.row=row;
    }
    @Override
    public String toString(){
        return String.format("col %d row %d",col,row);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj==null)
            return false;
        if (obj==this)
            return true;
        if (obj instanceof Cell){
            Cell o =(Cell)obj;
            return o.col==col && o.row==row;
        }
        return  false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + this.col;
        hash = 71 * hash + this.row;
        return hash;
    }
}

class Cells extends ArrayList<CellElement>{
    public CellElement addElement(int col,int row){
        CellElement element = new CellElement();
        element.col=col;
        element.row=row;
        this.add(element);
        return element;
    }
    
    public int elementCount(int col,int row){
        Integer result = 0;
        for (CellElement ce:this){
            if (ce.col==col && ce.row==row){
                result+=1;
            }
        }
        return result;
    }

    boolean isExists(int col, int row) {
        for (CellElement ce :this)
            if (ce.col==col && ce.row==row)
                return true;
        return false;
    }

    Set<CellElement> getCells(int col,int row){
        Set<CellElement> list = new HashSet<>();
        for (CellElement ce:this){
            if (ce.col==col && ce.row==row)
                list.add(ce);
        }
        return list;
    }

    public Set<CellElement> getSelected(){
        Set<CellElement> set = new HashSet<>();
        for (CellElement ce:this)
            if (ce.selected)
                set.add(ce);
        return set;
    }
    
    
    void setSelected(CellElement[] list) {
        for (CellElement element:this){
            element.selected = false;
        }
        for (CellElement ce:list){
            ce.selected=true;
        }
    }

    
}

class DragObject{
    Rectangle bound;
    CellElement element;
    public DragObject(CellElement element){
        this.element=element;
        bound = new Rectangle(element.bound);
    }
    public void draw(Graphics g){
        element.draw(g, bound);
    }
}


abstract class AbstractTimeGrid extends Container {
    Set<DragObject> dragObjects = null;
    protected Cells cells;
    int colCount = 7;
    int rowCount = 8;
    Integer[] rowHeights;
    Integer[] colWidths;
    int selectedRow = -1;
    int selectedCol = -1;
    int shadowCol = -1;
    int shadowRow = -1;
    int startX;
    int startY;
    boolean dragged = false;
    Cell overCell = null;
    
    
    public AbstractTimeGrid() {
//        setPreferredSize(new Dimension(800, 600));
        cells = new Cells();
        
        setColCount(6);

        setRowCount(3);

        addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                repaint();
            }

            @Override
            public void focusGained(FocusEvent e) {
                repaint();
            }

        });
        
        addMouseListener(new MouseAdapter() {
            
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocus();
                startX = e.getX();
                startY = e.getY();
                Cell cell = getCellOver(startX, startY);
                if (cell != null) {
                    selectedCol = cell.col;
                    selectedRow = cell.row;
                    cellClick(cell.col, cell.row);
                }
                if (!e.isControlDown()) {
                    for (CellElement ce : cells) {
                        ce.selected = false;
                    }
                }
                for (CellElement ce : cells) {
                    if (ce.hitTest(startX, startY)) {
                        ce.selected = !ce.selected;
                        cellElementClick(ce);
                    }
                }
                repaint();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                Cell cell = getCellOver(e.getX(), e.getY());
                if (cell == null) {
                    selectedCol = -1;
                    selectedRow = -1;
                } else {
                    if (dragged) {
                        stopDrag(cell.col, cell.row);
                    }
                    selectedCol = cell.col;
                    selectedRow = cell.row;
                }
                if (dragged) {
                    dragged = false;
                    dragObjects = null;
                }
                calcRowHeight();
                repaint();
            }

        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Cell cell = getCellOver(e.getX(), e.getY());
                if (cell != null) {
                    setOverCell(cell);
                }
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                
                Cell cell = getCellOver(e.getX(), e.getY());
                if (cell != null) {
                    setOverCell(cell);
                }
                
                
                if (!dragged) {
                    startDrag(selectedCol, selectedRow);
                    dragObjects = new HashSet<>();
                    for (CellElement ce:cells.getSelected()){
                            dragObjects.add(new DragObject(ce));
                    }
                    dragged = true;
                }
                int x = e.getX();
                int y = e.getY();
                drag(x - startX, y - startY);
                for (DragObject ce : dragObjects) {
                    ce.bound.x += x - startX;
                    ce.bound.y += y - startY;
                }
                startX = x;
                startY = y;
                repaint();
            }
        });
        
        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                switch(e.getKeyCode()){
                    case KeyEvent.VK_UP:
                        System.out.println("UP");
                        if (selectedRow>0)
                            selectedRow-=1;
                        break;
                    case KeyEvent.VK_DOWN:
                        System.out.println("DOWN");
                        if (selectedRow<rowCount-1)
                            selectedRow+=1;
                        break;
                    case KeyEvent.VK_LEFT:
                        System.out.println("LEFT");
                        if (selectedCol>0)
                            selectedCol-=1;
                        break;
                    case KeyEvent.VK_RIGHT:
                        System.out.println("RIGHT");
                        if (selectedCol<colCount-1)
                            selectedCol+=1;
                        break;
                    case KeyEvent.VK_DELETE:
                           for (CellElement ce:cells.getSelected()){
                               removeElement(ce);
                           }
                        break;
//                    case KeyEvent.VK_INSERT:
//                        break;
                }
                for (CellElement ce:cells){
                    if (e.isControlDown()){
                         if (ce.row==selectedRow && ce.col==selectedCol){
                             ce.selected=true;
                         }
                    }  else
                       ce.selected= (ce.row==selectedRow && ce.col==selectedCol);
                }
                repaint();
            }
            
        });
    }
    
    

    private Integer getMaxRowCount(int row) {
        Integer result = 1;
        Integer[] count = new Integer[colCount];
        for (int i = 0; i < count.length; i++) {
            count[i] = 0;
        }
        for (CellElement ce : cells) {
            if (ce.row == row) {
                count[ce.col] += 1;
            }
        }
        for (int i = 0; i < count.length; i++) {
            if (count[i] > result) {
                result = count[i];
            }
        }
        return result;
    }

    /**
     * Установка количества строк
     * @param rowCount количество строк
     */
    public void setRowCount(Integer rowCount){
        this.rowCount=rowCount;
        rowHeights = new Integer[rowCount];
        calcRowHeight();
    }
    
    /**
     * Установка количества колонок
     * @param colCount 
     */
    public void setColCount(Integer colCount){
        this.colCount=colCount;
        colWidths= new Integer[colCount];
        calcColWidth();
    }
    
    public void calcRowHeight() {
        for (int row = 0; row < rowCount; row++) {
            rowHeights[row] = getMaxRowCount(row) * CellElement.HEIGHT;
        }
    }

    public Integer calcColWidth(){
        Integer result = 0;
        for (int i=0;i<colWidths.length;i++){
            colWidths[i]= CellElement.WIDTH;
            result += colWidths[i];
        }
        return result;
    }
    
    public void realign(){
        calcColWidth();
        calcRowHeight();
        repaint();
    }
    
    /**
     * Установка строки и колонки ячейки на которой находится мышь
     * @param cell 
     */
    public void setOverCell(Cell cell) {
        if (!cell.equals(overCell)) {
            overCell = cell;
            System.out.println(overCell);
        }
    }

    /**
     * Получение стрки и колонки ячейки над которой находится мышь
     * @param x коорд. мышы
     * @param y коорд. мышы
     * @return ячейка Cell
     */
    public Cell getCellOver(int x, int y) {
        for (int col = 0; col < colCount; col++) {
            for (int row = 0; row < rowCount; row++) {
                if (getBound(col, row).contains(x, y)) {
                    return new Cell(col, row);
                }
            }
        }
        return null;
    }

    /**
     * Получение активной ячейки
     * @return активная ячейка Cell
     */
    public Cell getSelectedCell() {
        if (selectedCol<0 || selectedRow<0)
            return null;
        else
            return new Cell(selectedCol, selectedRow);
    }

    /**
     * Получение границ яцейки  встроке row и колонке col
     * @param col  колонка
     * @param row  строка
     * @return  возвращает Rectangle если ячейка существует в противном случае null
     */
    protected Rectangle getBound(int col, int row) {
        Rectangle result = new Rectangle();
        result.x = col * CellElement.WIDTH;
        result.width = CellElement.WIDTH;
        result.y = 0;
        for (int i = 0; i < row; i++) {
            result.y += rowHeights[i];
        }
        result.height = rowHeights[row];
        return result;
    }

    private boolean CellElementIsDragged(CellElement ce) {
        if (dragObjects == null) {
            return false;
        }
        for (DragObject dro : dragObjects) {
            if (dro.element == ce) {
                return true;
            }
        }
        return false;
    }

    public void drawCell(Graphics g, int col, int row) {
        Rectangle r;
        Color color = Color.pink;
        r = getBound(col, row);
        g.setColor(Color.red);
        g.drawRect(r.x, r.y, r.width, r.height);
        if (col == selectedCol && row == selectedRow && isFocusOwner()) {
            color = Color.white;
        }
        if (overCell != null) {
            if (col == overCell.col && row == overCell.row) {
                color = Color.yellow;
            }
        }
        g.setColor(color);
        g.fillRect(r.x + 1, r.y + 1, r.width - 2, r.height - 2);
//        if (isFocusOwner()){
//            color=Color.black;
//            g.drawRect(r.x+3, r.y+3, r.width-6, r.height-6);
//        }
        //----------------------------------
        if (cells.isExists(col, row)) {
            Set<CellElement> list = cells.getCells(col, row);
            Rectangle r1 = new Rectangle(r);
            r1.height = r.height / list.size();
            for (CellElement ce : list) {
                if (!CellElementIsDragged(ce)) {
                    ce.bound = new Rectangle(r1.x + 1, r1.y + 1, r1.width - 2, r1.height - 2);
                    ce.draw(g, ce.bound);
                    r1.y += r1.height;
                }
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        for (int col = 0; col < colCount; col++) {
            for (int row = 0; row < rowCount; row++) {
                drawCell(g, col, row);
            }
        }
        if (dragObjects != null) {
            for (DragObject dro : dragObjects) {
                dro.draw(g);
            }
        }
    }

    
    public Set<CellElement> getSelectedElements(){
        return cells.getSelected();
    }
    
    public abstract void cellClick(int col, int row);

    public abstract void addElement(CellElement ce);
    
    public abstract void removeElement(CellElement ce);
    
    public abstract void cellElementClick(CellElement ce);

    public abstract void startDrag(int col, int row);

    public abstract void stopDrag(int col, int row);

    public abstract void drag(int dx, int dy);
    
}
