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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JPanel;
import ru.viljinsky.Dataset;
import ru.viljinsky.Values;
import ru.viljinsky.dialogs.BaseDialog;

/**
 *
 * @author вадик
 */

public class ShiftDialog extends BaseDialog{
    ShiftPanel drawPanel = new ShiftPanel();
    JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));

    
    
    public ShiftDialog(){
        super();
        JButton btn;
        btn = new JButton("SELECT_ALL");
        controls.add(btn);
        btn.addActionListener(this);
        
        btn = new JButton("UNSELECT_ALL");
        controls.add(btn);
        btn.addActionListener(this);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(drawPanel,BorderLayout.CENTER);
        panel.add(controls,BorderLayout.PAGE_END);
        
        getContentPane().add(panel,BorderLayout.CENTER);
        
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
//            System.out.println(n[0]+" "+n[1]);
        }
        
        for (Integer[] n:getAdded()){
//            System.out.println("ADDED"+n[0]+" "+n[1]);
        }
        
        for (Integer[] n:getRemoved()){
//            System.out.println("REMOVED"+n[0]+" "+n[1]);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command){
            case "SELECT_ALL":
//                System.out.println(command);
                drawPanel.selectAll();
                break;
            case "UNSELECT_ALL":
//                System.out.println(command);
                drawPanel.unSelectAll();
                break;
            default:
                super.actionPerformed(e);
        }
    }
    
    
    
}

/////////////////////////   SHIFT_PANEL ///////////////////////////////////////
class DBShiftPanel extends ShiftPanel{
    Dataset dataset;
    public void setDataset(Dataset dataset) throws Exception{
        this.dataset = dataset;
        Values values;
        cells.clear();
        if (dataset==null)
            return;
        for (int i=0;i<dataset.size();i++){
            values= dataset.getValues(i);
            cells.add(new Point(values.getInteger("day_id")-1,values.getInteger("bell_id")-1));
        }
        repaint();
    }
}

class ShiftPanel extends JPanel {
    FontMetrics fontMetrics;
    Set<Point> cells = new HashSet<>();
    Set<Point> oldValues = new HashSet<>();
    Integer COLUMN_HEADER_HEIGHT = 30;
    Integer ROW_HEADER_WIDHT = 100;
    Integer CELL_WIDTH = 25;
    Integer CELL_HEIGHT = 25;
    Integer dayCount = 7;
    Integer bellCount = 10;
    String[] columnHeaders;
    String[] rowHeaders;
    
    public ShiftPanel() {
        setPreferredSize(new Dimension(300, 300));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                hitTest(e.getX(), e.getY());
                repaint();
            }
        });
        columnHeaders= new String[]{"Пн","Вт","Ср","Чт","Пт","Cб","Вс"};
        rowHeaders= new String[]{"10-11","11-12","12-13","13-14","14-15","15-16","16-17","17-18","18-19"};
        dayCount=columnHeaders.length;
        bellCount=rowHeaders.length;
    }
    
    public void setDayList(){
    }
    
    public void setBellList(){
    }

    void selectAll() {
        cells.clear();
        for (int i=0;i<dayCount;i++)
            for (int j=0;j<bellCount;j++){
                cells.add(new Point(i,j));
            }
        repaint();
    }

    void unSelectAll() {
        cells.clear();
        repaint();
    }


    public void drawColumnHeader(Graphics g, int day) {
        String caption = columnHeaders[day];
        int h = fontMetrics.getHeight();
        int w = fontMetrics.stringWidth(caption);
        
        Rectangle r = getColumnHeaderBound(day);
        g.setColor(Color.gray);
        g.drawRect(r.x, r.y, r.width, r.height);
        g.setColor(Color.BLACK);
        g.drawString(caption, r.x+(r.width-w)/2, r.y+h);
    }

    public Rectangle getColumnHeaderBound(int day) {
        return new Rectangle(ROW_HEADER_WIDHT + day * CELL_WIDTH, 0, CELL_WIDTH, COLUMN_HEADER_HEIGHT);
    }

    public void drawRowHeader(Graphics g, int bell) {
        String caption = rowHeaders[bell];
        int h= fontMetrics.getHeight();
        int w=fontMetrics.stringWidth(caption);
        Rectangle r = getRowHeaderRectangle(bell);
        g.setColor(Color.gray);
        g.drawRect(r.x, r.y, r.width, r.height);
        g.setColor(Color.BLACK);
        g.drawString(caption, r.x+(r.width-w)/2, r.y+h);
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
        fontMetrics =g.getFontMetrics();
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
        Point cell = new Point(day, bell);
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
//        System.out.println(String.format("day:%d bell:%d", day, bell));
        Point cell = new Point(day, bell);
        if (cells.contains(cell)) {
            cells.remove(cell);
        } else {
            cells.add(cell);
        }
//        System.out.println(cells);
    }

    public void columnClick(int day) {
//        System.out.println("day =" + day);
        boolean b = cells.contains(new Point(day, 0));
        Point cell;
        for (int bell = 0; bell < bellCount; bell++) {
            cell = new Point(day, bell);
            if (b) {
                cells.remove(cell);
            } else {
                cells.add(cell);
            }
        }
    }

    public void rowClick(int bell) {
//        System.out.println("bell=" + bell);
        Point cell;
        boolean b = cells.contains(new Point(0, bell));
        for (int day = 0; day < dayCount; day++) {
            cell = new Point(day, bell);
            if (b) {
                cells.remove(cell);
            } else {
                cells.add(cell);
            }
        }
    }
    
    
    public List<Integer[]> getSelected(){
        List<Integer[]> result = new ArrayList<>();
        for (Point cell:cells){
            result.add(new Integer[]{cell.x,cell.y});
        }
        return result;
    }
    
    public void setSelected(List<Integer[]> list){
        cells.clear();
        for (Integer[] n:list){
            cells.add(new Point(n[0],n[1]));
        }
        oldValues = new HashSet<>(cells);
    }
    
    public List<Integer[]> getRemoved(){
        List<Integer[]> result = new ArrayList<>();
        for (Point cell:oldValues){
            if (!cells.contains(cell)){
                result.add(new Integer[]{cell.x,cell.y});
            }
        }
        return result;
    }
    
    public List<Integer[]> getAdded(){
        List<Integer[]> result = new ArrayList<>();
        for (Point cell:cells){
            if (!oldValues.contains(cell)){
                result.add(new Integer[]{cell.x,cell.y});
            }
        }
        return result;
    }
    
}
