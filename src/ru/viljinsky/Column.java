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
public class Column {
    String columnName;
    String tableName;
    String displayLabel;
    String columnTypeName;
    Integer columnWidth; 
    Integer scale;
    Integer precision;
    String columnLabel;
    Integer displaySize;
    boolean autoIncrement;
    boolean primary = false;
    Integer columnType;
    Integer columnIndex;
    Class columnClass ;

    public Column() {
    }
    
    public boolean isPrimary(){
        return primary;
    }

    public Class<?> getColumnClass(){
        return columnClass;
//        return Object.class;
    } 
    
    @Override
    public String toString() {
        return columnName+"  ("
                +  columnTypeName+(primary?"  PK":"")
                + (autoIncrement==true?" AU":"")+")";
    }
    
    public void print(){    
        System.out.println(
                 "columnIndex     = "+columnIndex+"\n"
                +"tableName       = "+tableName + "\n"
                +"columnName      = "+ columnName + "\n"
                +"columnLanel     = "+columnLabel+"\n"
                +"columnTypeName  = "+columnTypeName+"\n"
                +"columnType      = "+columnType+"\n"
                +"columnWidth     ="+columnWidth+"\n"
                +"displaySize     = "+ displaySize+"\n"
                +"scale           = "+scale+"\n"
                +"precision       = "+precision+"\n"
                +"autoIncrement   = "+autoIncrement+"\n"
                +"\n");
    }

    public String getColumnName() {
        return columnName;
    }

    public String getTableName(){
        return tableName;
    }
    
    public String getColumTypeName() {
        return columnTypeName;
    }
    
}
