/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.util.Map;
import ru.viljinsky.DataModule;
import ru.viljinsky.Dataset;
import ru.viljinsky.KeyMap;

/**
 *
 * @author вадик
 */
public class DataTask {
    protected static DataModule dataModule = DataModule.getInstance();
    
    public static void proc2() throws Exception{
        dataModule.execute("delete from schedule");
    }
    
    public static void proc1() throws Exception{
        Dataset dataset = dataModule.getSQLDataset("select * from subject_group");
        dataset.open();
        Map<String,Object> values;
        
        String sql = "insert into schedule (day_id,bell_id,depart_id,subject_id,group_id) values (?,?,?,?,?);";
        KeyMap map = new KeyMap();
        for (int i=0;i<dataset.size();i++){
            values= dataset.getValues(i);
            map.clear();
            map.put(1,1);
            map.put(2, 1);
            map.put(3, values.get("depart_id"));
            map.put(4, values.get("subject_id"));
            map.put(5, values.get("group_id"));
            System.out.println(map);
            dataModule.execute(sql, map);
        }
        
        
    }
    
    public static void main(String[] args) throws Exception{
        dataModule.open();
        proc1();
    }
    
}
