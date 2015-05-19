/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.sqlite;

/**
 *
 * @author вадик
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.*;

abstract class SQLReader{
    
    public abstract void sqlredy(String sql) throws Exception;

    public void execute(String fileName) throws Exception{
//        URL url = SQLReader.class.getResource(fileName);
//        String path = url.getPath();
        
        System.out.println("SCRIPT_EXECUTING \""+fileName +"\"");
        String line;
        StringBuilder sql;
        BufferedReader reader = null;
        
        InputStream str = null;
        
        str = CreateData.class.getResourceAsStream(fileName);
        if (str==null){
            throw new Exception("SCRIPT_NOT_FOUND "+fileName);
        }

        try{
            reader = new BufferedReader(new InputStreamReader(str, "UTF-8"));
            sql = new StringBuilder();
            while ((line=reader.readLine())!=null){
                if (!line.isEmpty()){
                    line = line.trim();
                    if (!line.startsWith("--")){
                        sql.append(line+"\n");
                        if (line.endsWith(";")){
                            sqlredy(sql.toString());
                            sql = new StringBuilder();
                        }
                    }
                }
            }

        } finally {
            if (reader!=null) reader.close();
        }
    }
}

public class CreateData {
    
    public static final String[] scriptList = {"/ru/viljinsky/sql/schedule.sql","/ru/viljinsky/sql/data.sql"};
    
    
    Connection con = null;
    Statement  stmt = null;
    SQLReader  reader;
    
    public void run(String fileName,boolean force,String[] SQLscript) throws Exception {
        
        try{
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e){
            throw new Exception("ERROR_CLASS_NOT_FOUND");
        }
        
        File file = new File(fileName);
        if (file.exists()){
            if (force) {
                if (!file.delete()){
                    throw new Exception("ERROR_FILE_CAN_NOT_DELETED '"+fileName+"'");
                }
            } else {
                throw new Exception("ERROR_FILE_EXISTS '"+fileName+"'");
            }
        }
        
        
        Connection con = null;
        try{
            con = DriverManager.getConnection(String.format("jdbc:sqlite:%s",fileName));
            stmt = con.createStatement();
            
            stmt.execute("pragma foreign_keys = ON;");
            
            reader = new SQLReader() {

                @Override
                public void sqlredy(String sql) throws Exception {
                    try{
                        stmt.execute(sql);
                    } catch (SQLException e){
                        throw new Exception("ERROR_SQL_EXECUTING\nSQL : '"+sql+"'\nMessage:"+e.getMessage());
                    }
                }
            };
            
            
            con.setAutoCommit(false);
            try{
                for (String script:SQLscript) {
                    try{
                        reader.execute(script);
                        con.commit();
                    } catch (Exception e){
                        con.rollback();
                        throw new Exception("ERROR_SCRIPT_EXECUTING '"+script+"'\n"+e.getMessage());
                    }
                }
            } finally {
                con.setAutoCommit(true);
            }
            
        } catch (Exception e){
            throw new Error(e.getMessage());
        } finally {
            if (stmt!=null)
                try {
                    stmt.close();
                } catch (Exception e){};
            if (con!=null){
                try {
                    con.close();
                } catch (Exception e){};
            }
        }
    }
    
    public static void execute(String fileName) throws Exception{
        CreateData cd = new CreateData();
        System.out.println("Создаётся новая база данных...");
        try{
            cd.run(fileName,false,scriptList);
            System.out.println("База данных создана '"+fileName+"'");
        } catch (Exception e){
            throw new Exception("CREARE_DATABASE_ERROR:\n"+e.getMessage());
        }
    }


    public static void main(String[] args){
        String fileName = DataModule.DEFAULT_DATA;//  "example.db";
        CreateData cd = new CreateData();
        System.out.println("Создаётся новая база данных...");
        try{
            cd.run(fileName,true,scriptList);
            System.out.println("База данных создана '"+fileName+"'");
        } catch (Exception e){
            System.err.println("Ошибка при создании базы данных:\n"+e.getMessage());
        }
    }
    
}
