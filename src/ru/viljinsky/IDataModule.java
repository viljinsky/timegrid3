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
public interface IDataModule{
    public void open() throws Exception;
    public void close() throws Exception;
    public boolean isActive();
    // Получение датасетов
    
    // Список имён базовых таблиц
    public String[] getTableNames();
    // Получение набора записей базовой таблицы
    public Dataset getDataset(String tableName) throws Exception;
    // Получение набора записей из запроса
    public Dataset getSQLDataset(String sql) throws Exception;
    //Получение набора записей из запроса с параметрами
    public Dataset getSQLDataset(String sql,KeyMap params) throws Exception;
    // Выполнение запроса
    public void execute(String sql) throws Exception; 
     // Выполнение запроса с параметрами
    public void execute(String sql,KeyMap params) throws Exception;
}

