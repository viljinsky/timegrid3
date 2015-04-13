/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.timegrid;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import ru.viljinsky.DataModule;
import ru.viljinsky.Dataset;
import ru.viljinsky.Values;

/**
 *
 * @author вадик
 */
public class TimeTableGrid extends TimeGrid {
    protected int startRow;
    protected int StartCol;
    Dataset dataset = null;
    Values filter = null;

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
        reload();
    }

    public void reload() throws Exception {
        dataset = DataModule.getSQLDataset("select * from v_schedule");
        dataset.setFilter(filter);
        dataset.open();
//        for ()
        super.clear();
        Values values;
        TimeTableGroup ttGroup;
        for (int i = 0; i < dataset.getRowCount(); i++) {
            values = dataset.getValues(i);
            ttGroup = new TimeTableGroup(values);
            addElement(ttGroup);
        }
        realign();
    }

    //        @Override
    //        public void cellElementClick(CellElement ce) {
    //        }
    @Override
    public void startDrag(int col, int row) throws Exception {
        super.startDrag(col, row);
        startRow = row;
        StartCol = col;
    }

    @Override
    public void stopDrag(int col, int row) throws Exception {
        if (col == StartCol && row == startRow) {
            super.stopDrag(col, row);
            return;
        }
        String sql;
        try {
            for (CellElement ce : getSelectedElements()) {
                TimeTableGroup sg = (TimeTableGroup) (ce);
                sql = String.format("update schedule set day_id=%d,bell_id=%d " + "where day_id=%d and bell_id=%d and depart_id=%d and subject_id=%d and group_id=%d;", sg.day_no + col - StartCol, sg.bell_id + row - startRow, sg.day_no, sg.bell_id, sg.depart_id, sg.subject_id, sg.group_id);
                System.out.println(sql);
                DataModule.execute(sql);
                sg.day_no += col - StartCol;
                sg.bell_id += row - startRow;
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
        String sql = "insert into schedule (day_id,bell_id,depart_id,subject_id,group_id,teacher_id,room_id)\n"
                  + " values (%d,%d,%d,%d,%d,%d,%d)";
        if (values==null)
            return;
        if (values.getInteger("unplaced")<=0)
            return;
        int day_id= getSelectedCol()+1;
        int bell_id = getSelectedRow()+1;
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
//            super.clear();
            realign();
        } catch (Exception e){
            DataModule.rollback();
            throw new Exception("TIME_TEBLE_CLEAR_ERROR\n"+e.getMessage());
        }
    }

    
    
    public void afterDataChange(int dCol, int dRow) {
    }
    
}
