/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.viljinsky.dialogs.EntryForm;

/**
 *
 * @author вадик
 */
public class TestTag {
    public static String source=null;
    
    /**
     * Прочитать источник
     * @param fileName
     * @throws Exception 
     */
    public static void readSource(String fileName) throws Exception{
        StringBuilder result = new StringBuilder();
        
        File file = new File(fileName);
        if (!file.exists()){
            throw new Exception("FILE_NOT_FOUND\n"+fileName);
        }
        
        BufferedReader bufr = null;
        String line;
        try{
            bufr = new BufferedReader(new FileReader(file));
            while ((line=bufr.readLine())!=null){
                result.append(line+"\n");
            }
            source = result.toString();
        } finally {
            if (bufr!=null)
                bufr.close();
        }
    }
    
    /**
     * Получить набор тегов вида $имя_тега$
     * @return 
     */
    public static  Set<String>  getTagSet(){
        Set<String> tagSet = new HashSet<>();
        if (source!=null){
            Pattern p = Pattern.compile("\\$.*?\\$");
            Matcher m = p.matcher(source);
            while (m.find()){
                tagSet.add(source.substring(m.start(), m.end()));
            }
        }
        return tagSet;
    }
    
    public static String getTagText(String tag){
        String result;
        String t = "\\$"+ tag.substring(1,tag.length()-1)+"\\$";
        Pattern p=Pattern.compile(t+".*"+t,Pattern.DOTALL);
        Matcher m = p.matcher(source);
        if (m.find()){
            result = source.substring(m.start(), m.end());
            return result.substring(tag.length(),result.length()-tag.length());
        }
        return null;
    }
    
    public static void execute(String sourceFile){
        try{
            readSource(sourceFile);
            
            for (String tag :getTagSet()){
                System.out.println("\""+getTagText(tag)+"\"\n");
            }
            
            System.out.println(getTagSet());
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
    }
    
    public static void main(String[] args) throws Exception{
        String sourceFile = "D:\\development\\schedule2\\site\\share.txt";
        String[] fields = new String[]{"source;Шаблон;FC_PATH"};
        EntryForm form = new EntryForm(){

            @Override
            public void doOnEntry() throws Exception {
                execute(getValue("source").toString());
            }
            
        };
        form.setFields(fields);
        form.setValue("source",sourceFile);
        form.showModal(null);
//        execute();
    }
    
}
