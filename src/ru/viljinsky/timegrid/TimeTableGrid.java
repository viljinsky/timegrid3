/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.timegrid;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JOptionPane;
import ru.viljinsky.sqlite.DataModule;
import ru.viljinsky.sqlite.Dataset;
import ru.viljinsky.sqlite.Recordset;
import ru.viljinsky.sqlite.Values;

/**
 *
 * @author вадик
 */
public class TimeTableGrid extends TimeGrid {
    protected int startRow;
    protected int StartCol;
    Dataset dataset = null;
    Values filter = null;
    /** Доступные ячейки */
    public Set<Point> avalableCells = null;
    /** Ячейки незаполненные групами*/
    public List<Point> emptyCells = new ArrayList<>();
    /** Заголовки колонок - дни */
    public Map<Integer,String> colCaption = null;
    /** Заголовки строк - часы  */
    public Map<Integer,String> rowCaption = null;

    
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (emptyCells.isEmpty())
            return;
        Rectangle b;
        Graphics2D g2 = (Graphics2D)g;
        Stroke stroke = new BasicStroke(4);
        g2.setStroke(stroke);
        g2.setColor(Color.BLUE);
        for (Point p:emptyCells){
            b= getBound(p.x, p.y);
            g2.drawRect(b.x, b.y, b.width, b.height);
        }
    }
    
    

    @Override
    public String getRowHeaderText(int row) {
        if (rowCaption==null)
            return super.getRowHeaderText(row); //To change body of generated methods, choose Tools | Templates.
        else
            return rowCaption.get(row);
    }

    @Override
    public String getColumnHeaderText(int col) {
        if (colCaption==null)
            return super.getColumnHeaderText(col); //To change body of generated methods, choose Tools | Templates.
        else
            return colCaption.get(col);
    }
    
    

    public Dataset getDataset() {
        return dataset;
    }

    public TimeTableGrid(){
        super(7,10);
    }
    
    public TimeTableGrid(int col, int row) {
        super(col, row);
    }

    public void SetFilter(Values filter) throws Exception {
        this.filter = filter;
        if (filter!=null)
            reload();
        else{
            avalableCells.clear();
            emptyCells.clear();
            cells.clear();
            realign();
        } 
            
    }

    public void reload() throws Exception {
        emptyCells.clear();
        cells.clear();
                
        dataset = DataModule.getSQLDataset("select * from v_schedule order by depart_id,group_id");
        dataset.open(filter);
        Values values;
        TimeTableGroup ttGroup;
        for (int i = 0; i < dataset.getRowCount(); i++) {
            values = dataset.getValues(i);
            ttGroup = new TimeTableGroup(values){

                @Override
                public void checkClick(TimeTableGroup group) {
                    System.out.println(group.toString()+" CLICKED!!!");
                    group.checked = (group.checked==Boolean.FALSE);
                }
                
            };
            addElement(ttGroup);
        }
        realign();
    }

    @Override
    public void startDrag(int col, int row) throws Exception {
        super.startDrag(col, row);
        startRow = row;
        StartCol = col;
    }

    @Override
    public void stopDrag(int col, int row) throws Exception {
        String sql ="update schedule set day_id=%d,bell_id=%d "
                + "where day_id=%d "
                + "and bell_id=%d "
                + "and depart_id=%d "
                + "and subject_id=%d "
                + "and group_id=%d;";

        if (col == StartCol && row == startRow) {
            super.stopDrag(col, row);
            emptyCells.clear();
            return;
        }
        
        
        TimeTableGroup group;
        // проверка попадания в emptyCell
        Point testPoint;
        try{
            for (CellElement ce:getSelectedElements()){
                group=(TimeTableGroup)ce;
                testPoint = new Point(group.day_no+col-StartCol-1,group.bell_id+row-startRow-1);
                if (!emptyCells.contains(testPoint)){
                    throw new Exception("CAN_NOT_PLACE_IN_THIS_CELL");
                }

            }
        }
        finally{
            emptyCells.clear();
        }
        
        
    
        try {
            for (CellElement ce : getSelectedElements()) {
                group = (TimeTableGroup) (ce);
                sql = String.format(sql, group.day_no + col - StartCol, group.bell_id + row - startRow, 
                        group.day_no, group.bell_id, group.depart_id, group.subject_id, group.group_id);
                System.out.println(sql);
                DataModule.execute(sql);
                group.day_no += col - StartCol;
                group.bell_id += row - startRow;
            }
            DataModule.commit();
            super.stopDrag(col, row);
            realign();
            // обновление таблицы
            afterDataChange(col - StartCol, row - startRow);
        } catch (Exception e) {
            DataModule.rollback();
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    @Override
    public void delete() throws Exception{
        TimeTableGroup group;
        String sql = "delete from schedule where day_id=%d and bell_id=%d and depart_id=%d and subject_id=%d and group_id=%d";
        try{
            for (CellElement ce:getSelectedElements()){
                if (ce.moveble==true){
                    group = (TimeTableGroup)ce;
                    DataModule.execute(String.format(sql,group.day_no,group.bell_id,group.depart_id,group.subject_id,group.group_id));
                    cells.remove(ce);
                }
            }
//            super.delete(); 
            DataModule.commit();
            realign();
        } catch (Exception e){
            DataModule.rollback();
            throw new Exception("TIME_TABLE_DELETE_ERROR\n"+e.getMessage());
        }
    }
    
    public void fix() throws Exception{
        TimeTableGroup group;
        String sql = "update schedule set ready='true' where day_id=%d and bell_id=%d and depart_id=%d and subject_id=%d and group_id=%d;";
        try{
            for (CellElement ce:getSelectedElements()){
                group = (TimeTableGroup)ce;
                DataModule.execute(String.format(sql,group.day_no,group.bell_id,group.depart_id,group.subject_id,group.group_id));
                group.ready=true;
            }
            DataModule.commit();
            repaint();
            
        } catch (Exception e){
            DataModule.rollback();
            throw new Exception("FIX_SCHEDULE_ERROR\n"+e.getMessage());
        }
    }
    
    public void unfix() throws Exception{
        TimeTableGroup group;
        String sql = "update schedule set ready='false' where day_id=%d and bell_id=%d and depart_id=%d and subject_id=%d and group_id=%d;";
        try{
            for (CellElement ce:getSelectedElements()){
                group = (TimeTableGroup)ce;
                DataModule.execute(String.format(sql,group.day_no,group.bell_id,group.depart_id,group.subject_id,group.group_id));
                group.ready=false;
            }
            DataModule.commit();
            repaint();
            
        } catch (Exception e){
            DataModule.rollback();
            throw new Exception("FIX_SCHEDULE_ERROR\n"+e.getMessage());
        }
    }
    
    public void insert(Values values) throws Exception{
        
        if (emptyCells.isEmpty()){
            throw new Exception("EMPTY_CELLS_LIST IS EMPTY");
        }
        Point p = emptyCells.get(0);
        
        String sql = "insert into schedule (day_id,bell_id,depart_id,subject_id,group_id,teacher_id,room_id)\n"
                  + " values (%d,%d,%d,%d,%d,%d,%d)";
        if (values==null)
            return;
        if (values.getInteger("unplaced")<=0)
            return;
        int day_id= p.x+1;// getSelectedCol()+1;
        int bell_id = p.y+1;// getSelectedRow()+1;
        int depart_id =values.getInteger("depart_id");
        int subject_id=values.getInteger("subject_id");                
        int group_id= values.getInteger("group_id");
        try{
            DataModule.execute(String.format(sql,
                    day_id,
                    bell_id,
                    depart_id,
                    subject_id,
                    group_id,
                    values.getInteger("teacher_id"),
                    values.getInteger("room_id")
                    ));
            DataModule.commit();
            values.put("day_id",selectedCol+1 );
            values.put("bell_id",selectedRow+1);
            Dataset dataset = DataModule.getSQLDataset(String.format(
                    "select * from v_schedule where day_id=%d and bell_id=%d"
                            + " and depart_id=%d "
                            + " and subject_id=%d "
                            + " and group_id=%d",
                    day_id,bell_id,depart_id,subject_id,group_id));
            dataset.open();
            values = dataset.getValues(0);
            TimeTableGroup group = new TimeTableGroup(values);
            addElement(group);
            emptyCells.clear();
            realign();
        } catch (Exception e){
            DataModule.rollback();
            throw new Exception("TIME_TABLE_GRID_INSERT_ERROR\n"+e.getMessage());
        }
    }

    @Override
    public void clear() throws Exception {
        String sql = "delete from schedule "
                    + "where day_id=%d "
                    + "and bell_id=%d "
                    + "and depart_id=%d "
                    + "and subject_id=%d "
                    + "and group_id=%d;";
        TimeTableGroup group;
        try{
            List<CellElement> list = new ArrayList(cells);
            for (CellElement ce:list){
                group=(TimeTableGroup)ce;
                if (group.ready!=true){
                    DataModule.execute(String.format(sql,
                            group.day_no,
                            group.bell_id,
                            group.depart_id,
                            group.subject_id,
                            group.group_id
                            ));
                    cells.remove(group);
                }
            }
            DataModule.commit();
            realign();
        } catch (Exception e){
            DataModule.rollback();
            throw new Exception("TIME_TEBLE_CLEAR_ERROR\n"+e.getMessage());
        }
    }

    @Override
    public Color getCellBackground(int col, int row) {
        if (avalableCells!=null){
            if (avalableCells.contains(new Point(col,row)))
                return Color.WHITE;
                
        }
        return super.getCellBackground(col, row); //To change body of generated methods, choose Tools | Templates.
    }

    
    
    public void afterDataChange(int dCol, int dRow) {
    }
    
    
    public void setVisibleCell(int col,int row){
        Rectangle r = getBound(col, row);
//        System.out.println(String.format("%d %d %d %d",r.x,r.y,r.width,r.height));
        scrollRectToVisible(r);        
    }
    public void setSelectedGroup(Values values){
        TimeTableGroup group;
        int day_id,bell_id,depart_id,subject_id,group_id;
        try{
            day_id=values.getInteger("day_id");
            bell_id=values.getInteger("bell_id");
            depart_id=values.getInteger("depart_id");
            subject_id=values.getInteger("subject_id");
            group_id=values.getInteger("group_id");
            for (CellElement ce:cells){
                group=(TimeTableGroup)ce;
                group.setSelected(group.day_no==day_id && group.bell_id==bell_id && group.depart_id==depart_id && subject_id==subject_id && group.group_id==group_id);
            }
            setVisibleCell(day_id-1, bell_id-1);
            
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    public void selectValues(Values values){
        TimeTableGroup group;
        try{
        for (CellElement ce:cells){
            group =(TimeTableGroup)ce;
            group.selected=(group.depart_id.equals(values.getInteger("depart_id")) 
                    && group.subject_id.equals(values.getInteger("subject_id"))
                    && group.group_id.equals(values.getInteger("group_id")));
            
        }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void open() throws Exception{
        
        Map<Integer,String> columnHeaderCaption = new HashMap<>();
        Recordset recordset = DataModule.getRecordet("select day_no,day_caption from day_list");
        Object[] r;
        for (int i=0;i<recordset.size();i++){
            r=recordset.get(i);
            columnHeaderCaption.put(i,(String)r[1]);
        }
        colCaption = columnHeaderCaption;
        
        Map<Integer,String> rowHeaderCaption = new HashMap<>();
        recordset=DataModule.getRecordet("select bell_id,time_start || '\n' || time_end from bell_list");
        for (int i=0;i<recordset.size();i++){
            r=recordset.get(i);
            rowHeaderCaption.put(i, (String)r[1]);
        }
        rowCaption = rowHeaderCaption;
        
    }
    
}
