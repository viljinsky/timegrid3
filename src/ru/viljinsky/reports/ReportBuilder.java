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
    
    /**
     * Отчёт Расписание по классам
     * Для каждого класса своя сетка
     * @return
     * @throws Exception 
     */
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
    
    public String getScheduleCell2(int day_no,int bell_id,int depart_id) throws Exception{
        Values filter = new Values();
        filter.put("day_id", day_no);
        filter.put("bell_id", bell_id);
        filter.put("depart_id", depart_id);
        schedule.open(filter);
        if (schedule.isEmpty())
            return "&nbsp;";
        Values values = schedule.getValues(0);
        return values.getString("subject_name");
    } 
    
    
    /**
     * Отчет по классам общий
     *  Все классы в одной сетке
     * @return
     * @throws Exception 
     */
    public String getSchedueReport2() throws Exception{
        day_list = DataModule.getDataset("day_list");
        day_list.open();
        bell_list=DataModule.getDataset("bell_list");
        bell_list.open();
        depart = DataModule.getDataset("depart");
        depart.open();
        schedule = DataModule.getSQLDataset("select day_id,bell_id,depart_id,subject_name from schedule a inner join subject b on a.subject_id=b.id");
        
        StringBuilder result = new StringBuilder();
        
        result.append("<table border='1' width='90%' align='center'>");
        Values values;
        // заголовки колонок
        
            result.append("<tr>");
            result.append("<td>");            
            result.append("&nbsp;");
            result.append("</td>");
            for (int i=0;i<depart.size();i++){
                values = depart.getValues(i);
                result.append("<td>");
                result.append(values.getString("label"));
                result.append("</td>");
            }
            result.append("</tr>");
        
        // строки
        
            Values dayValues,bellValues;
            for (int day_no=0;day_no<day_list.size();day_no++){
                dayValues=day_list.getValues(day_no);
                
                // заголовок  строка день
                result.append("<tr>");
                result.append("<td colspan='"+(Integer)(depart.size()+1)+"'>");
                result.append(dayValues.getString("day_caption"));
                        
                result.append("</td>");                    
                result.append("</tr>");
                
                
                for (int bell_id=0;bell_id<bell_list.size();bell_id++){
                    result.append("<tr>");
                    bellValues = bell_list.getValues(bell_id);
                    result.append("<td>");
                    result.append(bellValues.getString("time_start"));
                    result.append("</td>");
                    
                    for (int i=0;i<depart.size();i++){
                        values=depart.getValues(i);
                        result.append("<td>");
                        result.append(getScheduleCell2(day_no+1, bell_id+1, values.getInteger("id")));
                        result.append("</td>");
                        
                    }
                    result.append("</tr>");
                    
                }
                // пустая строка
                result.append("<tr>");
                result.append("<td colspan='"+(Integer)(depart.size()+1)+"'>");
                result.append("</td>");                    
                result.append("</tr>");
            }
        
        // заголовки строк
        
        result.append("</table>");
        
        
        return result.toString();
    }
    
}
