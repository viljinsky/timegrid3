/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.reports;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import ru.viljinsky.sqlite.DataModule;
import ru.viljinsky.sqlite.Dataset;
import ru.viljinsky.sqlite.Recordset;
import ru.viljinsky.sqlite.Values;



/**
 *
 * @author вадик
 */
public class PageGenerator {
    private static final String SERVLET = "test";
    private static final String PAGE1 = "page1";
    private static final String PAGE2 = "page2";
    private static final String PAGE3 = "page3";
    private static final String PAGE4 = "page4";
    private static final String PAGE5 = "page5";
    private static final String PAGE6 = "page6";
    private static final String PAGE7 = "page7";
    Date currentDate = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM YYYY EEEE");
    Calendar calendar = Calendar.getInstance();

    public PageGenerator() {
        calendar.setTime(currentDate);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
    }

    private String getDepartLabel(int depart_id) throws Exception {
        Recordset r = DataModule.getRecordet("select label from depart where id=" + depart_id);
        return r.getString(0);
    }

    private String getTeacherFio(int teacher_id) throws Exception {
        Recordset r = DataModule.getRecordet("select last_name || ' ' || first_name || ' ' || patronymic from teacher where id=" + teacher_id);
        return r.getString(0);
    }

    private String getDayCaption(int day_id) throws Exception {
        Recordset r = DataModule.getRecordet("select day_caption from day_list where day_no=" + day_id);
        return r.getString(0);
    }

    private Dataset getLessons() throws Exception {
        return DataModule.getSQLDataset("select bell_id,time_start from bell_list");
    }

    public String getDefaultPage() {
        return "<h1>Расписание</h1>"
                + dateFormat.format(currentDate)
                + "<ul>"
                + "<li><a href='"
                + SERVLET+"?page="+PAGE1 
                + "'>Расписание классов</a></li>"
                + "<li><a href='"
                + SERVLET+"?page="+PAGE4
                + "'>Расписание перподавателей</a></li>"
                + "</ul>";
    }

    public String getDepartList() throws Exception {
        Dataset dataset = DataModule.getSQLDataset("select id,label from depart");
        Values values;
        dataset.open();
        StringBuilder result = new StringBuilder();
        result.append("<h2>Список классов</h2>" + "<ul>");
        for (int i = 0; i < dataset.size(); i++) {
            values = dataset.getValues(i);
            result.append("<li><a href='" + SERVLET+"?page="+ PAGE2 + "&depart_id=")
                  .append(values.getInteger("id"))
                  .append("'>")
                  .append(values.getString("label"))
                  .append("</li>");
        }
        result.append("</ul>");
        return result.toString();
    }
    private static final String SQL_DEPART_DAY_LIST = "select a.* from day_list a where exists (select * from schedule where day_id=a.day_no)";

    private Dataset getDepartDayList() throws Exception {
        return DataModule.getSQLDataset(SQL_DEPART_DAY_LIST);
    }

    /**
     * Дни класса
     * @param depart_id
     * @return
     * @throws Exception 
     */
    public String getDepartLessons(Integer depart_id) throws Exception {
        Dataset dataset = getDepartDayList();
        dataset.open();
        Values values;
        StringBuilder result = new StringBuilder();
        result.append("<h2>Список дней для класса " + getDepartLabel(depart_id) + "</h2>" + "<ul>");
        for (int i = 0; i < dataset.size(); i++) {
            values = dataset.getValues(i);
            result.append("<li><a href='" + SERVLET+"?page="+PAGE3 + "&depart_id=")
                  .append(depart_id)
                  .append("&day_id=")
                  .append(values.getInteger("day_no"))
                  .append("'>")
                  .append(values.getString("day_caption"))
                  .append("</a>")
                  .append("</li>");
        }
        result.append("<ul>").append("<a href='" + PAGE1 + "'>Назада</a>");
        return result.toString();
    }

