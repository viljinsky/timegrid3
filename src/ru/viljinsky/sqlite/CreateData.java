
package ru.viljinsky.sqlite;

/**
 *
 * @author вадик
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;

/**
 * Чтение скрипта из файла
 * @author вадик
 */
abstract class SQLReader{
    
    public abstract void sqlredy(String sql) throws Exception;

    public void execute(String fileName) throws Exception{
        
        System.out.println("SCRIPT_EXECUTING \""+fileName +"\"");
        String line;
        StringBuilder script;
        BufferedReader reader = null;
        
        InputStream str = null;
        
        str = CreateData.class.getResourceAsStream(fileName);
        if (str==null){
            throw new Exception("SCRIPT_NOT_FOUND "+fileName);
        }
        
        try{
            reader = new BufferedReader(new InputStreamReader(str,"UTF-8"));
            script = new StringBuilder();
            while ((line=reader.readLine())!=null){
                line=line.replaceAll("--.*", "");
                if (!line.isEmpty()){
                    script.append(line).append("\n");
                }
            }
            
            String[] sqls = script.toString().split(";");
            for (String sql:sqls){
                if (!sql.trim().isEmpty())
                    sqlredy(sql.trim());
                
            }
            
        } finally {
            if (reader!=null) reader.close();
        }

    }
}

public class CreateData {
    
    public static final String[] scriptList = {
        "/ru/viljinsky/sql/schedule.sql",
        "/ru/viljinsky/sql/view.sql",
//        "/ru/viljinsky/sql/data.sql"
    };

    public static void newDatabase(String fileName) throws Exception{
        if (DataModule.active){
            throw new Exception("DATA_MODULE_IS_ACTIVE");
        }
        Class.forName("org.sqlite.JDBC");
        
        SQLReader reader = new SQLReader() {

            @Override
            public void sqlredy(String sql) throws Exception {
                DataModule.execute(sql);
            }
        };
        
        File file = new File(fileName);
        if (file.exists()){
            throw new Exception("FILE_ALREADY_EXISTS\n"+fileName);
        }
        Connection con =null;
        try{
            DriverManager.getConnection("jdbc:sqlite:"+fileName);
        }finally {
            if (con!=null) con.close();
        }
        
        if (file.exists()){
            try{
                DataModule.open(fileName);
                for (String script:scriptList){
                    reader.execute(script);
                }
                DataModule.commit();
                DataModule.close();
            } catch (Exception ex){
                file.delete();
                throw new Exception(ex);
            }
        }
    }
}
