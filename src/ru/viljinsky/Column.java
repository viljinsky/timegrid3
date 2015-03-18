/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky;

import java.sql.ResultSetMetaData;

/**
 *
 * @author вадик
 */
public class Column {
    String columnName;
    String tableName;
    String displayLabel;
    String columnTypeName;
    String columnClassName;

    public Column() {
    }

    public Column(ResultSetMetaData rsmeta, Integer columnIndex) throws Exception {
        try {
            columnName = rsmeta.getColumnName(columnIndex + 1);
            displayLabel = columnName;
            tableName = rsmeta.getTableName(columnIndex + 1);
            columnClassName = rsmeta.getColumnClassName(columnIndex + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return tableName + "." + columnName + " (" + columnTypeName + ")";
    }
    
}
