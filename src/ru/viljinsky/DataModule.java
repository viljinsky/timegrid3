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
import java.util.List;

//class DataMap extends HashMap<String,Object>{
//}


interface IDataModuleConsts {
    public static final String  DATABASE_NOT_ACTIVE = "База данных не открыта";
    public static final String  DATABASE_IS_ACTIVE  = "База открыта";
    public static final String  FILE_NOT_FOUND = "Файл \"%s\" не найден";
    public static final String TABLE_NOT_FOUND = "TABLE_NOT_FOUND";
    public static final String DEFAULT_DATA = "example.db";
}

public class DataModule implements IDataModuleConsts {
    private static DataModule instance = null;
    static Boolean active = false;
    static List<Dataset> datasetList = new ArrayList<>();
    static List<DatasetInfo> infoList = new ArrayList<>();
    
    private static Connection con = null;
    
    public static Connection getConnection(){
        return con;
    }
    
    public static Statement createStatement() throws Exception{
        return con.createStatement();
    }
    
    
    public static List<DatasetInfo> getInfoList(){
        return infoList;
    }
    
    public static DataModule getInstance(){
        if (instance==null){
            instance = new DataModule();
        }
        return instance;
    }
    
    public boolean isTableExists(String tableName){
        for (DatasetInfo info:infoList)
            if (info.isTable() && info.tableName.equals(tableName))
                return true;
        return false;
    }
    
    public static boolean isActive(){
        return active;
    }
    
    public static void open() throws Exception{
        open(DEFAULT_DATA);
    }
    
    public static void reopen() throws Exception{
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
    
    public static void open(String fileName) throws Exception{
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
            con.setAutoCommit(false);
        } catch (SQLException e){
            throw new Exception ("Ошбка приокрытии бд:\n"+e.getMessage());
        }
        
    }
    
    public static void close() throws Exception{
        if (!active)
            throw new Exception(DATABASE_NOT_ACTIVE);
        for (Dataset dataset:datasetList){
            dataset.close();
        }
        
        datasetList.clear();
        infoList.clear();
        con.close();
        con=null;
        active = false;
    }

    public static String[] getTableNames() throws Exception{
        if (!active)
            throw new Exception(DATABASE_NOT_ACTIVE);
        String[] result = new String[infoList.size()];
        int i=0;
        for (DatasetInfo info:infoList){
            result[i++]=info.tableName;
        }
        return result;
    }
    
    public static Dataset getDataset(String tableName) throws Exception{
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
        throw new Exception(TABLE_NOT_FOUND+"\n\""+tableName+"\"");
    }
    
    public static Dataset getSQLDataset(String sql){
        DatasetInfo info = new DatasetInfo();
        info.tableType="SQL";
        
        Statement stmt = null;
        ResultSet rs = null;
        ResultSet sr1;
        ResultSetMetaData rsmeta;
        DatabaseMetaData dbm;
        Column column;
        try{
            stmt = con.createStatement();
            rs= stmt.executeQuery(sql+" limit 1");
            rsmeta = rs.getMetaData();
            dbm = con.getMetaData();
            
            for (int i=0;i<rsmeta.getColumnCount();i++){
                column = new Column();
                rsmeta.getColumnName(i+1);
                column.columnIndex= i+1;
                column.tableName=rsmeta.getTableName(i+1);
                column.columnName=rsmeta.getColumnName(i+1);
                if (column.columnName.contains(".")){
                    column.columnName=column.columnName.split("\\.")[1];
                }
//                column.columnLabel = rsmeta.get
                column.columnLabel=rsmeta.getColumnLabel(i+1);
                column.columnTypeName=rsmeta.getColumnTypeName(i+1);
                column.columnType = rsmeta.getColumnType(i+1);
                
                sr1 = dbm.getColumns(null, null, column.tableName, column.columnName);
                while (sr1.next()){
                    column.columnTypeName=sr1.getString("TYPE_NAME");
                    break;
                }
                sr1.close();
                
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
            if (rs!=null) try{rs.close();} catch (Exception e){}
            if (stmt!=null) try{ stmt.close();} catch (Exception e){};
        }
        
        
        info.selectSQL = sql;
        Dataset dataset = new Dataset(info);
        
        datasetList.add(dataset);
        return dataset;
    }
    
    public static Dataset getSQLDataset(String sql,KeyMap params){
        return null;
    }
    
    
    //--------------------------------------------------------------------------
    
    public static void commit() throws SQLException{
        con.commit();
    }
    
    public static void rollback() throws SQLException{
        con.rollback();
    }
    
    public static void execute(String sql) throws Exception{
        Statement stmt=null;
        try{
            stmt=con.createStatement();
            stmt.execute(sql);
        } catch (Exception e){
            System.err.println(sql);
            throw new Exception(e);
        } finally {
            if (stmt!=null)
                try {
                    stmt.close();
                } catch (Exception e){
                    System.out.println("OOPS"+e.getMessage());
                }
        }
    }
    
    public static void execute(String sql,KeyMap params) throws Exception{
        PreparedStatement pstmt=null;
        try{
            pstmt=con.prepareStatement(sql);
            for (Integer key:params.keySet()){
                pstmt.setObject(key, params.get(key));
            }
            pstmt.execute();
        } catch (SQLException e){
            System.err.println(sql);
            System.err.println(params);
            throw new Exception(e);
        
        } finally {
            if (pstmt!=null)
                try {
                    pstmt.close();
                } catch (Exception e){
                    System.err.println("OOOOPS!!!\n"+e.getMessage());
                }
        } 
        
    }
    
    public static Recordset getRecordet(String sql) throws Exception{
        Statement stmt= null;
        ResultSet rs = null;
        Recordset result = new Recordset();
        Object[] rowset;
        Integer columnCount;
        try{
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            ResultSetMetaData meta = rs.getMetaData();
            columnCount=meta.getColumnCount();
            while (rs.next()){
                rowset = new Object[columnCount];
                for (int col=0;col<columnCount;col++)
                    rowset[col]=rs.getObject(col+1);
                result.add(rowset);
            }
        } finally{
            if (rs!=null){
                rs.close();
            }
            if (stmt!=null){
                stmt.close();
            }
        }
        return result;
    }
    
    public static void main(String[] args){
        Dataset dataset;
        try{
            open();
            for (String tableName:getTableNames()){
                dataset = getDataset(tableName);
                dataset.test();
                dataset.open();
                dataset.print();
            }
            
        } catch (Exception e){
            e.printStackTrace();
        }
        
    }
    
    
}
