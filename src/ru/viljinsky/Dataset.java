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
import java.util.List;
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
    public Map<String,Object> getNewValues(){
        Map<String,Object> result = new HashMap<>();
        for (int i : columns.keySet()){
            result.put(getColumnName(i), null);
        }
        return result;
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
            clear();
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
                    if (!filtered || checkFilter(rowset))
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
        ResultSet rs;
        try {
            stmt = dataModule.con.createStatement();
            try{
                rs = stmt.executeQuery(info.selectSQL+" limit 1;");
            } catch (Exception e){
                throw new Exception("TestError:\n"+e.getMessage());
            }
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
//            throw new Exception(e.getMessage());
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


    /**
     * Для SQLite получение нового значение 
     * @return 
     */
    public Integer getNextValue(){
        Statement stmt=null;
        ResultSet rs =null;
        try{
            stmt = dataModule.con.createStatement();
            rs = stmt.executeQuery("select seq from sqlite_sequence where name ='"+getTableName()+"';");
            while (rs.next()){
                return rs.getInt("seq")+1;
            }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if (rs!=null) try {rs.close();} catch (SQLException e){};
            if (stmt!=null) try {stmt.close();} catch (SQLException e){};
        }
        return 1;
    }
    
    @Override
    public Integer appned(Map<String, Object> values) throws Exception {
        // проверка автоинкремента
        Column column ;
        PreparedStatement pstmt = null;
        try{
            for (int col:columns.keySet()){
                column=columns.get(col);
                if (column.autoIncrement){
                    if (values.get(column.columnName)==null){
                        values.put(column.columnName, getNextValue());
                    }
                }
            }

            String sql = info.insertSQL;
            pstmt = dataModule.con.prepareStatement(sql);

            int n=1;
            for (String k:values.keySet()){
                pstmt.setObject(n++, values.get(k));
            }

            try{
                pstmt.execute();
            } catch (Exception e){
                System.err.println(sql);
                System.err.println(values);
                throw new Exception("SQL ERROR\n:"+e.getMessage());
            }


            Object[] rowset = new Object[getColumnCount()];
            for (String columnName:values.keySet()){
                rowset[getColumnIndex(columnName)]= values.get(columnName);
            }
            add(rowset);
            return indexOf(rowset);
        }finally {
            if (pstmt!=null);pstmt.close();
        }
    }

    @Override
    public Boolean delete(Integer rowIndex) throws Exception {
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
            throw new Exception("DATASET_DELETE_ERROR:\n"+e.getMessage());
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

    private static final String[] strLookupMap={
        "shift=name",
        "subject=subject_name",
        "group_type=group_type_caption",
        "skill=caption",
        "curriculum=caption",
        "profile=name",
        "shift_type=caption",
        "profile_type=caption",
        "day_list=day_caption",
        "bell_list=time_start"
    } ;
    
    public Map<String,String> getLookupMap(){
        Map<String,String> result= new HashMap<>();
        String key,value;
        String s[];
        for (String line:strLookupMap){
            s=line.split("=");
            key=s[0];value=s[1];
            result.put(key, value);
        }
        return result;
    }
    
    @Override
    public Map<Object,String> getLookup(String columnName) throws Exception{
        
        Map<Object,String> map = new HashMap<>();
        String lookup = info.references.get(columnName);
        if (lookup== null)
            return null;
        String tName = lookup.split("\\.")[0];
        String cName = lookup.split("\\.")[1];
        String rName = cName;
        rName = getLookupMap().get(tName);
        if (rName == null)
            rName = cName;
        
                
        Dataset lookupDataset = dataModule.getDataset(tName);
        lookupDataset.open();
        Map<String,Object> values;
        for (int i=0;i<lookupDataset.getRowCount();i++){
            values = lookupDataset.getValues(i);
            if (values!=null){
                try{
                    map.put(values.get(cName),values.get(rName).toString());
                } catch (Exception e){
                    System.err.println(tName+" "+cName );
                }
            }
            
        }
        return map;
    }

    @Override
    public Column[] getColumns() {
        Column[] result = new Column[columns.size()];
        for (int i=0;i<result.length;i++){
            result[i]=columns.get(i);
        }
        return result;
    }

    public Map<String,String> getDetails(){
        Map<String,String> result = new HashMap<>();
        Map<String,String> map;
        for (DatasetInfo info:dataModule.infoList){
            map=info.references;
            String rf;
            for (String columnName:map.keySet()){
                rf=map.get(columnName).split("\\.")[0];
                if (rf.equals(this.info.tableName)){
                    System.out.println(info.tableName+" "+columnName);
                    result.put(info.tableName, columnName);
                }
            }
        }
        return result;
    }

    public Dataset[] getForeignDataset() {
        List<Dataset> list = new ArrayList<>();
        Dataset dataset;
        Map<String,String> map = new HashMap<>();
        for (DatasetInfo info:dataModule.infoList){
            map=info.references;
            String rf;
            for (String columnName:map.keySet()){
                dataset = null;
                rf=map.get(columnName).split("\\.")[0];
                if (rf.equals(this.info.tableName)){
                    try{
                        dataset = dataModule.getDataset(info.tableName);
                        dataset.test();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    if (dataset!=null)
                        list.add(dataset);
                }
            }
        }
        
        return list.toArray(new Dataset[list.size()]);
        
    }
    //////////////////////    FILTER ///////////////////////////////////////////
    
    boolean filtered = false;
    Map<Integer,Object> filter;

    @Override
    public void setFilter(Map<String, Object> filterMap) throws Exception{
        filter = new HashMap<>();
        for (String columnName:filterMap.keySet())
            try{
                filter.put(getColumnIndex(columnName), filterMap.get(columnName));
            } catch (Exception e){
                throw  new Exception ("ОШИБКА установка фильтра:\n"+e.getMessage());
            }
        filtered = !filter.isEmpty();
    }

    @Override
    public void setFiltered(boolean aFiltered) {
        
    }

    @Override
    public boolean isFiltered() {
        return filtered;
    }
    
    public boolean checkFilter(Object[] rowset){
        Object v;
        for (int i:filter.keySet()){
            v=filter.get(i);
            if (v==null)
                return false;
            if (!v.equals(rowset[i]))
                return false;
        }
        return true;
    }
}
