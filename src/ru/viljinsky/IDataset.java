/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky;

import java.util.Map;
import java.util.Set;

/**
 *
 * @author вадик
 */

public interface IDataset{
    public boolean isActive();
    public void open() throws Exception;
    public void open(Map<String,Object> filter) throws Exception;
    public boolean test() throws Exception;
    public void close() throws Exception;
    public boolean isEmpty();
    
    public String getTableName();
    public Integer getColumnCount();
    public Integer getRowCount();
    public Column getColumn(Integer columnIndex);
    public Column getColumn(String columnName);
    public Values getValues(Integer rowIndex);
    public Values getNewValues();
    public void setVlaues(Integer rowIndex,Map<String,Object> aValues) throws Exception;
    public Integer getColumnIndex(String columnName) throws Exception;
    
    public boolean isEditable();
    public Integer appned(Map<String,Object> values) throws Exception;
    public void edit(Integer rowIndex,Map<String,Object> values) throws Exception;
    public Boolean delete(Integer rowIndex) throws Exception;
    

    public String getReferences(String columnName);
    public Map<Object,String> getLookup(String columnName) throws Exception;
    public Column[] getColumns();
    public Integer locate(Values values) throws Exception;
    
    /**
     * Установка фильтра на датасет 
     * @param filterMap карта фильтра <имя_роля><операция> = <шаблон>
     * @throws java.lang.Exception
     */
    public void setFilter(Map<String,Object> filterMap) throws Exception;
    public void setFiltered(boolean aFiltered);
    public boolean isFiltered();
    
    /**
     * Получение сета данных поля из всего датасета
     * @param columnName имя поля
     * @return 
     */
    public Set<Object> getColumnSet(String columnName);
    
}
    

