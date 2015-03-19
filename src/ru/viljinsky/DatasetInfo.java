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
class DatasetInfo {

    String tableName;
    String selectSQL;
    String insertSQL;
    String deleteSQL;
    String updateSQL;
    
    String primaryKey = "";
    Map<String, String> references = new HashMap<>();

    public DatasetInfo() {}
    
//    public String getReferences(String columnName){
//        return references.get(columnName);
//    }
    
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
    }

    public void addPrimaryKey(String column_name) {
        if (!primaryKey.isEmpty()) {
            primaryKey += ";";
        }
        primaryKey += column_name;
    }

    @Override
    public String toString() {
        String result = "";
        for (String s : references.keySet()) {
            result += s + "=" + references.get(s) + "\n";
        }
        return "table " + tableName + "\n primary:" + primaryKey + "\n" + result;
    }
    
}
