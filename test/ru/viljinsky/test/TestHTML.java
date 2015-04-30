/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import ru.viljinsky.DataModule;
import ru.viljinsky.Dataset;
import ru.viljinsky.Recordset;
import ru.viljinsky.Values;

/**
 *
 * @author вадик
 */

class PageGenerator{
    
    public PageGenerator(){
    }
    
    private String getDepartLabel(int depart_id) throws Exception{
        Recordset r = DataModule.getRecordet("select label from depart where id="+depart_id);
        return r.getString(0);
    }
    
    private String getTeacherFio(int teacher_id) throws Exception{
        Recordset r = DataModule.getRecordet("select last_name || ' ' || first_name || ' ' || patronymic from teacher where id="+teacher_id);
        return r.getString(0);
    }
    
    private String getDayCaption(int day_id) throws Exception{
        Recordset r = DataModule.getRecordet("select day_caption from day_list where day_no="+day_id);
        return r.getString(0);
    }
    
    private Dataset getLessons() throws Exception{
        return DataModule.getSQLDataset("select bell_id,time_start from bell_list");
    }
    
    public String getDepartList() throws Exception{
        Dataset dataset = DataModule.getSQLDataset("select id,label from depart");
        Values values;
        dataset.open();
        StringBuilder result = new StringBuilder();
        result.append("<h2>Список классов</h2>"+
               "<ul>");
        for (int i=0;i<dataset.size();i++){
           values=dataset.getValues(i);
            result.append("<li><a href='page2.html?depart_id=")
                    .append(values.getInteger("id"))
                    .append("'>")
                    .append(values.getString("label"))
                    .append("</li>");
        }
        result.append("</ul>");
        return result.toString();
    }
    
    public String getDepartLessons(Integer depart_id) throws Exception{
        Dataset dataset = DataModule.getSQLDataset("select * from day_list");
        dataset.open();
        Values values;
        StringBuilder result = new StringBuilder();
        result.append("<h2>Список дней для класса "+getDepartLabel(depart_id)+"</h2>"+
                "<ul>");
        for (int i=0;i<dataset.size();i++){    
            values = dataset.getValues(i);
            result.append("<li><a href='page3.html?depart_id=").append(depart_id)
                    .append("&day_id=")
                    .append(values.getInteger("day_no"))
                    .append("'>")
                    .append(values.getString("day_caption"))
                    .append("</a>")
                    .append("</li>");
        }
        result.append("<ul>")
                .append("<a href='page1.html'>Назада</a>")
                ;
        return result.toString();
    }
    
    public String getDepartSchedule(Integer depart_id,Integer day_id) throws Exception{
        Dataset schedule = DataModule.getSQLDataset("select * from v_schedule where depart_id="+depart_id+" and day_id="+day_id);
        schedule.open();
        Dataset bell_list = DataModule.getDataset("bell_list");
        bell_list.open();
        Values bells,v,filter;
        filter = new Values();
                    
        StringBuilder result = new StringBuilder();
                result.append("<h2>Расписание</h2>");
                
                result.append("<b>класс ")
                        .append(getDepartLabel(depart_id))
                        .append(" день ")
                        .append(getDayCaption(day_id))
                        .append("</b>"+
                "<table border='1' width='90%' align='center'>");
                
                for (int count=0;count<bell_list.size();count++){
                    bells = bell_list.getValues(count);
                    filter.put("bell_id",bells.getInteger("bell_id"));
                    schedule.open(filter);
                    
                   
                    if (schedule.size()==0){
                        result.append(
                        "<tr>"
                            + "<td>"+bells.getString("time_start")+"</td><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td>"
                        + "</tr>");
                    } else {
                        v=schedule.getValues(0);
                        result.append(
                        "<tr>"
                            + "<td>"+bells.getString("time_start")+"</td><td>"+v.getString("subject_name")+"</td><td>"+v.getString("teacher")+"</td><td>"+v.getString("room")+"</td>"
                        + "</tr>");
                    }
                }
                result.append( "</table>"
                + "<a href='page2.html?depart_id="+depart_id+"'>Список дней</a>&nbsp;")
                ;
                int prior_day,next_day;
                prior_day = day_id-1;
                next_day = day_id+1;
                result.append("<a href='page3.html?depart_id=")
                        .append(depart_id)
                        .append("&day_id=")
                        .append(prior_day)
                        .append("'>&lt;Назад</a>&nbsp<a href='page3.html?depart_id=")
                        .append(depart_id)
                        .append("&day_id=")
                        .append(next_day)
                        .append("'>Вперёд&gt;</a>");
        return result.toString();
    }
    
    public String getTeacherList() throws Exception{
        Dataset dataset = DataModule.getSQLDataset("select id,last_name || ' ' || first_name ||' ' || patronymic as teacher_fio from teacher");
        dataset.open();
        Values values;
        StringBuilder result = new StringBuilder();
        result.append("<h2>Список преподавателей</h2>");
                result.append("<ul>");
                for (int i=0;i<dataset.size();i++){
                    values=dataset.getValues(i);
                    result.append("<li><a href='page5.html?teacher_id=")
                            .append(values.getInteger("id"))
                            .append("'>")
                            .append(values.getString("teacher_fio"))
                            .append("</a></li>");
                }
                result.append("</ul>");
        return result.toString();
    }
    private String getTeacherLessons(Integer teacher_id) throws Exception{
        Dataset dataset = DataModule.getSQLDataset("select distinct a.* from day_list a inner join shift_detail b on a.day_no=b.day_id inner join teacher t on t.shift_id=b.shift_id where t.id="+teacher_id);
        dataset.open();
        Values values;
        StringBuilder result = new StringBuilder();
        result.append("<h2>Дни занятий для перподавателя"+getTeacherFio(teacher_id)+"</h2>"
                +"<ul>");
        for (int i=0;i<dataset.size();i++){
            values = dataset.getValues(i);
               result.append("<li><a href='page6.html?teacher_id=")
                       .append(teacher_id)
                       .append("&day_id=")
                       .append(values.getInteger("day_no"))
                       .append("'>")
                       .append(values.getString("day_caption"))
                       .append("</a></li>");
        }               
        result.append("<ul>");
                
        return result.toString();
    }

