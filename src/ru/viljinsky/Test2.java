/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;

/**
 *
 * @author вадик
 */
public class Test2 {
    public static void main(String[] args){
        String sql = "";
        URL url = Test2.class.getResource("template.sql");
        if (url!=null){
            File file = new File(url.getFile());
            try{
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line ;
                while ((line=br.readLine())!=null){
                    if (!line.isEmpty()){
                        sql+=line;
                        if (line.contains(";")){
                            System.out.println("---->\n"+sql+"\n----->\n");
                            sql="";
                        }
                    }
                }
                
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        
    }
}
