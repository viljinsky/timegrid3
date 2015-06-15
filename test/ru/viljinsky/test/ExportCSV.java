/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import ru.viljinsky.sqlite.DataModule;
import ru.viljinsky.sqlite.Dataset;
import ru.viljinsky.sqlite.Values;

/**
 *
 * @author вадик
 */
public class ExportCSV {
    public static String exportTableCSV(String tableName) throws Exception{
        String[] columns = new String[]{"depart_label","day_caption","lesson_time",
                                        "subject_name","group_label","teacher","room"};
        Dataset dataset = DataModule.getSQLDataset("select * from v_schedule order by depart_id,day_id,bell_id,group_id");
        Values values;
        dataset.open();
        Object value;
        StringBuilder result=new StringBuilder();
        for (int row=0;row<dataset.size();row++){
            values = dataset.getValues(row);
            for (String columnName:columns){
                value = values.getObject(columnName);
                if (value==null)
                    result.append("");
                else
                    result.append("\""+value.toString()+"\"");
                result.append(";");
            }
            result.append("\n");
        }
        return result.toString();
    }
    
    
    public static void main(String[] args) throws Exception{
        DataModule.open();
        String sql = exportTableCSV("v_schedule");
        System.out.println(sql);
        
        BufferedWriter bufw = null;
        try{
            bufw     = new BufferedWriter(new FileWriter(new File("Расписание.csv")));
            bufw.write(sql, 0, sql.length());
        } finally {
            if (bufw!=null)
                bufw.close();
        }
        
        System.out.println("OK");
        
        

    }
}
