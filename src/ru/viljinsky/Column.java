/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky;

import java.sql.ResultSetMetaData;
import java.sql.Time;
import java.util.Date;

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
    Integer columnWidth; 
    Integer scale;
    Integer precision;
    String columnLabel;
    Integer displaySize;
    boolean autoIncrement;
    boolean primary = false;
    Integer columnType;
    Integer columnIndex;

    public Column() {
    }

//    public Column(ResultSetMetaData rsmeta, Integer columnIndex) throws Exception {
//        try {
//            tableName       = rsmeta.getTableName(columnIndex + 1);
//            columnName      = rsmeta.getColumnName(columnIndex + 1);
//            displayLabel    = columnName;
//            
//            columnClassName = rsmeta.getColumnClassName(columnIndex + 1);
//            precision       = rsmeta.getPrecision(columnIndex+1);
//            scale           = rsmeta.getScale(columnIndex+1);
//            columnLabel     = rsmeta.getColumnLabel(columnIndex+1);
//            displaySize     = rsmeta.getColumnDisplaySize(columnIndex+1);
//            autoIncrement   = rsmeta.isAutoIncrement(columnIndex+1);
//            columnType      = rsmeta.getColumnType(columnIndex+1);
//            columnTypeName  = rsmeta.getColumnTypeName(columnIndex+1);
//            switch (columnType){
//                case java.sql.Types.INTEGER:
//                    columnClassName = Integer.class.getName();
//                    break;
//                case java.sql.Types.VARCHAR:
//                    columnClassName = String.class.getName();
//                    break;
//                case java.sql.Types.REAL:
//                    columnClassName = Float.class.getName();
//                    break;
//                case java.sql.Types.FLOAT:
//                    columnClassName = Float.class.getName();
//                    break;
//                case java.sql.Types.DATE:
//                    columnClassName = Date.class.getName();
//                    break;
////                case java.sql.Types.TIME:
////                    columnClassName = Time.class.getName();
////                    break;
//                case java.sql.Types.BOOLEAN:
//                    columnClassName = Boolean.class.getName();
//                    break;
//                    
//            };
////            System.out.println(this);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    
    public boolean isPrimary(){
        return primary;
    }

    public Class<?> getColumnClass(){
        try{
            return Class.forName(columnClassName);
        } catch (Exception e){
            return Object.class;
        }
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
                +"cloumnClassName = "+columnClassName+"\n"
                +"displaySize     = "+ displaySize+"\n"
                +"scale           = "+scale+"\n"
                +"precision       = "+precision+"\n"
                +"autoIncrement   = "+autoIncrement+"\n"
                +"\n");
    }

    public String getColumnName() {
        return columnName;
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
