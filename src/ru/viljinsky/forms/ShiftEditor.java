/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
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
import ru.viljinsky.sqlite.DataModule;
import ru.viljinsky.sqlite.Dataset;
import ru.viljinsky.sqlite.Recordset;
import ru.viljinsky.sqlite.Values;

abstract class AbstractShiftPanel extends JPanel{
    
    Boolean allowEdit = false;
    Boolean hasChange = false;
    
    public static final Integer CELL_WIDTH=30;
    public static final Integer CELL_HEIGHT=30;
    public static final Integer COLUMN_HEADER_HEIGHT=18;
    public static final Integer ROW_HEADER_WIDTH = 60;
    
    public abstract void rowClick(int row);
    public abstract void columnClick(int col);
    public abstract void cellClick(int col ,int row);
    Rows rows = new Rows();
    Columns columns = new Columns();
    Cells cells = new Cells();
    Set<Point> oldPoints = null;
    
    
    
    public boolean isHasChange(){
        return hasChange;
    }
    
    public void setAllowEdit(boolean value){
        allowEdit = value;
        if (allowEdit){
            hasChange=false;
            oldPoints = new HashSet<>(cells.points);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        } else
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    public boolean isAllowEdit(){
        return allowEdit;
    }
    
    public void setRowHeader(String[] headers){
        rows.headers=headers;
    }
    
    public void setColumnHeaders(String[] header){
        columns.headers=header;
    }
    
    public void setPoints(Set<Point> points){
        cells.points=points;
        oldPoints = new HashSet<>(points);
        repaint();
    }
    
    public Set<Point> getOldPoints(){
        return oldPoints;
    }
    
    public Set<Point> getAdded(){
        Set<Point> result = new HashSet<>();
        for (Point p : cells.points){
            if (!oldPoints.contains(p))
                result.add(p);
        }
        return result;
    }
    
    public Set<Point> getRemoved(){
        Set<Point> result = new HashSet<>();
        for (Point p : oldPoints){
            if (!cells.points.contains(p))
                result.add(p);
        }
        return result;
    }
    
    class Rows{
        String[] headers=null;
        Rectangle getBound(){
            return null;
        }
        int getCount(){
            return headers.length;
        }
        
        public void draw(Graphics g){
            int w,h;
            h=g.getFontMetrics().getHeight() ;w=10;
            h = (CELL_HEIGHT+h)/2;
            Rectangle bound = new Rectangle(0,COLUMN_HEADER_HEIGHT,ROW_HEADER_WIDTH,CELL_HEIGHT);
            g.setColor(Color.black);
            for (int i=0;i<headers.length;i++){
                g.drawRect(bound.x, bound.y, bound.width, bound.height);
                g.drawString(headers[i], bound.x+w,bound.y+h);
                bound.y+=bound.height;
            }
        }
        public boolean hitTest(int row,int x,int y){
            Rectangle bound = new Rectangle(0,COLUMN_HEADER_HEIGHT+row*CELL_HEIGHT,ROW_HEADER_WIDTH,CELL_HEIGHT);
            return bound.contains(x, y);
        }
    }
    
    class Columns{
        String[] headers=null;
        int getCount(){
            return headers.length;
        }
        
        public void draw(Graphics g){
            int w,h;
            h=g.getFontMetrics().getHeight();w=10;
            h = (COLUMN_HEADER_HEIGHT + h)/2;
            Rectangle bound = new Rectangle(ROW_HEADER_WIDTH,0,CELL_WIDTH,COLUMN_HEADER_HEIGHT);
            for (int i=0;i<headers.length;i++){
                g.drawRect(bound.x,bound.y,bound.width,bound.height);
                g.drawString(headers[i], bound.x+w,bound.y+h-3);
                bound.x+=bound.width;
            }
        }
        
        public boolean hitTest(int col,int x,int y){
            Rectangle bound = new Rectangle(ROW_HEADER_WIDTH+col*CELL_WIDTH,0,CELL_WIDTH, COLUMN_HEADER_HEIGHT);
            return bound.contains(x, y);
        }
    }
    
    public void drawCell(Graphics g,Rectangle bound,int col,int row){
        Point p;
        g.setColor(Color.black);
        g.drawRect(bound.x,bound.y,bound.width,bound.height);
        p = new Point(col,row);
        if (cells.points.contains(p)){
            g.setColor(Color.green);
        } else {
            g.setColor(Color.white);
        }
        g.fillRect(bound.x+1,bound.y+1,bound.width-1,bound.height-1);
    }
    
    class Cells{
        Set<Point> points = new HashSet<>();
        
        public void draw(Graphics g){
            for (int row=0;row<rows.getCount();row++)
                for (int col=0;col<columns.getCount();col++){
                    Rectangle bound = new Rectangle(ROW_HEADER_WIDTH + col*CELL_WIDTH,COLUMN_HEADER_HEIGHT + row*CELL_HEIGHT,CELL_WIDTH,CELL_HEIGHT);
                    drawCell(g, bound,col, row);
                }
        }
        
        public Rectangle getBound(int col,int row){
            return new Rectangle(ROW_HEADER_WIDTH + col*CELL_WIDTH,COLUMN_HEADER_HEIGHT + row*CELL_HEIGHT,CELL_WIDTH,CELL_HEIGHT);
        }
        
        boolean hitTest(int col,int row,int x,int y){
            return getBound(col, row).contains(x,y);
        }
        
        boolean hitTest(int x,int y){
            Rectangle bound;
            for (int row=0;row<rows.getCount();row++){
                for (int col=0;col<columns.getCount();col++){
                    bound=new Rectangle(ROW_HEADER_WIDTH + col*CELL_WIDTH,COLUMN_HEADER_HEIGHT + row*CELL_HEIGHT,CELL_WIDTH,CELL_HEIGHT);
                    if (bound.contains(x, y)){
                        return true;
                    }
                }
            }
            return false;
        }
    }
    
    public AbstractShiftPanel(){
        setPreferredSize(new Dimension(300,300));
        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                mouseClick(e.getX(),e.getY());
            }
        });
    }    
    
    protected void mouseClick(int x,int y){
        for (int col=0;col<columns.getCount();col++)
            if (columns.hitTest(col,x,y))
                columnClick(col);
        
        for (int row=0;row<rows.getCount();row++)
            if (rows.hitTest(row,x,y))
                rowClick(row);
        
        for (int row=0;row<rows.getCount();row++)
            for (int col=0;col<columns.getCount();col++)
                if (cells.hitTest(col,row,x, y))
                    cellClick(col, row);
        
        repaint();
    }
    
    @Override
    public void paint(Graphics g){
        super.paint(g);
        g.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,12));
        rows.draw(g);
        columns.draw(g);
        cells.draw(g);
        g.setColor(Color.gray);
        g.drawRect(0,0, getWidth()-1, getHeight()-1);
        
    }
}

