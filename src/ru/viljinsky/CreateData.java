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
    public SQLReader(){
    }

    public abstract void sqlredy(String sql);

    public void execute(String fileName) throws Exception{
        String line;
        StringBuilder sql;
        BufferedReader reader = null;
        File file=null;

        URL url = CreateData.class.getResource(fileName);
        if (url==null)
            throw new Exception ("file "+fileName+"not found");
        try{
            file = new File(url.getFile());
            reader = new BufferedReader(new FileReader(file));
            sql = new StringBuilder();
            while ((line=reader.readLine())!=null){
                if (!line.isEmpty()){
                    line = line.trim();
                    if (!line.startsWith("--")){
                        sql.append(line);
                        if (line.endsWith(";")){
                            sqlredy(sql.toString());
                            sql = new StringBuilder();
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader!=null) reader.close();
        }
    }
}

public class CreateData {
    
    Connection con = null;
    Statement stmt = null;
    SQLReader reader;
    
    public void run(boolean force) {
        String fileName = "example.db";
        
        try{
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e){
            System.err.println("class not found");
            return;
        }
        
        File file = new File(fileName);
        if (file.exists()){
            System.err.println("file exists");
            if (force)
                if (!file.delete()){
                    return;
                } else {
                    System.out.println("file has been deleted");
                }
        }
        
        
        Connection con = null;
        try{
            con = DriverManager.getConnection(String.format("jdbc:sqlite:%s",fileName));
            con.setAutoCommit(false);
            stmt = con.createStatement();
            
            reader = new SQLReader() {

                @Override
                public void sqlredy(String sql) {
                    try{
                        stmt.execute(sql);
                    } catch (SQLException e){
                        System.err.println(sql+"\n"+e.getMessage());
                    }
                }
            };
            
            
           reader.execute("createData.sql");
           con.commit();
           reader.execute("fillData.sql");
           con.commit();
            
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (stmt!=null)
                try {stmt.close();} catch (Exception e){};
            if (con!=null){
                try {con.close();} catch (Exception e){};
            }
        }
        
        
    }
    
    public static void main(String[] args){
        new CreateData().run(true);
    }
    
}
