/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.reports;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author вадик
 */
public class PageProducer {
    String DEFAULT_PATTERN = 
                  "<!DOCTYPE html><html lang='ru'>"
                + "<head>"
                + "<title>$TYTLE$</title>"
                + "$STYLE$" 
                + "</head>"
                + "<body>$BODY$</body>"
                + "</html>";
    String htmlPattern;
    
    public PageProducer(){
        htmlPattern=DEFAULT_PATTERN;
    }
    public void setHtmlPattern(String htmlPattern){
        this.htmlPattern=htmlPattern;
    }
    public void loadPattern(String fileName) throws Exception{
        BufferedReader bufr = null;
        String line;
        StringBuilder result = new StringBuilder();
        try{
            bufr=new BufferedReader(new FileReader(new File(fileName)));
            while ((line=bufr.readLine())!=null){
                result.append(line);
            }
            htmlPattern=result.toString();
        } catch (Exception e){
            if (bufr!=null)
                bufr.close();
        }
        
    }
    
    public String execute(){
        String result = htmlPattern;
        Pattern p = Pattern.compile("\\$.*?\\$");
        Matcher m = p.matcher(htmlPattern);
        int start,end;
        String tag;
        String substr;
        Map<String,String> map = new HashMap<>();
        while (m.find()){
            start=m.start();
            end=m.end();
            tag = htmlPattern.substring(start, end);
            substr = getReplaceText(tag);
            map.put(tag, substr);
        }
        for (String s:map.keySet()){
            result=result.replace(s, map.get(s));
        }
        return result;
    }
    
    public String getReplaceText(String tag){
        System.out.println(tag);
        return "xxx";
    }
    
    public static void main(String[] args) throws Exception{
        PageProducer producer=new PageProducer();
        producer.loadPattern("d:\\development\\schedule2\\site\\site3\\pattern.html");
        String html = producer.execute();
        System.out.println(html);
    }
    
}




