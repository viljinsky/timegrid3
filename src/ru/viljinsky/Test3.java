/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.Map;

/**
 *
 * @author вадик
 */
public class Test3 {
    public static void main(String[] args) throws Exception{
        DataModule dataModule = DataModule.getInstance();
        dataModule.open();
        Dataset dataset = dataModule.getDataset("curriculum");
        dataset.open();
        for (Column column:dataset.getColumns()){
            System.out.println(column);
        }
        Map<String,Object> values;
        for (int i=0;i<dataset.size();i++){
            values = dataset.getValues(i);
            System.out.println(values);
            
        }
        
        DatabaseMetaData meta = dataModule.getConnection().getMetaData();
        ResultSet rs = meta.getColumns(null, null, "curriculum", null);
        while (rs.next()){
            System.out.print("->"+rs.getObject("COLUMN_NAME"));
            System.out.print("->"+rs.getObject("DATA_TYPE"));
            System.out.print("->"+rs.getObject("TYPE_NAME"));
            System.out.print("->"+rs.getObject("COLUMN_SIZE"));
            System.out.print("->"+rs.getObject("IS_NULLABLE"));
            System.out.print("->"+rs.getObject("REMARKS"));
            
            
            System.out.println();
            
        }
    }
    
}
