/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author вадик
 */

public class Dataset extends ArrayList<Object[]> implements IDataset {
    DataModule dataModule = DataModule.getInstance();
    DatasetInfo info;
    Map<Integer, Column> columns = new HashMap<>();
    Boolean active = false;
    Boolean editable = false;

    @Override
    public boolean isEditable(){
        return editable;
    }
    public Dataset(DatasetInfo info) {
        this.info=info;
    }

    @Override
    public String getTableName() {
        return info.tableName;
    }
    
    @Override
    public Column getColumn(Integer columnIndex){
        return columns.get(columnIndex);
    }
    
    @Override
    public Integer getRowCount(){
        return this.size();
    }
    
    @Override
    public Map<String,Object> getValues(Integer rowIndex){
        Map<String,Object> result = new HashMap<>();
        Object[] rowset;
        rowset = get(rowIndex);
        Column column;
        for (int col:columns.keySet()){
            column=columns.get(col);
            result.put(column.columnName,rowset[col]);
        }
        return result;
    }
    
    @Override
    public void setVlaues(Integer rowIndex, Map<String, Object> aValues) throws Exception {
        Object[] rowset = get(rowIndex);
        for (String column:aValues.keySet()){
            rowset[getColumnIndex(column)]=aValues.get(column);
        }
    }    
    
    @Override
    public Integer getColumnCount() {
        return columns.size();
    }

    @Override
    public Integer getColumnIndex(String columnName) throws Exception{
        for (Integer col:columns.keySet()){
            if (columns.get(col).columnName.equals(columnName))
                return col;
        }
        throw new Exception ("Поле '"+columnName+"' не найдено");
    }
    

    public String getColumnName(Integer columnIndex) {
        return columns.get(columnIndex).columnName;
    }

    @Override
    public void open() throws Exception {
        if (test()){
            Statement stmt = null;
            try {
                stmt = dataModule.con.createStatement();
                ResultSet rs = stmt.executeQuery(info.selectSQL);
                Object[] rowset;
                while (rs.next()) {
                    rowset = new Object[getColumnCount()];
                    for (int i = 0; i < rowset.length; i++) {
                        rowset[i] = rs.getObject(i + 1);
                    }
                    add(rowset);
                }
                this.active = true;
            } finally {
            }
        }
    }

    @Override
    public void close() throws Exception {
        this.clear();
        this.active = false;
    }

    public Column getColumnByName(String columnName) {
        Column column;
        for (int col : columns.keySet()) {
            column = columns.get(col);
            if (column.columnName.equals(columnName)) {
                return column;
            }
        }
        return null;
    }

