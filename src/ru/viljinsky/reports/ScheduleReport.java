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
 *
 * @author вадик
 */
class ScheduleReport extends AbstractReport {
    Dataset day_list;
    Dataset bell_list;
    Dataset hall_list;
    Dataset schedule;
    Dataset depart;

    private String getRoomLabel(int depart_id, int day_id, int bell_id) throws Exception {
        Recordset r = DataModule.getRecordet("select distinct room_name from room a inner join schedule b on a.id=b.room_id\n" + "where day_id=" + day_id + " and bell_id=" + bell_id + " and depart_id=" + depart_id);
        String room_label = "";
        if (r.isEmpty()) {
            room_label = "?";
        } else {
            for (int i = 0; i < r.size(); i++) {
                if (!room_label.isEmpty()) {
                    room_label += "<br>";
                }
                room_label += r.get(i)[0].toString();
            }
        }
        return room_label;
    }

    private String getScheduleCell(int depart_id, int bell_id, int day_id) throws Exception {
        String room_label = getRoomLabel(depart_id, day_id, bell_id);
        Values values = new Values();
        values.put("day_id", day_id);
        values.put("bell_id", bell_id);
        if (hall_list.locate(values) >= 0) {
            return "<td colspan='2' align='center'>****</td>";
        }
        for (int i = 0; i < schedule.size(); i++) {
            values = schedule.getValues(i);
            if (values.getInteger("day_id") == day_id && values.getInteger("bell_id") == bell_id) {
                return "<td nowrap>" + values.getString("subject_name") + "</td><td nowrap>" + room_label + "</td>";
            }
        }
        return "<td colspan='2'>&nbsp;</td>";
    }

    /**
     * Расписание занятий по классам
     * каждый класс расположен в одельной таблице
     * @return
     * @throws Exception
     */
    public String getScheduleReport(Integer depart_id) throws Exception {
        Integer day_id;
        Integer bell_id;
        Values values;
        Values filter;
        String sql;
        sql = "select distinct a.* from day_list a inner join shift_detail b\n" + "on a.day_no=b.day_id inner join depart d on d.shift_id=b.shift_id where d.id=" + depart_id;
        day_list = DataModule.getSQLDataset(sql);
        day_list.open();
        sql = "select distinct a.* from bell_list a inner join shift_detail b on a.bell_id=b.bell_id \n" + "inner join depart d on d.shift_id=b.shift_id where d.id=" + depart_id;
        bell_list = DataModule.getSQLDataset(sql);
        bell_list.open();
        StringBuilder result = new StringBuilder();
        //        result.append(getReportHeader());
        Recordset r = DataModule.getRecordet("select label from depart where id=" + depart_id);
        filter = new Values();
        filter.put("depart_id", depart_id);
        schedule.open(filter);
        hall_list.open(filter);
        result.append("<h3>").append(r.getString(0)).append("</h3>");
        result.append("<table>").append("<tr>").append("<td>").append("&nbsp;").append("</td>");
        // заголовок колонок
        for (int i = 0; i < day_list.size(); i++) {
            values = day_list.getValues(i);
            result.append("<th colspan='2'>").append(values.getString("day_caption")).append("</th>");
        }
        result.append("</tr>");
        for (int row = 0; row < bell_list.size(); row++) {
            bell_id = bell_list.getValues(row).getInteger("bell_id");
            result.append("<tr>");
            // заголовок строк
            values = bell_list.getValues(row);
            result.append("<th nowrap>").append(values.getString("time_start")).append("</th>");
            // значения
            for (int col = 0; col < day_list.size(); col++) {
                day_id = day_list.getValues(col).getInteger("day_no");
                result.append(getScheduleCell(depart_id, bell_id, day_id));
            }
            result.append("</tr>");
        }
        result.append("</table>");
        return result.toString();
    }

    @Override
    public void prepare() throws Exception {
        hall_list = DataModule.getSQLDataset(SQL_ERROR_HALL_IN_SCHEDULE);
        hall_list.open();
        schedule = DataModule.getSQLDataset("select a.day_id,a.bell_id,b.subject_name,a.depart_id from schedule a inner join subject b on a.subject_id=b.id");
        StringBuilder result = new StringBuilder();
        result.append("<h1>Расписание занятий по классам</h1>");
        result.append(getReportHeader());
        Values values;
        depart = DataModule.getDataset("depart");
        depart.open();
        for (int i = 0; i < depart.size(); i++) {
            values = depart.getValues(i);
            result.append(getScheduleReport(values.getInteger("id")));
            result.append("<br>");
        }
        html = result.toString();
    }
    
}
