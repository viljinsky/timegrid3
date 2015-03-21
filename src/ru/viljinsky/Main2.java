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
public class Main2 {
    
    public static void main(String[] args){
        IDataModule dataModule = DataModule.getInstance();
        try{
            dataModule.open();
            Dataset dataset = dataModule.getDataset("teacher");//dataModule.getSQLDataset("select * from teacher");
            dataset.open();
            for (Column column:dataset.getColumns()){
                System.out.println(column);
                System.out.println(dataset.getLookup(column.columnName));
                System.out.println(dataset.info.references);
            }
            dataset.getDetails();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
}
