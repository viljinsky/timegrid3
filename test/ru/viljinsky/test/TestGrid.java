/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.test;

import javax.swing.JPanel;
import ru.viljinsky.sqlite.Column;
import ru.viljinsky.sqlite.DataModule;
import ru.viljinsky.sqlite.Dataset;
import ru.viljinsky.sqlite.DatasetInfo;

/**
 *
 * @author вадик
 */


/**
 * Класс создаёт словарь полеё ColumnMap
 * @author вадик
 */
public class TestGrid  extends JPanel{
    
    public static void main(String[] args) throws Exception{
            DataModule.open();

            Dataset dataset;
            for (DatasetInfo info:DataModule.getInfoList()){
                dataset = DataModule.getDataset(info.getTableName());
                System.out.println("// "+info.getTableName());
                for (Column column:dataset.getColumns()){
                    System.out.println(String.format("{\"%s.%s\",\"\"},", column.getTableName(),column.getColumnName()));
                }
                System.out.println();
            }
            
    };
    
}
