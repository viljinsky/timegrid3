/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky;

import java.util.Map;

/**
 *
 * @author вадик
 */

public interface IDataset{
    public String getTableName();
    public Integer getColumnCount();
    public Integer getRowCount();
    public Column getColumn(Integer columnIndex);
    public Map<String,Object> getValues(Integer rowIndex);
    public void setVlaues(Integer rowIndex,Map<String,Object> aValues) throws Exception;
    public Integer getColumnIndex(String columnName) throws Exception;
    
    public void open() throws Exception;
    public Integer appned();
    public Integer appned(Map<String,Object> values) throws Exception;
    public Boolean delete(Integer rowIndex);
    public void close() throws Exception;

}
    

