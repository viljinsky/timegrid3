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
public class Test3 {
    public static void main(String[] args) throws Exception{
        DataModule dataModule = DataModule.getInstance();
        Dataset dataset;
        dataModule.open();
//        for (DatasetInfo info: dataModule.infoList ){
//            info.print();
//        }
        
        
        dataset=dataModule.getSQLDataset("select * from schedule");
        dataset.info.print();
        System.out.println(dataset.info.columns);
        dataset.open();
        dataset.print();
//        
//        dataset =dataModule.getSQLDataset("select * from teacher");
//        dataset.info.print();
//        System.out.println(dataset.info.columns);
//        
//        dataset =dataModule.getDataset("v_teacher");
//        dataset.info.print();
//        System.out.println(dataset.info.columns);
//        
//        dataset = dataModule.getSQLDataset("select * from shift_detail");
//        dataset.info.print();
        
//        dataset = dataModule.getSQLDataset("SELECT typeof(t), typeof(nu), typeof(i), typeof(r), typeof(no) FROM t1");
        
//        dataset = dataModule.getSQLDataset("select * from t1");
//        dataset.info.print();
                
//        dataset = dataModule.getDataset("t1");
//        dataset.info.print();
        
            
        
    }
    
}
