/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import ru.viljinsky.dialogs.BaseDialog;

/**
 *
 * @author вадик
 */

public class ShiftDialog extends BaseDialog{
    ShiftPanel drawPanel = new ShiftPanel();

    
    public ShiftDialog() {
        super();
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        getContentPane().add(panel,BorderLayout.CENTER);
        panel.add(drawPanel);
    }
    
    public void setSelected(List<Integer[]> list){
        drawPanel.setSelected(list);
    }
    
    public List<Integer[]> getSelected(){
        return drawPanel.getSelected();
    }
    
    public List<Integer[]> getAdded(){
        return drawPanel.getAdded();
    }
    
    public List<Integer[]> getRemoved(){
        return drawPanel.getRemoved();
    }
    
    @Override
    public void doOnEntry() throws Exception {
        for (Integer[] n:getSelected()){
            System.out.println(n[0]+" "+n[1]);
        }
        
        for (Integer[] n:getAdded()){
            System.out.println("ADDED"+n[0]+" "+n[1]);
        }
        
        for (Integer[] n:getRemoved()){
            System.out.println("REMOVED"+n[0]+" "+n[1]);
        }
    }
    
}


class ShiftPanel extends JPanel {
    Set<Cell> cells = new HashSet<>();
    Set<Cell> oldValues = new HashSet<>();
    Integer COLUMN_HEADER_HEIGHT = 30;
    Integer ROW_HEADER_WIDHT = 100;
    Integer CELL_WIDTH = 25;
    Integer CELL_HEIGHT = 25;
    Integer dayCount = 7;
    Integer bellCount = 10;

    class Cell {

        Integer day;
        Integer bell;

        public Cell(int day, int bell) {
            this.day = day;
            this.bell = bell;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj instanceof Cell) {
                Cell c = (Cell) obj;
                return c.day == day && c.bell == bell;
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return day * 100 + bell;
        }

        @Override
        public String toString() {
            return String.format("%d %d", day, bell);
        }
    }

    public ShiftPanel() {
        setPreferredSize(new Dimension(300, 300));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                hitTest(e.getX(), e.getY());
                repaint();
            }
        });
    }

    public void drawColumnHeader(Graphics g, int day) {
        Rectangle r = getColumnHeaderBound(day);
        g.setColor(Color.gray);
        g.drawRect(r.x, r.y, r.width, r.height);
    }

    public Rectangle getColumnHeaderBound(int day) {
        return new Rectangle(ROW_HEADER_WIDHT + day * CELL_WIDTH, 0, CELL_WIDTH, COLUMN_HEADER_HEIGHT);
    }

    public void drawRowHeader(Graphics g, int bell) {
        Rectangle r = getRowHeaderRectangle(bell);
        g.setColor(Color.gray);
        g.drawRect(r.x, r.y, r.width, r.height);
    }

    public Rectangle getRowHeaderRectangle(int bell) {
        return new Rectangle(0, COLUMN_HEADER_HEIGHT + bell * CELL_HEIGHT, ROW_HEADER_WIDHT, CELL_HEIGHT);
    }

    public void drawCell(Graphics g, int day, int bell) {
        g.setColor(Color.gray);
        Rectangle r = getCellBound(day, bell);
        g.drawRect(r.x, r.y, r.width, r.height);
        if (isExists(day, bell)) {
            g.setColor(Color.red);
            g.fillRect(r.x + 3, r.y + 3, r.width - 6, r.height - 6);
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        // drow columnHeader
        for (int day = 0; day < dayCount; day++) {
            drawColumnHeader(g, day);
        }
        // draw rawHeader
        for (int bell = 0; bell < bellCount; bell++) {
            drawRowHeader(g, bell);
        }
        // draw cells
        for (int day = 0; day < dayCount; day++) {
            for (int bell = 0; bell < bellCount; bell++) {
                drawCell(g, day, bell);
            }
        }
    }

    public boolean isExists(int day, int bell) {
        Cell cell = new Cell(day, bell);
        return cells.contains(cell);
    }

    public boolean hitTest(int x, int y) {
        Rectangle r;
        Point p = new Point(x, y);
        for (int day = 0; day < dayCount; day++) {
            if (getColumnHeaderBound(day).contains(p)) {
                columnClick(day);
            }
        }
        for (int bell = 0; bell < bellCount; bell++) {
            if (getRowHeaderRectangle(bell).contains(p)) {
                rowClick(bell);
            }
        }
        for (int day = 0; day < dayCount; day++) {
            for (int bell = 0; bell < bellCount; bell++) {
                r = getCellBound(day, bell);
                if (r.contains(p)) {
                    clickCell(day, bell);
                }
            }
        }
        return false;
    }

    public Rectangle getCellBound(int day, int bell) {
        return new Rectangle(ROW_HEADER_WIDHT + day * CELL_WIDTH, COLUMN_HEADER_HEIGHT + bell * CELL_HEIGHT, CELL_WIDTH, CELL_WIDTH);
    }

    public void clickCell(int day, int bell) {
        System.out.println(String.format("day:%d bell:%d", day, bell));
        Cell cell = new Cell(day, bell);
        if (cells.contains(cell)) {
            cells.remove(cell);
        } else {
            cells.add(cell);
        }
        System.out.println(cells);
    }

    public void columnClick(int day) {
        System.out.println("day =" + day);
        boolean b = cells.contains(new Cell(day, 0));
        Cell cell;
        for (int bell = 0; bell < bellCount; bell++) {
            cell = new Cell(day, bell);
            if (b) {
                cells.remove(cell);
            } else {
                cells.add(cell);
            }
        }
    }

    public void rowClick(int bell) {
        System.out.println("bell=" + bell);
        Cell cell;
        boolean b = cells.contains(new Cell(0, bell));
        for (int day = 0; day < dayCount; day++) {
            cell = new Cell(day, bell);
            if (b) {
                cells.remove(cell);
            } else {
                cells.add(cell);
            }
        }
    }
    
    
    public List<Integer[]> getSelected(){
        List<Integer[]> result = new ArrayList<>();
        for (Cell cell:cells){
            result.add(new Integer[]{cell.day,cell.bell});
        }
        return result;
    }
    
    public void setSelected(List<Integer[]> list){
        cells.clear();
        for (Integer[] n:list){
            cells.add(new Cell(n[0],n[1]));
        }
        oldValues = new HashSet<>(cells);
    }
    
    public List<Integer[]> getRemoved(){
        List<Integer[]> result = new ArrayList<>();
        for (Cell cell:oldValues){
            if (!cells.contains(cell)){
                result.add(new Integer[]{cell.day,cell.bell});
            }
        }
        return result;
    }
    
    public List<Integer[]> getAdded(){
        List<Integer[]> result = new ArrayList<>();
        for (Cell cell:cells){
            if (!oldValues.contains(cell)){
                result.add(new Integer[]{cell.day,cell.bell});
            }
        }
        return result;
    }
    
}
