/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import ru.viljinsky.Column;
import ru.viljinsky.DataModule;
import ru.viljinsky.Dataset;

/**
 *
 * @author вадик
 */
public class Test1 {
    public static String sql=
          "select d.subject_name as subject_name,e.label, c.default_teacher_id,\n" +
        "   c.subject_id as teacher_id,c.group_id as group_id,c.depart_id as depart_id,c.subject_id as subject_id,c.hour_per_week\n" +
        "  from teacher a inner join profile_item b\n" +
        "on a.profile_id=b.profile_id\n" +
        "inner join v_subject_group c on c.subject_id=b.subject_id\n" +
        "inner join subject d on d.id=c.subject_id\n" +
        "inner join depart e on e.id=c.depart_id\n" +
        "where c.default_teacher_id is null";
            
    public static void main(String[] args) throws Exception{
        
        
//        String s="qwer.ty";
//        if (s.contains(".")){
//            System.out.println(s.split("\\.")[1]);
//        }
        
        DataModule datamodule = DataModule.getInstance();
        datamodule.open();
        Dataset dataset = datamodule.getSQLDataset(sql);
        for (Column column:dataset.getColumns()){
            column.print();
        }
        
        
    }
    
}
