/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
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
        String columnTypeName;
        rs = meta.getColumns(null, null, tableName, null);
        while (rs.next()){
            column = new Column();
            column.columnIndex =rs.getInt("ORDINAL_POSITION");
            column.tableName=tableName;
            column.columnName=rs.getString("COLUMN_NAME");
            column.columnTypeName=rs.getString("TYPE_NAME");
            columnTypeName=rs.getString("TYPE_NAME");
            column.columnType=rs.getInt("DATA_TYPE");

            switch (columnTypeName){
                    case  "INTEGER":
                        column.columnClass= Integer.class;
                        break;
                        
                    case "FLOAT":
                        column.columnClass= Float.class;
                        break;
                        
                    case "NUMERIC":
                        column.columnClass=Double.class;
                        break;
                        
                    case "BOOLEAN":
                        column.columnClass=Boolean.class;
                        break;
                     default:
                        column.columnClass=Object.class;
                
            }
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
