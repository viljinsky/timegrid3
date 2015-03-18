/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class DataModule {
    List<Dataset> datasetList;
    List<DatasetInfo> infoList = new ArrayList<>();
    Connection con = null;
    private static DataModule instance = null;
    private DataModule(){
        datasetList = new ArrayList<>();
    }
    
    public static DataModule getInstance(){
        if (instance==null){
            instance = new DataModule();
        }
        return instance;
    }
    
    public String[] getTableNames(){
        String[] result = new String[infoList.size()];
        int i=0;
        for (DatasetInfo info:infoList){
            result[i++]=info.tableName;
        }
        return result;
    }
    
    public Dataset getTable(String tableName){
        Dataset dataset;
        for (DatasetInfo info:infoList){
            if (info.tableName.equals(tableName)){
                dataset = new Dataset(info);
                datasetList.add(dataset);
                return dataset;
            }
        }
        return null;
    }
    
    public Dataset getQuery(String sql){
        DatasetInfo info = new DatasetInfo();
        info.selectSQL = sql;
        Dataset dataset = new Dataset(info);
        datasetList.add(dataset);
//        dataset.dataModule = this;
        return dataset;
    }
    
    public void open() throws Exception{
        open("example.db");
    }
    
    public void open(String fileName) throws Exception{
        File file = new File(fileName);
        if (!file.exists()){
            throw new Exception("file "+fileName+" not found");
        }
        
        try{
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e){
            throw new Exception("driver not found\n"+e.getMessage());
        }
        
        try{
            con = DriverManager.getConnection(String.format("jdbc:sqlite:%s",fileName));
            DatabaseMetaData meta = con.getMetaData();
            ResultSet rs = meta.getTables(null,null,null, new String[]{"TABLE"});
            String tableName;
            DatasetInfo info = new DatasetInfo();
            while (rs.next()){
                tableName = rs.getString("TABLE_NAME");
                info = new DatasetInfo(tableName,meta);
                infoList.add(info);
            }
            
        } catch (SQLException e){
            throw new Exception ("Ошбка приокрытии бд:\n"+e.getMessage());
        }
        
    }
    
    public void close() throws Exception{
        
    }
    
    public static void main(String[] args){
        DataModule dm = DataModule.getInstance();
        Dataset dataset;
        try{
            dm.open();
            for (String tableName:dm.getTableNames()){
                dataset = dm.getTable(tableName);
                dataset.test();
                dataset.open();
                dataset.print();
            }
            
        } catch (Exception e){
            e.printStackTrace();
        }
        
    }
}
