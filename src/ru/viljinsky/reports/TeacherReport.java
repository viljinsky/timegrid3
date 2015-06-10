/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.reports;

import ru.viljinsky.sqlite.DataModule;
import ru.viljinsky.sqlite.Dataset;
import ru.viljinsky.sqlite.Recordset;
import ru.viljinsky.sqlite.Values;

/**
 * Отчет РАСПИСАНИЕ ПРЕПОДАВАТЕЛЕЙ
 * @author вадик
 */
class TeacherReport extends AbstractReport {
    Dataset schedule;
    Dataset day_list;
    Dataset bell_list;
    String  REPORT_TYTLE = "Расписание преподавателей";

    /**
     * Таблица расписания преподавателя
     * @param teacher_id
     * @return
     * @throws Exception
     */
    public String getTeacherTable(Integer teacher_id) throws Exception {
        String emptyString ="";
        StringBuilder result = new StringBuilder();
        int min_bell_id,max_bell_id,recNo;
        Recordset r;
        r = DataModule.getRecordet("select teacher_fio,profile_name from v_teacher where id=" + teacher_id + ";");
        result.append("<h2>").append(r.getString(0)).append("</h2>");
        result.append("<div>").append(r.getString(1)).append("</div>");
        result.append("<table border='1' width='90%' align='center'>");
        result.append("<tr><th>Время</th><th>Предмет</th><th>Класс</th><th>Група</th><th>Кабинет</th><th>Здание</th></tr>");
        // заголовок
        day_list = DataModule.getSQLDataset("select * from day_list where exists (select * from schedule where day_id=day_list.day_no and teacher_id=" + teacher_id + ");");
        day_list.open();
        
        if (day_list.size()==0)
            return emptyString;
        
        String day_caption;
        Integer day_id;
        String bell_caption;
        Integer bell_id;
        Values map = new Values();
        for (int row = 0; row < day_list.size(); row++) {
            day_caption = day_list.getValues(row).getString("day_caption");
            day_id = day_list.getValues(row).getInteger("day_no");
            r = DataModule.getRecordet("select min(bell_id),max(bell_id) from schedule where day_id=" + day_id + " and teacher_id=" + teacher_id + ";");
            min_bell_id = r.getInteger(0);
            max_bell_id = r.getInteger(1);
            bell_list = DataModule.getSQLDataset("select * from bell_list where bell_id between " + min_bell_id + " and " + max_bell_id + ";");
            bell_list.open();
            result.append("<tr>").append("<td colspan='6'>").append(day_caption).append("</td>").append("</tr>");
            for (int col = 0; col < bell_list.size(); col++) {
                bell_caption = bell_list.getValues(col).getString("time_start");
                bell_id = bell_list.getValues(col).getInteger("bell_id");
                result.append("<tr>").append("<td>").append(bell_caption).append("</td>");
                map.put("teacher_id", teacher_id);
                map.put("day_id", day_id);
                map.put("bell_id", bell_id);
                recNo = schedule.locate(map);
                if (recNo >= 0) {
                    Values v = null;
                    v = schedule.getValues(recNo);
                    result.append("<td>").append(v.getString("subject_name")).append("</td><td>").append(v.getString("depart_label")).append("</td><td>").append(v.getString("group_label")).append("</td><td>").append(v.getString("room")).append("</td><td>").append(v.getString("building")).append("</td>");
                } else {
                    result.append("<td colspan='5' align='center'>****</td>");
                }
                result.append("</tr>");
            }
        }
        result.append("<tr>").append("<td colspan='6'>").append("&nbsp").append("</dt>").append("</tr>").append("</table>");
        return result.toString();
    }

    @Override
    public void prepare() throws Exception {
        StringBuilder result = new StringBuilder();
        result.append("<h1>"+REPORT_TYTLE+"</h1>");
        result.append(getReportHeader());
        
        schedule = DataModule.getDataset("v_schedule");
        schedule.open();
        Dataset teacher = DataModule.getDataset("v_teacher");
        teacher.open();
        Values values;
        String subReport ;
        for (int i = 0; i < teacher.size(); i++) {
            values = teacher.getValues(i);
            subReport = getTeacherTable(values.getInteger("id"));
            if (!subReport.isEmpty())
                result.append(subReport);
        }
        html = result.toString();
    }
    
}