    private String getTeacherSchedule(Integer teacher_id, Integer day_id) throws Exception{
        StringBuilder result = new StringBuilder();
        result.append("<h2>Распиание  ")
                .append(getTeacherFio(teacher_id))
                .append("</h2>");
        
        result.append("<table border='1' width='90%' align='center'>");
       
        result.append("<tr><th colspan='5'>"+getDayCaption(day_id)+"</th></tr>");
        
        Dataset schedule = DataModule.getSQLDataset("select * from v_schedule where teacher_id="+teacher_id+" and day_id="+day_id);
        Values filter = new Values();
        filter.put("teacher_id", teacher_id);
        filter.put("day_id",day_id);
        
        Dataset lessons = getLessons();
        lessons.open();
        Values lesson;
        Values v;
        for (int count=0;count<lessons.size();count++){
            lesson = lessons.getValues(count);
            result.append("<tr><td>"+lesson.getString("time_start")+"</td>");
            
            filter.put("bell_id", lesson.getInteger("bell_id"));
            schedule.open(filter);
            if (schedule.isEmpty())
                result.append("<td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>");
            else{
                v = schedule.getValues(0);
                result.append("<td>")
                        .append(v.getString("subject_name"))
                        .append("</td><td>")
                        .append(v.getString("depart_label"))
                        .append("</td><td>")
                        .append(v.getString("group_label"))
                        .append("</td><td>")
                        .append(v.getString("room"))
                        .append("</td></tr>");
            }
        }
        result.append("</table>");
        
        result.append("<a href='page6.html?teacher_id="
                +teacher_id
                +"&day_id="
                +(Integer)(day_id-1)
                +"'>Назад</a>&nbsp;<a href='page6.html?teacher_id="
                +teacher_id
                +"&day_id="
                +(Integer)(day_id+1)
                +"'>Верёд</a>");
        
        return result.toString();
    }
    
    
    public String getParam(String request,String paramName){
        String[] s = request.split("\\?");
        if (s.length>1){
            for (String s1:s[1].split("&")){
                String[] s2 = s1.split("=");
                if (s2[0].equals(paramName))
                    return s2[1];
            }
        }
        return null;
    }
    
    public String service(String request){
        String[] part = request.split("\\?");
        String path = part[0];
        String responce;
        
        Integer depart_id,teacher_id;
        Integer day_id;
        try{
            switch(path){
                
                // Список классов
                case "page1.html":
                    responce = getDepartList();
                    break;
                // Выбор дня для класса    
                case "page2.html":
                    depart_id = Integer.valueOf(getParam(request, "depart_id"));
                    responce = getDepartLessons(depart_id);
                    break;
                    
                // Расписание на день для класса   
                case "page3.html":
                    depart_id = Integer.valueOf(getParam(request, "depart_id"));
                    day_id = Integer.valueOf(getParam(request,"day_id"));
                    responce = getDepartSchedule(depart_id, day_id);
                    break;

                // Список преподавателей    
                case "page4.html":
                    responce = getTeacherList();
                    break;

                case "page5.html":
                    teacher_id = Integer.valueOf(getParam(request, "teacher_id"));
                    responce = getTeacherLessons(teacher_id);

                    break;
                case "page6.html":
                    teacher_id = Integer.valueOf(getParam(request, "teacher_id"));
                    day_id = Integer.valueOf(getParam(request, "day_id"));
                    responce = getTeacherSchedule(teacher_id,day_id);
                    break;


                default:
                    responce = "<b>Немивестная команда "+path+"</b>";
            }
        } catch (Exception e){
            responce = "Ошибка\n"+e.getMessage();
        }
        return responce;
    }
    
    
    public String getIndexPage(){
        return "<h1>Привет генератор страниц</h1>"+
                "<ul>"+
                "<li><a href='page1.html'>Расписание классов</a></li>"+
                "<li><a href='page4.html'>Расписание перподавателей</a></li>"+
                "</ul>";
    }

}

public class TestHTML extends JPanel{
    JTextPane textPane = new JTextPane();
    PageGenerator generator = new PageGenerator();
    public TestHTML(){
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(800,600));
        add(new JScrollPane(textPane),BorderLayout.CENTER);
        textPane.setEditable(false);
        textPane.addHyperlinkListener(new HyperlinkListener() {

            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType()==HyperlinkEvent.EventType.ACTIVATED){
                    System.out.println(e.getDescription());
                    doCommand(e.getDescription());
                }
            }
        });
        
        textPane.setContentType("text/html");
        textPane.setText(generator.getIndexPage());
        try{
            DataModule.open();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    public void doCommand(String request){
        String responce = generator.service(request);
        textPane.setText(generator.getIndexPage()+responce);
    }
    
    public static void main(String[] args){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new TestHTML());
        frame.pack();
        frame.setVisible(true);
    }
    
}
