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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DataModule {
    List<Dataset> datasetList;
    List<String> tableNames;
    Connection con = null;
    private static DataModule instance = null;
    private DataModule(){
        datasetList = new ArrayList<>();
        tableNames = new ArrayList<>();
    }
    
    public static DataModule getInstance(){
        if (instance==null){
            instance = new DataModule();
        }
        return instance;
    }
    
    public String[] getTableNames(){
        return tableNames.toArray(new String[tableNames.size()]);
    }
    
    public Dataset getTable(String tableName){
        Dataset dataset = new Dataset();
        datasetList.add(dataset);
        dataset.dataModule = this;
        dataset.tableName =  tableName;
        dataset.selectSQL="select * from "+tableName;
        return dataset;
    }
    
    public Dataset getQuery(String sql){
        Dataset dataset = new Dataset();
        datasetList.add(dataset);
        dataset.dataModule = this;
        dataset.tableName = "no table";
        dataset.selectSQL=sql;
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
                tableNames.add(tableName);
                info = new DatasetInfo();
                info.tableName=tableName;
                ResultSet rs1 = meta.getImportedKeys(null,null, tableName);
                while (rs1.next()){
                    info.references.put(rs1.getString("FKCOLUMN_NAME"), rs1.getString("PKTABLE_NAME")+"."+rs1.getString("PKCOLUMN_NAME"));
                }
                
                rs1 = meta.getPrimaryKeys(null, null, tableName);
                while (rs1.next())
                    info.addPrimaryKey(rs1.getString("COLUMN_NAME"));
                System.out.println(info);

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
            
//            for (Dataset dataset:dm.datasetList){
//                System.out.println(dataset.tableName);
//                for (int col:dataset.columns.keySet()){
//                    System.out.println(col+".  "+dataset.columns.get(col));
//                }
//                
//                dataset.open();
//                Object[] rowset;
//                for (int i=0;i<dataset.size();i++){
//                    rowset=dataset.get(i);
//                    for (int j=0;j<dataset.getColumnCount();j++){
//                        System.out.println(rowset[j]);
//                    }
//                }
//                
//            }
        } catch (Exception e){
            e.printStackTrace();
        }
        
    }
}