    /**
     * Создание контента страницы "расписание занятий класса"
     * @param depart_id
     * @param day_id
     * @return  html-код контента
     * @throws Exception
     */
    public String getDepartSchedule(Integer depart_id, Integer day_id) throws Exception {
        String day_of_schedule = dateFormat.format(calendar.getTime()) + "(" + calendar.get(Calendar.WEEK_OF_YEAR) + " неделя)";
        Dataset schedule = DataModule.getSQLDataset("select * from v_schedule where depart_id=" + depart_id + " and day_id=" + day_id);
        schedule.open();
        Dataset bell_list = DataModule.getDataset("bell_list");
        bell_list.open();
        Values bells,filter,v;
        filter = new Values();
        StringBuilder result = new StringBuilder();
        result.append("<h2>Расписание</h2>")
                .append("<b>класс ")
                .append(getDepartLabel(depart_id))
                .append("</b>")
                .append("<table border='1' width='90%' align='center'>")
                .append("<tr><th colspan='5'>" + day_of_schedule + "</th></tr>");
        for (int count = 0; count < bell_list.size(); count++) {
            bells = bell_list.getValues(count);
            filter.put("bell_id", bells.getInteger("bell_id"));
            schedule.open(filter);
            result.append("<tr><td>").append(bells.getString("time_start")).append("</td>");
            if (schedule.size() == 0) {
                result.append("<td>&nbsp</td><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td>");
                result.append("</tr>");
            } else {
                for (int row = 0; row < schedule.size(); row++) {
                    if (row > 0) {
                        result.append("<tr><td>&nbsp;</td>");
                    }
                    v = schedule.getValues(row);
                    result.append("<td>").append(v.getString("subject_name")).append("</td><td>").append(v.getString("group_label")).append("</td><td>").append(v.getString("teacher")).append("</td><td>").append(v.getString("room")).append("</td>");
                    result.append("</tr>");
                }
            }
        }
        result.append("</table>");
        result.append("<a href='" + PAGE2 + "?depart_id=")
                .append(depart_id)
                .append("'>Список дней</a>&nbsp;")
                .append("<div align='center'>")
                .append("<a href='" + SERVLET+"?page="+PAGE3 + "&depart_id=")
                .append(depart_id)
                .append("&day=prior")
                .append("'>&lt;Назад</a>&nbsp;<a href='" + SERVLET+"?page="+PAGE3 + "&depart_id=")
                .append(depart_id).append("&day=next")
                .append("'>Вперёд&gt;</a>")
                .append("</div>");
        
        return result.toString();
    }
    private static final String SQL_TEACHER_LIST = "select a.id,a.last_name || ' ' || a.first_name ||' ' || a.patronymic as teacher_fio \n" + "from teacher a where exists(select * from schedule where teacher_id=a.id)\n" + "order by a.last_name || ' ' || a.first_name ||' ' || a.patronymic";

    public String getTeacherList() throws Exception {
        Dataset dataset = DataModule.getSQLDataset(SQL_TEACHER_LIST);
        dataset.open();
        Values values;
        StringBuilder result = new StringBuilder();
        result.append("<h2>Список преподавателей</h2>");
        result.append("<ul>");
        for (int i = 0; i < dataset.size(); i++) {
            values = dataset.getValues(i);
            result.append("<li><a href='" + SERVLET +"?page="+ PAGE5 + "&teacher_id=").append(values.getInteger("id")).append("'>").append(values.getString("teacher_fio")).append("</a></li>");
        }
        result.append("</ul>");
        return result.toString();
    }

    /**
     * Список дней преподавателей
     * @param teacher_id
     * @return
     * @throws Exception
     */
    private String getTeacherLessons(Integer teacher_id) throws Exception {
        Dataset dataset = DataModule.getSQLDataset("select distinct a.* from day_list a inner join shift_detail b on a.day_no=b.day_id inner join teacher t on t.shift_id=b.shift_id where t.id=" + teacher_id);
        dataset.open();
        Values values;
        StringBuilder result = new StringBuilder();
        result.append("<h2>" + getTeacherFio(teacher_id) + "</h2>" + "<ul>");
        for (int i = 0; i < dataset.size(); i++) {
            values = dataset.getValues(i);
            result.append("<li><a href='" + SERVLET+"?page="+PAGE6 + "&teacher_id=").append(teacher_id).append("&day_id=").append(values.getInteger("day_no")).append("'>").append(values.getString("day_caption")).append("</a></li>");
        }
        result.append("<ul>");
        return result.toString();
    }

    /**
     * Расписание преподавателя
     * @param teacher_id
     * @param day_id
     * @return
     * @throws Exception
     */
    private String getTeacherSchedule(Integer teacher_id, Integer day_id) throws Exception {
        String dateString = dateFormat.format(calendar.getTime());
        StringBuilder result = new StringBuilder();
        result.append("<h2>").append(getTeacherFio(teacher_id)).append("</h2>");
        result.append("<table border='1' width='90%' align='center'>");
        result.append("<tr><th colspan='5'>" + dateString + "</th></tr>");
        Dataset schedule = DataModule.getSQLDataset("select * from v_schedule where teacher_id=" + teacher_id + " and day_id=" + day_id);
        Values filter = new Values();
        filter.put("teacher_id", teacher_id);
        filter.put("day_id", day_id);
        Dataset lessons = getLessons();
        lessons.open();
        Values lesson;
        Values v;
        for (int count = 0; count < lessons.size(); count++) {
            lesson = lessons.getValues(count);
            result.append("<tr><td>" + lesson.getString("time_start") + "</td>");
            filter.put("bell_id", lesson.getInteger("bell_id"));
            schedule.open(filter);
            if (schedule.isEmpty()) {
                result.append("<td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>");
            } else {
                v = schedule.getValues(0);
                result.append("<td>").append(v.getString("subject_name")).append("</td><td>").append(v.getString("depart_label")).append("</td><td>").append(v.getString("group_label")).append("</td><td>").append(v.getString("room")).append("</td></tr>");
            }
        }
        result.append("</table>");
        
        result.append("<div align='center'>");
        
        result.append("<a href='" + SERVLET + "?page=" + PAGE7 + "&teacher_id=")
                .append(teacher_id)
                .append("&day=prior'>Назад</a>&nbsp;<a href='" + SERVLET+"?page="+PAGE7 + "&teacher_id=")
                .append(teacher_id)
                .append("&day=next" + "'>Верёд</a>");
        
        result.append("</div>");
        return result.toString();
    }

