/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import ru.viljinsky.forms.DataTask;
import ru.viljinsky.sqlite.CreateData;
import ru.viljinsky.sqlite.DataModule;
import ru.viljinsky.sqlite.Dataset;
import ru.viljinsky.sqlite.Values;

/**
 *
 * @author вадик
 */
public class FillSubjectGroup3 {
    
    /**
     * После импорта списка классов нужно создать группы
     *
     * @author вадик
     */
    public void proc1() throws Exception{
        DataModule.execute("delete from subject_group");
        Dataset dataset = DataModule.getSQLDataset("select * from depart");
        dataset.open();
        Values v;
        for (int i=0;i<dataset.size();i++){
            v= dataset.getValues(i);
            System.out.println(v.getString("label"));
            DataTask.fillSubjectGroup2(v.getInteger("id"));
            System.out.println("OK");
        }
    }
    
    
    public void execute(String fileName) throws Exception{
        File file = new File(fileName);
        if (!file.exists()){
            throw new Exception("file not found");
        }
        
        BufferedReader reader = null;
        try{
            String sql = "insert into teacher (first_name,last_name,patronymic,profile_id) \n"
                    +"values(?,?,?,?)";
            PreparedStatement stmt = DataModule.getConnection().prepareStatement(sql);
            String[] values ;
            Map<Integer,Object> map = new HashMap<>();
            String[] columns = new String[]{"last_name;0","first_name;1","patronymic;2","profile_id;3"};
            reader = new BufferedReader(new FileReader(file));
            String line;
            StringBuilder result = new StringBuilder();
            while ((line = reader.readLine())!=null){
                if (line.startsWith("//"))
                    line ="";
                if (!line.isEmpty()){
                    values = line.split(";");
                    for (String column:columns){
                        String columnName = column.split(";")[0];
                        Integer columnIndex = Integer.valueOf(column.split(";")[1]);
                        Object value = values.length>columnIndex?values[columnIndex]:null;
                        map.put(columnIndex,value);
                        stmt.setObject(columnIndex+1, value);
                    }
                    System.out.println(map);
                    stmt.execute();
                    result.append(line+"\n");
                }
            }
            System.out.println(result.toString());
            
        } finally {
            if (reader!=null){
                reader.close();
            }
        }
        
    }
    
    public static void main(String[] args){
        FillSubjectGroup3 fsg = new FillSubjectGroup3();
        String fileName = "новая 41.db";
        try{
            File file = new File(fileName);
            if (file.exists()){
                throw new Exception("FILE_EXISTS\n"+fileName);
            }
            
            CreateData.newDatabase(fileName);
            
            DataModule.open(fileName);
            try{
                DataModule.executeScript(new File("./sql/учебный план.sql"));
                DataModule.executeScript(new File("./sql/расписание звонков.sql"));
                DataModule.executeScript(new File("./sql/профили.sql"));
                DataModule.executeScript(new File("./sql/графики.sql"));
                DataModule.executeScript(new File("./sql/классы.sql"));
                DataModule.executeScript(new File("./sql/помещения.sql"));
                
                fsg.execute("./sql/fio.csv");
                
                fsg.proc1();
                
                DataModule.commit();
                System.out.println("sfg has been completed ");
            } catch (SQLException ex){
                DataModule.rollback();
                throw new Exception(ex);
            }
        } catch (Exception e){
            System.err.println("Ошибка в FillSubjectGroup3:\n"+e.getMessage());
        }
    }
    
}
