/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.timegrid;

import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.JPanel;



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

public abstract class AbstractTimeGrid extends JPanel {
    Set<DragObject> dragObjects = null;
    protected Cells cells;
    int colCount = 7;
    int rowCount = 8;
    Integer[] rowHeights;
    Integer[] colWidths;
    int selectedRow = -1;
    protected int width=300;
    protected int height=500;
    int selectedCol = -1;
    int shadowCol = -1;
    int shadowRow = -1;
    int startX;
    int startY;
    boolean dragged = false;
    Cell overCell = null;
    
    
    TimeGridHeader columnHeader;
    TimeGridHeader rowHeader;
    
    public Cells getCells(){
        return cells;
    }

    public int getSelectedRow() {
        return selectedRow;
    }
    
    /**
     * Определяет имеется хотябы один элемент в ячейки col row
     * @param col колонка
     * @param row строка
     * @return аозвращает true если есть хотябы один элемент в ячейке col row
     */
    public boolean isEmpty(int col,int row){
        return !cells.isExists(col, row);
    }
    
    /**
     * Определяет наличие эементов в сетке
     * @return  true если нет ни одного элемета в противном случае false
     */
    public boolean isEmpty(){
        return cells.isEmpty();
    }


    public int getSelectedCol() {
        return selectedCol;
    }
    
    public AbstractTimeGrid() {
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
                        try{
                            stopDrag(cell.col, cell.row);
                        } catch (Exception p){
                        }
                    }
                    selectedCol = cell.col;
                    selectedRow = cell.row;
                }
                if (dragged) {
                    dragged = false;
                    dragObjects = null;
                }
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
                    try{
                        startDrag(selectedCol, selectedRow);
                        dragObjects = new HashSet<>();
                        for (CellElement ce:cells.getSelected()){
                            if (allowStartDrag(ce))
                                dragObjects.add(new DragObject(ce));
                        }
                        
                    } catch (Exception p){
                        p.printStackTrace();
                    } finally {
                        dragged = true;
                    }
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
    
    /**
     * Нужно проверить закреплё или нет элемент
     * а также не состалено ли расписания для указанного элемента
     * @param element
     * @return 
     */
    public boolean allowStartDrag(CellElement element){
        return true;
    }
    
    public Integer calcRowHeight() {
        Integer result = 0;
        for (int row = 0; row < rowCount; row++) {
            rowHeights[row] = getMaxRowCount(row) * CellElement.HEIGHT;
            result += rowHeights[row];
        }
        return result;
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
        width = calcColWidth();
        height =calcRowHeight();
        setPreferredSize(new Dimension(width, height));
        rowHeader.setPrefferedHeight(height);
        columnHeader.setPrefferedWidth(width);
        revalidate();
        rowHeader.repaint();
        columnHeader.repaint();
        repaint();
    }
    
    /**
     * Установка строки и колонки ячейки на которой находится мышь
     * @param cell 
     */
    public void setOverCell(Cell cell) {
        if (!cell.equals(overCell)) {
            overCell = cell;
//            System.out.println(overCell);
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
    
    public void setSelectedCell(Cell cell){
        Rectangle r = getBound(cell.col, cell.row);
        scrollRectToVisible(r);
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

    public  Color getCellBackground(int col,int row){
//        return Color.ORANGE;
        return Color.LIGHT_GRAY;
    }
    
    public void drawCell(Graphics g, int col, int row) {
        Rectangle r;
        Color color = getCellBackground(col, row);
        r = getBound(col, row);
        g.setColor(Color.gray);
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
    
    public int getColumnCount(){
        return colCount;
    }
    public int getColumnWidth(int columnIndex){
        return colWidths[columnIndex];
    }
    
    public int getRowCount(){
        return rowCount;
    }
    public int getRowHeight(int rowIndex){
        return rowHeights[rowIndex];
    }
    
    public abstract void cellClick(int col, int row);

    public abstract void addElement(CellElement ce);
    
    public abstract void removeElement(CellElement ce);
    
    public abstract void cellElementClick(CellElement ce);

    public abstract void startDrag(int col, int row) throws Exception;

    public abstract void stopDrag(int col, int row) throws Exception;

    public abstract void drag(int dx, int dy);
    
    public abstract void columnHeaderClick(int col);
    
    public abstract void rowHeaderClick(int row);
    
}
