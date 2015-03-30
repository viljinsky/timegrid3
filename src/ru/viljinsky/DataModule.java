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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class DataMap extends HashMap<String,Object>{
}

interface IDataModuleConsts {
    public static final String  DATABASE_NOT_ACTIVE = "База данных не открыта";
    public static final String  DATABASE_IS_ACTIVE  = "База открыта";
    public static final String  FILE_NOT_FOUND = "Файл \"%s\" не найден";
}

public class DataModule implements IDataModule,IDataModuleConsts {
    private static DataModule instance = null;
    Boolean active = false;
    List<Dataset> datasetList;
    List<DatasetInfo> infoList = new ArrayList<>();
    Connection con = null;

    private DataModule(){
        datasetList = new ArrayList<>();
    }
    
    public Connection getConnection(){
        return con;
    }
    
    public List<DatasetInfo> getInfoList(){
        return infoList;
    }
    
    public static DataModule getInstance(){
        if (instance==null){
            instance = new DataModule();
        }
        return instance;
    }
    
    @Override
    public boolean isActive(){
        return active;
    }
    
    @Override
    public void open() throws Exception{
        open("example.db");
    }
    
    public void reopen() throws Exception{
        if (!active)
            throw new Exception(DATABASE_NOT_ACTIVE);
        for (Dataset dataset:datasetList)
            dataset.close();
        datasetList.clear();
        infoList.clear();
        //    duplicate
        DatabaseMetaData meta = con.getMetaData();
        ResultSet rs = meta.getTables(null,null,null, new String[]{"TABLE","VIEW"});
        String tableName;
        DatasetInfo info;
        while (rs.next()){
            tableName = rs.getString("TABLE_NAME");
            info = new DatasetInfo(tableName,meta);
            info.tableType = rs.getString("TABLE_TYPE");
            infoList.add(info);
        }
            
    }
    
    public void open(String fileName) throws Exception{
        if (active)
            throw new Exception(DATABASE_IS_ACTIVE);
        File file = new File(fileName);
        if (!file.exists()){
            throw new Exception(String.format(FILE_NOT_FOUND, fileName));
        }
        
        try{
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e){
            throw new Exception("driver not found\n"+e.getMessage());
        }
        
        try{
            con = DriverManager.getConnection(String.format("jdbc:sqlite:%s",fileName));
            // duplicate
            DatabaseMetaData meta = con.getMetaData();
            ResultSet rs = meta.getTables(null,null,null, new String[]{"TABLE","VIEW"});
            String tableName;
            DatasetInfo info;
            while (rs.next()){
                tableName = rs.getString("TABLE_NAME");
                info = new DatasetInfo(tableName,meta);
                info.tableType = rs.getString("TABLE_TYPE");
                infoList.add(info);
            }
            // 
            active = true;
            execute("PRAGMA foreign_keys = ON;");
            
        } catch (SQLException e){
            throw new Exception ("Ошбка приокрытии бд:\n"+e.getMessage());
        }
        
    }
    
    @Override
    public void close() throws Exception{
        if (!active)
            throw new Exception(DATABASE_NOT_ACTIVE);
        for (Dataset dataset:datasetList){
//            System.out.println(dataset.getTableName());
            dataset.close();
        }
//        for (Dataset dataset:datasetList){
//            dataset.close();
//        }
        
        datasetList.clear();
        infoList.clear();
        con.close();
        con=null;
        active = false;
    }
    
    
    
    @Override
    public String[] getTableNames() throws Exception{
        if (!active)
            throw new Exception(DATABASE_NOT_ACTIVE);
        String[] result = new String[infoList.size()];
        int i=0;
        for (DatasetInfo info:infoList){
            result[i++]=info.tableName;
        }
        return result;
    }
    
    @Override
    public Dataset getDataset(String tableName) throws Exception{
        if (!active)
            throw new Exception(DATABASE_NOT_ACTIVE);
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
    
    @Override
    public Dataset getSQLDataset(String sql){
        DatasetInfo info = new DatasetInfo();
        info.tableType="SQL";
        
        Statement stmt = null;
        ResultSet rs = null;
        Column column;
        try{
            stmt = con.createStatement();
            rs= stmt.executeQuery(sql+" limit 1");
            ResultSetMetaData rsmeta = rs.getMetaData();
            DatabaseMetaData dbm = con.getMetaData();
            
            for (int i=0;i<rsmeta.getColumnCount();i++){
                column = new Column();
                rsmeta.getColumnName(i+1);
                column.columnIndex= i+1;
                column.tableName=rsmeta.getTableName(i+1);
                column.columnName=rsmeta.getColumnName(i+1);
                column.columnLabel=rsmeta.getColumnLabel(i+1);
                column.columnTypeName=rsmeta.getColumnTypeName(i+1);
                column.columnType = rsmeta.getColumnType(i+1);
                
                ResultSet sr1 = dbm.getColumns(null, null, column.tableName, column.columnName);
                while (sr1.next()){
                    column.columnTypeName=sr1.getString("TYPE_NAME");
                    break;
                }
                
                switch(column.columnTypeName){
                    
                    case  "INTEGER":
                        column.columnClass=Integer.class;
                        break;
                        
                    case "FLOAT":
                        column.columnClass=Float.class;
                        break;
                        
                    case "NUMERIC":
                        column.columnClass=Double.class;
                        break;
                        
                    case "BOOLEAN":
                        column.columnClass=Boolean.class;
                        break;
                        
                    default:
                        column.columnClass=Object.class;
                }
                info.columns.put(i, column);
            }
            
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (stmt!=null) try{ stmt.close(); } catch (Exception e){};
        }
        
        
        info.selectSQL = sql;
        Dataset dataset = new Dataset(info);
        
        datasetList.add(dataset);
//        dataset.dataModule = this;
        return dataset;
    }
    
    @Override
    public Dataset getSQLDataset(String sql,KeyMap params){
        return null;
    }
    
    
    //--------------------------------------------------------------------------
    public void startTrans() throws Exception{
        con.setAutoCommit(false);
    }
    
    public void stopTrans() throws Exception{
        try{
            try{
                con.commit();                
            } catch (SQLException e){
                con.rollback();
                throw new Exception("Ошибка сохранения\n"+e.getMessage());
            }
        } finally {
            con.setAutoCommit(true);
        }
    }
    
    
    @Override
    public void execute(String sql) throws Exception{
        Statement stmt=null;
        try{
            stmt=con.createStatement();
            stmt.execute(sql);
        } catch (Exception e){
            throw new Exception(e.getMessage());
        } finally {
            if (stmt!=null) try {stmt.close();} catch (Exception e){}
        }
    }
    
    @Override
    public void execute(String sql,KeyMap params) throws Exception{
        PreparedStatement pstmt=null;
        try{
            pstmt=con.prepareStatement(sql);
            for (Integer key:params.keySet()){
                pstmt.setObject(key, params.get(key));
            }
            pstmt.execute();
        } catch (Exception e){
            throw new Exception(e.getMessage());
        } finally {
            if (pstmt!=null) try {pstmt.close();} catch (Exception e){}
        }
        
    }
    
    public static void main(String[] args){
        DataModule dm = DataModule.getInstance();
        Dataset dataset;
        try{
            dm.open();
            for (String tableName:dm.getTableNames()){
                dataset = dm.getDataset(tableName);
                dataset.test();
                dataset.open();
                dataset.print();
            }
            
        } catch (Exception e){
            e.printStackTrace();
        }
        
    }
    
}