class ShiftPanel extends AbstractShiftPanel{

    public ShiftPanel() {
        super();
        rows.headers=new String[]{"row 1"};
        columns.headers=new String[]{"col 1"};
    }

    @Override
    public void rowClick(int row) {
        System.out.println("ROW CLICK "+row);
        
        if (allowEdit){
            Point p = new Point(0,row);
            Boolean b = cells.points.contains(p);

            for (int col=0;col<columns.getCount();col++){
                p = new Point(col,row);
                if (b)
                    cells.points.remove(p);
                else
                    cells.points.add(p);
            }
            hasChange=true;
            changing();
        }
    }

    @Override
    public void columnClick(int col) {
        System.out.println("COLUMN CLICK "+col);
        if (allowEdit){
            Point p = new Point(col,0);
            boolean b = cells.points.contains(p);

            for (int row=0;row<rows.getCount();row++){
                p=new Point(col,row);
                if (b)
                    cells.points.remove(p);
                else
                    cells.points.add(p);
            }
            hasChange=true;
            changing();
        }
    }

    @Override
    public void cellClick(int col, int row) {
        System.out.println("CLICK ROW"+row +" COLUMN"+col+" "+cells.points.size());
        if (allowEdit){
            Point p = new Point(col,row);
            if (cells.points.contains(p)){
                cells.points.remove(p);
            } else
                cells.points.add(p);
            hasChange=true;
            changing();
        }
    }
    
    void selectAll() {
        Point p;
        cells.points.clear();
        for (int col=0;col<columns.getCount();col++)
            for (int row=0;row<rows.getCount();row++){
                p=new Point(col,row);
                cells.points.add(p);
            }
        repaint();
    }

    void unSelectAll() {
        cells.points.clear();
        repaint();
    }
    
    public void post() throws Exception{
        hasChange=false;
        changing();
    }
    
    public void cancel(){
        cells.points=oldPoints;
        hasChange=false;
        changing();
    }
    
    public void changing(){
    }
    
    
}


