/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author вадик
 */

public class Dataset extends ArrayList<Object[]> implements IDataset {
    DataModule dataModule;
    Map<Integer, Column> columns;
    String selectSQL;
    String insertSQL;
    String deleteSQL;
    String updateSQL;
    String tableName;
    Boolean active = false;

    public Dataset() {
        columns = new HashMap<>();
    }

    @Override
    public String getTableName() {
        return tableName;
    }
    
    @Override
    public Column getColumn(Integer columnIndex){
        return columns.get(columnIndex);
    }
    
    @Override
    public Integer getRowCount(){
        return this.size();
    }
    
    @Override
    public Map<String,Object> getValues(Integer rowIndex){
        Map<String,Object> result = new HashMap<>();
        Object[] rowset;
        rowset = get(rowIndex);
        Column column;
        for (int col:columns.keySet()){
            column=columns.get(col);
            result.put(column.columnName,rowset[col]);
        }
        return result;
    }
    
    @Override
    public void setVlaues(Integer rowIndex, Map<String, Object> aValues) throws Exception {
        Object[] rowset = get(rowIndex);
        for (String column:aValues.keySet()){
            rowset[getColumnIndex(column)]=aValues.get(column);
        }
    }    
    
    @Override
    public Integer getColumnCount() {
        return columns.size();
    }

    @Override
    public Integer getColumnIndex(String columnName) throws Exception{
        for (Integer col:columns.keySet()){
            if (columns.get(col).columnName.equals(columnName))
                return col;
        }
        throw new Exception ("Поле '"+columnName+"' не найдено");
    }
    

    public String getColumnName(Integer columnIndex) {
        return columns.get(columnIndex).columnName;
    }

    @Override
    public void open() throws Exception {
        if (test()){
            Statement stmt = null;
            try {
                stmt = dataModule.con.createStatement();
                ResultSet rs = stmt.executeQuery(selectSQL);
                Object[] rowset;
                while (rs.next()) {
                    rowset = new Object[getColumnCount()];
                    for (int i = 0; i < rowset.length; i++) {
                        rowset[i] = rs.getObject(i + 1);
                    }
                    add(rowset);
                }
                this.active = true;
            } finally {
            }
        }
    }

    @Override
    public void close() throws Exception {
        this.clear();
        this.active = false;
    }

    public Column getColumnByName(String columnName) {
        Column column;
        for (int col : columns.keySet()) {
            column = columns.get(col);
            if (column.columnName.equals(columnName)) {
                return column;
            }
        }
        return null;
    }

    public boolean test() throws Exception {
        Statement stmt = null;
        try {
            stmt = dataModule.con.createStatement();
            ResultSet rs = stmt.executeQuery(selectSQL);
            ResultSetMetaData rsmeta = rs.getMetaData();
            for (int i = 0; i < rsmeta.getColumnCount(); i++) {
                columns.put(i, new Column(rsmeta, i));
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt!=null)
                stmt.close();
        }
        return false;
    }
    
    
    public void print(){
        Map<String,Object> values;
        if (!active){
            System.out.println("Dataset not active");
        }
        System.out.println(selectSQL);
        for (int i=0;i<getRowCount();i++){
            values = getValues(i);
            for (String columnName:values.keySet()){
                System.out.print(columnName+" = '"+values.get(columnName)+"' ");
            }
            System.out.println();
            
        }
        
    }

    @Override
    public Integer appned() {
        Object[] rowset = new Object[getColumnCount()];
        add(rowset);
        return  indexOf(rowset);
    }

    @Override
    public Integer appned(Map<String, Object> values) throws Exception {
        Object[] rowset = new Object[getColumnCount()];
        for (String columnName:values.keySet()){
            rowset[getColumnIndex(columnName)]= values.get(columnName);
        }
        add(rowset);
        return indexOf(rowset);
    }

    @Override
    public Boolean delete(Integer rowIndex) {
        Object[] rowset = get(rowIndex);
        return remove(rowset);
    }

}
