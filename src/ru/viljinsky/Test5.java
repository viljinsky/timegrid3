/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author вадик
 */
public class Test5 {
    public static void main(String[] args) throws Exception{
        DataModule dataModule = DataModule.getInstance();
        Dataset dataset;
        dataModule.open();
        
        dataModule.execute("pragma foreign_keys=ON;");
        
        
        
        Map<String,Object> values = new HashMap<>();
        values.put("id",null);
        values.put("name", "room1");
        values.put("shift_id",new Integer(5));
        values.put("profile_id",new Integer(5));
        
        dataset = dataModule.getDataset("room");
        dataset.open();

        System.out.println(dataset.info.insertSQL);
        dataModule.execute("insert into room(name,shift_id,profile_id) values('qwert77','5','5')");
        
//        String[] tableNames = dataModule.getTableNames();
//        for (String tableName:tableNames){
//            dataset=dataModule.getDataset(tableName);
//            dataset.print();
//        }
        dataModule.close();
        
        String sql = "insert into qwerty (v1,v2,v3) values (%1,%2,%3)";
        String s;
        
         s = sql.replace("%1", "'one'");
         s = s.replace("%2", "'two'");
         s = s.replace("%3", "'three'");
         System.out.println(s);
        
    }
    
}
