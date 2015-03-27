/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky;

/**
 *
 * @author вадик
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.sql.*;

abstract class SQLReader{
    
    public abstract void sqlredy(String sql) throws Exception;

    public void execute(String fileName) throws Exception{
        String line;
        StringBuilder sql;
        BufferedReader reader = null;
        File file=null;

        URL url = CreateData.class.getResource(fileName);
        if (url==null)
            throw new Exception ("Не найден файл '"+fileName+"'");
        try{
            file = new File(url.getFile());
            reader = new BufferedReader(new FileReader(file));
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
    
    Connection con = null;
    Statement stmt = null;
    SQLReader reader;
    
    public void run(String fileName,boolean force,String[] SQLscript) throws Exception {
        
        try{
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e){
            throw new Exception("class not found");
        }
        
        File file = new File(fileName);
        if (file.exists()){
            if (force) {
                if (!file.delete()){
                    throw new Exception("Не удалось удалить файл '"+fileName+"'");
                }
            } else {
                throw new Exception("База данных '"+fileName+"' уже существует");
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
                        throw new Exception("Ошибка при выполнении запроса\nSQL : '"+sql+"'\nMessage:"+e.getMessage());
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
                        throw new Exception("Ошибка при выполнении скрипта '"+script+"'\n"+e.getMessage());
                    }
                }
            } finally {
                con.setAutoCommit(true);
            }
            
        } catch (Exception e){
            throw new Error(e.getMessage());
        } finally {
            if (stmt!=null)
                try {stmt.close();} catch (Exception e){};
            if (con!=null){
                try {con.close();} catch (Exception e){};
            }
        }
    }
    
    public static void main(String[] args){
        String fileName = "example.db";
        String[] script = {"sql/createData.sql","sql/fillData.sql"};
        CreateData cd = new CreateData();
        System.out.println("Создаётся новая база данных...");
        try{
            cd.run(fileName,true,script);
            System.out.println("База данных создана '"+fileName+"'");
        } catch (Exception e){
            System.err.println("Ошибка при создании базы данных:\n"+e.getMessage());
        }
    }
    
}
