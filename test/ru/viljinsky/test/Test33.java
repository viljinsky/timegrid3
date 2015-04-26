/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.test;

import ru.viljinsky.Column;
import ru.viljinsky.DataModule;
import ru.viljinsky.Dataset;

/**
 *
 * @author вадик
 */
public class Test33 {
    public static void main(String[] args) throws Exception{
        DataModule.open();
        Dataset  dataset = DataModule.getDataset("v_teacher_hour");
        dataset.test();
        for (Column column:dataset.getColumns()){
            column.print();
        }
        
    }
    
}