    public boolean test() throws Exception {
        Statement stmt = null;
        try {
            stmt = dataModule.con.createStatement();
            ResultSet rs = stmt.executeQuery(info.selectSQL);
            ResultSetMetaData rsmeta = rs.getMetaData();
            for (int i = 0; i < rsmeta.getColumnCount(); i++) {
                columns.put(i, new Column(rsmeta, i));
            }
            
            editable = !info.primaryKey.isEmpty();
            if (editable){
                for (String p:info.primaryKey.split(";"))
                    getColumnByName(p).primary=true;
                
            
            
                // create appendSQL
                String s1 = "";
                String s2 = "";
                for (int col:columns.keySet()){
                    if (!s1.isEmpty()) s1+=",";
                    s1+=columns.get(col).columnName;
                    if (!s2.isEmpty()) s2+=",";
                    s2+="?";
                }

                // creare deleteSQL
                String s3 = "";
                String[] sss =info.primaryKey.split(";");
                for (String s:sss){
                    if (!s3.isEmpty()) s3+=" and ";
                    s3+=s+"=?";
                }

                // create updateSQL
                String strSet="",strWhere="";
                for (Integer col:columns.keySet()){
                    if (!strSet.isEmpty()) strSet+=",";
                    strSet += columns.get(col).columnName+"= ?";
                }

                for (Integer col:columns.keySet()){
                    Column column = columns.get(col);
                    if (column.isPrimary()){
                        if (!strWhere.isEmpty()) strWhere+=" and ";
                        strWhere += column.columnName +"=?";
                    }
                }


                info.insertSQL="insert into "+info.tableName +"("+s1+") values ("+s2+");";
                info.deleteSQL="delete from "+info.tableName +" where "+s3+";";
                info.updateSQL="update "+info.tableName+" set "+strSet+" where "+strWhere;

//                System.out.println(info.insertSQL);
//                System.out.println(info.deleteSQL);
//                System.out.println(info.updateSQL);
//                System.out.println();
            }
            
            
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt!=null)
                stmt.close();
        }
        return false;
    }
    
    
    public void print(){
        Map<String,Object> values;
        if (!active){
            System.out.println("Dataset not active");
        }
        System.out.println(info.selectSQL);
        for (int i=0;i<getRowCount();i++){
            values = getValues(i);
            for (String columnName:values.keySet()){
                System.out.print(columnName+" = '"+values.get(columnName)+"' ");
            }
            System.out.println();
            
        }
        
    }

    @Override
    public Integer appned() {
        Object[] rowset = new Object[getColumnCount()];
        add(rowset);
        return  indexOf(rowset);
    }

    @Override
    public Integer appned(Map<String, Object> values) throws Exception {
        
        String sql = info.insertSQL;
        PreparedStatement pstmt = dataModule.con.prepareStatement(sql);
        
        int n=1;
        for (String k:values.keySet()){
            pstmt.setObject(n++, values.get(k));
        }
        
        pstmt.execute();
        
        
        Object[] rowset = new Object[getColumnCount()];
        for (String columnName:values.keySet()){
            rowset[getColumnIndex(columnName)]= values.get(columnName);
        }
        add(rowset);
        return indexOf(rowset);
    }

    @Override
    public Boolean delete(Integer rowIndex) {
        Object[] rowset = get(rowIndex);
        
        try{
            PreparedStatement pstmt = dataModule.con.prepareStatement(info.deleteSQL);
            String[] keys = info.primaryKey.split(";");
            int n=1;
            for (String k:keys){
                pstmt.setObject(n++, rowset[getColumnIndex(k)]);
            }
            pstmt.execute();
        
        } catch (Exception e){
            e.printStackTrace();
        }
        
        return remove(rowset);
    }

    @Override
    public void edit(Integer rowIndex, Map<String, Object> values) throws Exception {
        Object[] oldValues = get(rowIndex);
        Map<Integer,Object> keyMap = new HashMap<>();
        int n=1;
        for (int col:columns.keySet()){
            keyMap.put(n++, values.get(columns.get(col).columnName));
        }
        for (int col:columns.keySet()){
            if (columns.get(col).isPrimary()){
                keyMap.put(n++, oldValues[col]);
            }
        }
        
        PreparedStatement pstmt = dataModule.con.prepareStatement(info.updateSQL);
        for (int m:keyMap.keySet()){
            pstmt.setObject(m, keyMap.get(m));
        }
        pstmt.execute();
        
        setVlaues(rowIndex, values);
    }

    @Override
    public String getReferences(String columnName) {
        return info.references.get(columnName);
    }
    
    public Map<Object,String> getLookup(String columnName) throws Exception{
        Map<Object,String> map = new HashMap<>();
        String lookup = info.references.get(columnName);
        if (lookup== null)
            return null;
        String tName = lookup.split("\\.")[0];
        String cName = lookup.split("\\.")[1];
        System.out.println("-->"+tName + "  "+ cName);
        String rName = cName;
        switch (tName){
            case "shift":
                rName = "name";
                break;
            case "subject":
                rName = "subject_name";
                break;
            case "group_type":
                rName ="group_type_caption";
                break;
            case "skill":
                rName = "caption";
                break;
            case "curriculum":
                rName = "caption";
                break;
            case "profile":
                rName = "name";
                break;
        }
                
        Dataset lookupDataset = dataModule.getDataset(tName);
        lookupDataset.open();
        Map<String,Object> values;
        for (int i=0;i<lookupDataset.getRowCount();i++){
            values = lookupDataset.getValues(i);
            try{
            map.put(values.get(cName),values.get(rName).toString());
            } catch (Exception e){
                System.err.println(tName+" "+cName );
            }
            
        }
        System.out.println(map);
        return map;
    }

}