/**
 * Редактор графиков
 * <code>
 *  ShiftEditor editr = new ShiftEditor();
 *  Dataset dataset = DataModule.getDataset("teacher");
 *  .....
 *  editor.open();<br>
 *  .....
 *  // Получение чистого графика<br>
 *  int shift_id = dataset.getValues(row).getIntegerValue("shift_id");<br>
 *  editor.setShiftID(shift_id);<br>
 * <br>
 *  // Получение графика с отмеками о занятых часах<br>
 *  int teacher_id = dataset.getValues(row).getIntegerValue("teacher_id");<br>
 *  editor.setTeacherID(teacher_id);<br>
 * </code>
 * 
 * @author вадик
 */
public class ShiftEditor extends ShiftPanel {
    Set<Point> usedPoints = null;
    public static final String SQL_DAY_LIST = "select day_no,day_short_name from day_list";
    public static final String SQL_BELL_LIST = "select time_start from bell_list";

    @Override
    public void drawCell(Graphics g, Rectangle bound, int col, int row) {
        super.drawCell(g, bound, col, row);
        if (usedPoints != null) {
            Point p = new Point(col, row);
            if (usedPoints.contains(p)) {
                g.setColor(Color.yellow);
                g.fillOval(bound.x + 4, bound.y + 4, bound.width - 8, bound.height - 8);
                g.setColor(Color.black);
                g.drawOval(bound.x + 4, bound.y + 4, bound.width - 8, bound.height - 8);
            }
        }
    }

    public void open() throws Exception {
        Dataset r = DataModule.getSQLDataset(SQL_DAY_LIST);
        r.open();
        List<String> dayList = new ArrayList<>();
        for (int i = 0; i < r.size(); i++) {
            dayList.add(r.getValues(i).getString("day_short_name"));
        }
        int w = ROW_HEADER_WIDTH+dayList.size()*CELL_WIDTH;
        
        setColumnHeaders(dayList.toArray(new String[dayList.size()]));
        r = DataModule.getSQLDataset(SQL_BELL_LIST);
        r.open();
        List<String> bellList = new ArrayList<>();
        for (int i = 0; i < r.size(); i++) {
            bellList.add(r.getValues(i).getString("time_start"));
        }
        setRowHeader(bellList.toArray(new String[bellList.size()]));
        int h = COLUMN_HEADER_HEIGHT+bellList.size()*CELL_HEIGHT;
        setPreferredSize(new Dimension(w,h));
    }

    public void setShiftId(Integer shift_id) throws Exception {
        Dataset dataset;
        dataset = DataModule.getSQLDataset("select day_id,bell_id from shift_detail where shift_id=" + shift_id);
        dataset.open();
        Set<Point> points = new HashSet<>();
        for (int i = 0; i < dataset.size(); i++) {
            points.add(new Point(dataset.getValues(i).getInteger("day_id") - 1, dataset.getValues(i).getInteger("bell_id") - 1));
        }
        setPoints(points);
//        setAllowEdit(false);
    }

    public void setTeacherId(Integer teacher_id) throws Exception {
        Recordset r = DataModule.getRecordet("select shift_id from teacher where id=" + teacher_id);
        Integer shift_id = r.getInteger(0);
        setShiftId(shift_id);
        usedPoints = new HashSet<>();
        Dataset dataset = DataModule.getSQLDataset("select day_id,bell_id from schedule where teacher_id=" + teacher_id);
        dataset.open();
        Values v;
        for (int i = 0; i < dataset.size(); i++) {
            v = dataset.getValues(i);
            usedPoints.add(new Point(v.getInteger("day_id") - 1, v.getInteger("bell_id") - 1));
        }
        repaint();
    }

    public void setDepartId(Integer depart_id) throws Exception {
        Recordset r = DataModule.getRecordet("select shift_id from depart where id=" + depart_id);
        Integer shift_id = r.getInteger(0);
        setShiftId(shift_id);
    }

    public void setRoomId(Integer room_id) throws Exception {
        Recordset r = DataModule.getRecordet("select shift_id from room where id=" + room_id);
        Integer shift_id = r.getInteger(0);
        setShiftId(shift_id);
        usedPoints = new HashSet<>();
        Dataset dataset = DataModule.getSQLDataset("select day_id,bell_id from schedule where room_id=" + room_id);
        dataset.open();
        Values v;
        for (int i = 0; i < dataset.size(); i++) {
            v = dataset.getValues(i);
            usedPoints.add(new Point(v.getInteger("day_id") - 1, v.getInteger("bell_id") - 1));
        }
        repaint();
    }

    
}
