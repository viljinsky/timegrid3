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

import java.sql.*;

public class Test {
    public static void main(String[] args) throws Exception{
        
        String tableName;
        Class.forName("org.sqlite.JDBC");
        Connection con = DriverManager.getConnection("jdbc:sqlite:example.db");
        DatabaseMetaData meta  = con.getMetaData();
        ResultSet rs = meta.getTables(null, null,null, new String[]{"TABLE"});
        Statement stmt;
        while (rs.next()){
            tableName = rs.getString("TABLE_NAME");
            System.out.println("tableName : "+tableName);
            
            ResultSet data;
            data = meta.getPrimaryKeys(null, null, tableName);
            while (data.next()){
                System.out.println("primary :"+data.getString("TABLE_NAME")+" "+data.getString("COLUMN_NAME"));
            }
            
            data = meta.getImportedKeys(null,null, tableName);
            while (data.next()){
                System.out.println("foreigh :"
                        +data.getString("PKTABLE_NAME")+" "
                        +data.getString("PKCOLUMN_NAME")+" "
                        +data.getString("FKCOLUMN_NAME"));
            }
            
            System.out.println();
        }
        
    }
    
}