    public Map<String, String> getRequestParams(String request) {
        Map<String,String> result = new HashMap<>();
        String[] s= request.split("&");
        for (String k:s){
            result.put(k.split("=")[0], k.split("=")[1]);
        }
//        Map<String, String> result = new HashMap<>();
//        String[] s = request.split("\\?");
//        result.put("path", s[0]);
//        if (s.length > 1) {
//            for (String s1 : s[1].split("&")) {
//                String[] ss = s1.split("=");
//                result.put(ss[0], ss[1]);
//            }
//        }
        return result;
    }

    public String getResponce(){
        return getDefaultPage();
    }
    /**
     * Генерация тела страницы ответа
     * @param request Строка запроса
     * @return  Строка ответа
     */
    public String getResponce(String request) {
        Map<String, String> params = getRequestParams(request);
        String nextOrPrior;
//        String path = params.get("path");
        String path = params.get("page");
        String responce;
        Integer depart_id;
        Integer teacher_id;
        Integer day_id;
        nextOrPrior = params.get("day");
        if (nextOrPrior != null) {
            switch (nextOrPrior) {
                case "next":
                    calendar.add(Calendar.DATE, 1);
                    break;
                case "prior":
                    calendar.add(Calendar.DATE, -1);
                    break;
            }
        }
        try {
            switch (path) {
            // Список классов
                case PAGE1:
                    responce = getDepartList();
                    break;
            // Выбор дня для класса
                case PAGE2:
                    depart_id = Integer.valueOf(params.get("depart_id"));
                    responce = getDepartLessons(depart_id);
                    break;
            // Расписание на день для класса
                case PAGE3:
                    depart_id = Integer.valueOf(params.get("depart_id"));
                    day_id = calendar.get(Calendar.DAY_OF_WEEK) - 1;
                    if (day_id == 0) {
                        day_id = 7;
                    }
                    responce = getDepartSchedule(depart_id, day_id);
                    break;
            // Список преподавателей
                case PAGE4:
                    responce = getTeacherList();
                    break;
            // Список дней преподавателя
                case PAGE5:
                    teacher_id = Integer.valueOf(params.get("teacher_id"));
                    responce = getTeacherLessons(teacher_id);
                    break;
                case PAGE6:
                    calendar.setTime(new Date());
                    day_id = Integer.valueOf(params.get("day_id"));
                    while ((calendar.get(Calendar.DAY_OF_WEEK) - 1) != day_id) {
                        calendar.add(Calendar.DATE, 1);
                    }
                    teacher_id = Integer.valueOf(params.get("teacher_id"));
                    responce = getTeacherSchedule(teacher_id, day_id);
                    break;
            // Расписание преподавателя
                case PAGE7:
                    teacher_id = Integer.valueOf(params.get("teacher_id"));
                    day_id = calendar.get(Calendar.DAY_OF_WEEK) - 1;
                    if (day_id == 0) {
                        day_id = 7;
                    }
                    responce = getTeacherSchedule(teacher_id, day_id);
                    break;
                default:
                    responce = "<b>Неизвестная команда " + path + "</b>";
            }
        } catch (Exception e) {
            responce = "Ошибка\n" + e.getMessage();
        }
        return responce;
    }

    public String getPage(URL url) {
//        String request = url.getPath()+(url.getQuery()==null?"":"?"+url.getQuery());
//        if (request.startsWith("/")){
//            request = request.substring(1, request.length());
//        }
        String request = url.getQuery();
        System.out.println(request);
        return getDefaultPage() + getResponce(request);
        
//        return url.toString()+"<br>"+url.getQuery()+"<br>"+url.getPath()+"<br>"+url.getPath()+(url.getQuery()==null?"":url.getQuery());
    }

    
}
