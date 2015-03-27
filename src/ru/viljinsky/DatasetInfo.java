/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author вадик
 */
public class DatasetInfo {

    String tableType; // TABLE or VIEW
    String tableName;
    Map<Integer,Column> columns = new HashMap<>();

    public String getTableType() {
        return tableType;
    }

    public String getTableName() {
        return tableName;
    }
    String selectSQL;
    String insertSQL;
    String deleteSQL;
    String updateSQL;
    
    String primaryKey = "";
    Map<String, String> references = new HashMap<>();

    public DatasetInfo() {}
    
    public DatasetInfo(String tableName,DatabaseMetaData meta) throws Exception{
        this.tableName= tableName;
        selectSQL = "select * from "+tableName;
        ResultSet rs;
        rs = meta.getImportedKeys(null,null, tableName);
        while (rs.next()){
            references.put(rs.getString("FKCOLUMN_NAME"),
                    rs.getString("PKTABLE_NAME")+"."+rs.getString("PKCOLUMN_NAME"));
        }

        rs = meta.getPrimaryKeys(null, null, tableName);
        while (rs.next())
            addPrimaryKey(rs.getString("COLUMN_NAME"));
        
        Column column;
        rs = meta.getColumns(null, null, tableName, null);
        while (rs.next()){
            column = new Column();
            column.columnIndex =rs.getInt("ORDINAL_POSITION");
            column.tableName=tableName;
            column.columnName=rs.getString("COLUMN_NAME");
            column.columnTypeName=rs.getString("TYPE_NAME");
            column.columnType=rs.getInt("DATA_TYPE");

            switch (column.columnType){
                    case  java.sql.Types.INTEGER:
                        column.columnClassName=Integer.class.getName();
                        break;
                        
                    case java.sql.Types.FLOAT:
                        column.columnClassName=Float.class.getName();
                        break;
                        
                    case java.sql.Types.NUMERIC:
                        column.columnClassName=Double.class.getName();
                        
                    case java.sql.Types.VARCHAR:
                        column.columnClassName=String.class.getName();
                        break;
                
            }
            
//            column.precision=rs.getInt("DECIMAL_DIGITS");
            columns.put(column.columnIndex, column);
        }
        
    }

    public void addPrimaryKey(String column_name) {
        if (!primaryKey.isEmpty()) {
            primaryKey += ";";
        }
        primaryKey += column_name;
    }

    public boolean isTable(){
        return tableType.equals("TABLE");
    }
    @Override
    public String toString() {
        return tableName+" ("+tableType+")";
//        String result = "";
//        for (String s : references.keySet()) {
//            result += s + "=" + references.get(s) + "\n";
//        }
//        return tableType+" " + tableName + "\n primary:" + primaryKey + "\n" + result;
    }
    
    public void print(){
        String result = "";
        for (String s : references.keySet()) {
            result += s + "=" + references.get(s) + "\n";
        }
        System.out.println( tableType+" " + tableName + "\n primary:" + primaryKey + "\n" + result);
        for (Integer i:columns.keySet())
            columns.get(i).print();
        
       
    }
    
}
