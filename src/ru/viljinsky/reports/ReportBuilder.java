/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.reports;

import ru.viljinsky.DataModule;
import ru.viljinsky.Dataset;
import ru.viljinsky.Values;

/**
 *
 * @author вадик
 */
public class ReportBuilder {

    Dataset day_list;
    Dataset bell_list;
    Dataset schedule;
    Dataset depart;
    
    public String getScheduleCell(int row,int col) throws Exception{
        Values values;
        for (int i=0;i<schedule.size();i++){
            values = schedule.getValues(i);
            if (values.getInteger("day_id")-1==col && values.getInteger("bell_id")-1==row){
                return values.getString("subject_name");
            }
        }
        
        return "&nbsp;";
    }
    
    public String getScheduleGrid() throws Exception{
        day_list = DataModule.getDataset("day_list");
        day_list.open();
        bell_list = DataModule.getDataset("bell_list");
        bell_list.open();
        StringBuilder result = new StringBuilder();

        result.append("<table border='1' align='center' width='90%'>");
        Values values;
        result.append("<tr>");
        
        result.append("<td>");
        result.append("&nbsp;");
        result.append("</td>");
     
        // заголовок колонок
        for (int i=0;i<day_list.size();i++){
            values = day_list.getValues(i);
            result.append("<th>");
            result.append(values.getString("day_caption"));
            result.append("</th>");
        }
        result.append("</tr>");
        
        for (int row=0;row<bell_list.size();row++){
            result.append("<tr>");
            
            // заголовок строк
            values = bell_list.getValues(row);
            result.append("<th nowrap>");
            result.append(values.getString("time_start"));
            result.append("</th>");
            
            // значения
            for (int col=0;col<day_list.size();col++){
                result.append("<td>");
                result.append(getScheduleCell(row, col));
                result.append("</td>");
            }
            result.append("</tr>");
        }
        result.append("</table>");
        return result.toString();
    }
    
    public String getScheduleReport() throws Exception{
        schedule = DataModule.getSQLDataset("select a.day_id,a.bell_id,b.subject_name,a.depart_id from schedule a inner join subject b on a.subject_id=b.id");
        StringBuilder resultText = new StringBuilder();
        
        Values values,filter;
        
        filter = new Values();
        depart = DataModule.getDataset("depart");
        depart.open();
                
        for (int i=0;i<depart.size();i++){
            values = depart.getValues(i);
            filter.put("depart_id", values.getInteger("id"));
            schedule.open(filter);

            resultText.append("<h3>");
            resultText.append(values.getString("label"));
            resultText.append("</h3>");
            
            
            resultText.append(getScheduleGrid());
            resultText.append("<br>");
        
        }
        
        return resultText.toString();
    }
    
}
