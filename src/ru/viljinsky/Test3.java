/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.Map;

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
        
        
//        dataset=dataModule.getDataset("teacher");
//        dataset.info.print();
//        System.out.println(dataset.info.columns);
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
        
        String s = "12.3";
        Integer n ;
        Float f;
        
        
        
        try{
            n =  Integer.parseInt(s);
            System.out.println(n+" "+n.getClass().getName());
        } catch (Exception e){
            e.printStackTrace();
        }
            
        
    }
    
}
