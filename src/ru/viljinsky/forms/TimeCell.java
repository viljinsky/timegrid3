/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import ru.viljinsky.DataModule;
import ru.viljinsky.Dataset;


class CellsList extends ArrayList<EmptyCell>{
    
    public EmptyCell getPrefferedValue(){
        if (isEmpty())
            return null;
        Collections.sort(this);
        return get(0);
    }
    
    
    public void intersect(CellsList list){
        Set<EmptyCell> l=new HashSet<>();
        for (EmptyCell ex:this){
            if (!list.contains(ex))
                l.add(ex);
        }
        removeAll(l);
    }
    
    public void print(){
        for (EmptyCell ec:this){
            System.out.println(ec);
        }
    }
}

public class TimeCell {
    private static DataModule datamodule = DataModule.getInstance();
    public static final String sqlEMPTY_TEACHER_CELLS=""
            + "--  свободные часы из графика преподавателя\n" +
            "select b.day_id,b.bell_id from teacher a \n" +
            "inner join shift_detail b on a.shift_id=b.shift_id\n" +
            "where a.id=%teacher_id and \n" +
            "not exists (select * from schedule where day_id=b.day_id and bell_id=b.bell_id and teacher_id=a.id)\n" +
            "order by b.bell_id,b.day_id;";
    
    public static final String sqlEMPTY_ROOM_CELLS =
            "select b.day_id,b.bell_id from room a \n" +
            "inner join shift_detail b on a.shift_id=b.shift_id\n" +
            "where a.id=%room_id and \n" +
            "not exists (\n"+
            "  select * from schedule where day_id=b.day_id and bell_id=b.bell_id and room_id=a.id\n" +
            ");";
    
    public static final String sqlEMPTY_DEPART_CELLS =
            "select a.day_id,bell_id from shift_detail a inner join depart b on a.shift_id=b.shift_id\n" +
            "where b.id=%depart_id and not exists (\n" +
            "	select * from schedule where day_id=a.day_id and bell_id=a.bell_id and depart_id=b.id\n" +
            ");";
    
    public static CellsList getEmptyDepartCell(Integer depart_id) throws Exception{
        CellsList result = new CellsList();
        Dataset dataset = datamodule.getSQLDataset(sqlEMPTY_DEPART_CELLS.replace("%depart_id", depart_id.toString()));
        dataset.open();
        for (int i=0;i<dataset.size();i++){
            result.add(new EmptyCell(dataset.get(i)));
        }
        return result;
    }
    public static CellsList getEmptyTeacherCell(Integer teacher_id) throws Exception{
        CellsList result = new CellsList();
        Dataset dataset = datamodule.getSQLDataset(sqlEMPTY_TEACHER_CELLS.replaceAll("%teacher_id", teacher_id.toString()));
        dataset.open();
        for (int i=0;i<dataset.size();i++){
            result.add(new EmptyCell(dataset.get(i)));
        }
        return result;
    }
    
    public static CellsList getEmptyRoomCell(Integer room_id) throws Exception{
        CellsList result = new CellsList();
        Dataset dataset = datamodule.getSQLDataset(sqlEMPTY_ROOM_CELLS.replace("%room_id",room_id.toString()));
        dataset.open();
        for (int i=0;i<dataset.size();i++){
            result.add(new EmptyCell(dataset.get(i)));
        }
        return result;
    }
    
    public static void main(String[] args) throws Exception{
        
        datamodule.open();
        
        Integer teacher_id = 1;
        Integer depart_id =1;
        
        CellsList list1,list2;
        
        list1 = getEmptyDepartCell(depart_id);
        list2 = getEmptyTeacherCell(teacher_id);
        
        list1.intersect(list2);;
        
        System.out.println("-->"+list1.getPrefferedValue());
        list1.print();
        
        
    }
    
}

