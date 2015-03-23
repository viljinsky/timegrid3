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
    public void open() throws Exception;
    public void close() throws Exception;
    public boolean isEmpty();
    
    public String getTableName();
    public Integer getColumnCount();
    public Integer getRowCount();
    public Column getColumn(Integer columnIndex);
    public Map<String,Object> getValues(Integer rowIndex);
    public void setVlaues(Integer rowIndex,Map<String,Object> aValues) throws Exception;
    public Integer getColumnIndex(String columnName) throws Exception;
    
    public boolean isEditable();
    public Integer appned(Map<String,Object> values) throws Exception;
    public void edit(Integer rowIndex,Map<String,Object> values) throws Exception;
    public Boolean delete(Integer rowIndex);
    

    public String getReferences(String columnName);
    public Map<Object,String> getLookup(String columnName) throws Exception;
    public Column[] getColumns();
    
    /**
     * Установка фильтра на датасет 
     * @param filterMap карта фильтра <имя_роля><операция> = <шаблон>
     */
    public void setFilter(Map<String,Object> filterMap) throws Exception;
    public void setFiltered(boolean aFiltered);
    public boolean isFiltered();
    
}
    

