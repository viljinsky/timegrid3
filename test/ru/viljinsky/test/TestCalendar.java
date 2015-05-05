/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 *
 * @author вадик
 */
public class TestCalendar {
    public static void main(String[] args){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("y MMMM dd EEEE");
        for (int i=0;i<30;i++){
            System.out.println(dateFormat.format(calendar.getTime())+" "+(Integer)(calendar.get(Calendar.DAY_OF_WEEK)-1));
            calendar.add(Calendar.DATE, 1);
        }
    }
    
}
