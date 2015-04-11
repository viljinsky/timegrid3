/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.timegrid;

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
        clear();
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

    public void afterDataChange(int dCol, int dRow) {
    }
    
}
