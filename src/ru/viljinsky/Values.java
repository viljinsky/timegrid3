/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky;

import java.util.HashMap;

/**
 *
 * @author вадик
 */
public class Values extends HashMap<String, Object> {
    public static final String COLUMN_NOT_FOUND = "COLUMN_NOT_FOUND";

    
    public Object getObject(String columnName) throws Exception{
        if (containsKey(columnName))
            return get(columnName);
        throw new Exception(COLUMN_NOT_FOUND + "'" + columnName + "'");        
    }
    public Integer getInteger(String columnName) throws Exception {
        if (containsKey(columnName)) {
            Object result = get(columnName);
            if (result == null) {
                return null;
            }
            return (Integer) result;
        }
        throw new Exception(COLUMN_NOT_FOUND + "'" + columnName + "'");
    }
    
    public String getString(String columnName) throws Exception{
        if (containsKey(columnName)){
            Object result = get(columnName);
            if (result!=null)
                return (String)result;
            return null;
        }
        throw new Exception(COLUMN_NOT_FOUND+"'"+columnName+"'");
        
    }
    
}
