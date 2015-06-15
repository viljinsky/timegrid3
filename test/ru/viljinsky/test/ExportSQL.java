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
public class ExportSQL {
    /**
     * Экспорт таблиц в backup_sql;
     * @param tableName
     * @return
     * @throws Exception 
     */
    public static String backupTable(String tableName) throws Exception{
        Dataset dataset = DataModule.getDataset(tableName);
        dataset.open();
        Values values;
        Object value;
        StringBuilder result = new StringBuilder();
        result.append("\n--  table ").append(tableName).append(" \n\n");
        result.append("delete from ").append(tableName).append(";\n");
        for (int row=0;row<dataset.size();row++){
            values=dataset.getValues(row);
            result.append("insert into ").append(tableName).append(" (");
            int n = 0;
            for (String columnName:values.keySet()){
                result.append(columnName);
                if (n<values.size()-1)
                    result.append(",");
                n+=1;
            }
            result.append(")\n\t values (");
            
            n=0;
            for (String columnName:values.keySet()){
                value = values.getObject(columnName);
                if (value instanceof String)
                    result.append("'"+value+"'");
                else if (value==null)
                    result.append("null");
                else
                    result.append(value.toString());
                
                if (n<values.size()-1)
                    result.append(",");
                n+=1;
            }
            result.append(");\n");
        }
        
        return result.toString();
    }
    
    public static void backup(String fileName,String[] tables) throws Exception{
        String sql ;
        BufferedWriter bufw = null;
        try{
            bufw = new BufferedWriter(new FileWriter(new File(fileName)));
            for (String tableName:tables){
                sql= backupTable(tableName);
                bufw.append(sql);
            }
            
            bufw.flush();
            
            System.out.println("OK");
        } catch (Exception e){
            throw new Exception("BACKUP_ERROR\n"+e.getMessage());
        } finally {
            if (bufw!=null)
                bufw.close();
        }
        
    }
    
    public static void backupCurriculum() throws Exception{
        String[] tables = new String[]{
            "subject_domain",
            "subject",
            "skill",
            "curriculum",
            "curriculum_detail"
        };
        String sql ;
        String filaName = "учебный план2.sql";
        BufferedWriter bufw = null;
        try{
        bufw = new BufferedWriter(new FileWriter(new File(filaName)));
            for (String tableName:tables){
                sql= backupTable(tableName);
                bufw.append(sql);
            }
            
            bufw.flush();
            
            System.out.println("OK");
        } catch (Exception e){
            throw new Exception("BACKUP_CURRICULUM_ERROR\n"+e.getMessage());
        } finally {
            if (bufw!=null)
                bufw.close();
        }
        
    }
    
    public static void main(String[] args) throws Exception{
        DataModule.open();
        backupCurriculum();
    }
    
}
