/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.sqlite;

import java.util.HashMap;

/**
 *
 * @author вадик
 */
public class Values extends HashMap<String, Object> {
    public static final String COLUMN_NOT_FOUND = "COLUMN_NOT_FOUND";
    
    public Values(){
        super();
    }
    
    public Values(String key,Object value){
        put(key,value);
    }

    public void setValue(String columnName,Object value) throws Exception{
        if (containsKey(columnName)){
            put(columnName,value==null?null:value.toString());
        }
    }
    
    public Object getObject(String columnName) throws Exception{
        if (containsKey(columnName))
            return get(columnName);
        throw new Exception(COLUMN_NOT_FOUND + "'" + columnName + "'");        
    }
    
    public Integer getInteger(String columnName,Integer defValue) throws Exception{
        Integer result = getInteger(columnName);
        if (result==null)
            return defValue;
        return result;
    } 
    public Integer getInteger(String columnName) throws Exception {
        if (containsKey(columnName)) {
            Object result = get(columnName);
            if (result == null) {
                return null;
            }
            if (result instanceof Number)
                return (Integer) result;
            if (result instanceof String)
                return Integer.valueOf((String)result);
        }
        throw new Exception(COLUMN_NOT_FOUND + "'" + columnName + "'");
    }
    
    public String getString(String columnName) throws Exception{
        if (containsKey(columnName)){
            Object result = get(columnName);
            if (result!=null)
                if (result instanceof Number)
                    return result.toString();
                else    
                    return (String)result;
            return null;
        }
        throw new Exception(COLUMN_NOT_FOUND+"'"+columnName+"'");
    }
    
    public Boolean getBoolean(String columnName) throws Exception{
        if (containsKey(columnName)){
            Object result = get(columnName);
            if (result!=null)
                return Boolean.valueOf(result.toString());
            return null;
        }
        throw new Exception(COLUMN_NOT_FOUND+"'"+columnName+"'");
    }
    
}
