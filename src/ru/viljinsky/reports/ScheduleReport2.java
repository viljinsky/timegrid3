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
class ScheduleReport2 extends AbstractReport {
    Dataset hall_list;
    Dataset day_list;
    Dataset bell_list;
    Dataset depart;
    Dataset schedule;
    String REPORT_TYTLE = "Расписание занятий (вар 2)";

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

    /**
     * Возвращает ячейки subject_name room_labale
     * @param depart_id
     * @param day_id
     * @param bell_id
     * @return
     * @throws Exception
     */
    private String getScheduleCell2(int depart_id, int day_id, int bell_id) throws Exception {
        Values filter = new Values();
        filter.put("day_id", day_id);
        filter.put("bell_id", bell_id);
        filter.put("depart_id", depart_id);
        if (hall_list.locate(filter) >= 0) {
            return "<td colspan='2' align='center'>****</td>";
        }
        schedule.open(filter);
        if (schedule.isEmpty()) {
            return "<td colspan='2'>&nbsp;</td>";
        }
        String room_label = getRoomLabel(depart_id, day_id, bell_id);
        Values values = schedule.getValues(0);
        return "<td>" + values.getString("subject_name") + "</td><td nowrap>" + room_label + "</td>";
    }

    @Override
    public void prepare() throws Exception {
        hall_list = DataModule.getSQLDataset(SQL_ERROR_HALL_IN_SCHEDULE);
        hall_list.open();
        String sql = "select distinct a.* from day_list a inner join shift_detail b on a.day_no=b.day_id inner join depart d on d.shift_id=b.shift_id";
        day_list = DataModule.getSQLDataset(sql);
        day_list.open();
        bell_list = DataModule.getDataset("bell_list");
        bell_list.open();
        depart = DataModule.getDataset("depart");
        depart.open();
        schedule = DataModule.getSQLDataset("select day_id,bell_id,depart_id,subject_name from schedule a inner join subject b on a.subject_id=b.id");
        StringBuilder result = new StringBuilder();
        result.append(getReportHeader());
        result.append("<h1>"+REPORT_TYTLE+"</h1>");
        result.append("<table>");
        Values values;
        // заголовки колонок
        result.append("<tr>");
        result.append("<th>");
        result.append("&nbsp;");
        result.append("</th>");
        for (int i = 0; i < depart.size(); i++) {
            values = depart.getValues(i);
            result.append("<th colspan='2'>");
            result.append(values.getString("label"));
            result.append("</th>");
        }
        result.append("</tr>");
        // строки
        Values dayValues;
        Values bellValues;
        for (int col = 0; col < day_list.size(); col++) {
            dayValues = day_list.getValues(col);
            // заголовок  строка день
            result.append("<tr>");
            result.append("<td colspan='" + (Integer) (2 * depart.size() + 1) + "'>");
            result.append(dayValues.getString("day_caption"));
            result.append("</td>");
            result.append("</tr>");
            for (int row = 0; row < bell_list.size(); row++) {
                result.append("<tr>");
                bellValues = bell_list.getValues(row);
                result.append("<th nowrap>");
                result.append(bellValues.getString("time_start"));
                result.append("</th>");
                for (int i = 0; i < depart.size(); i++) {
                    values = depart.getValues(i);
                    result.append(getScheduleCell2(values.getInteger("id"), dayValues.getInteger("day_no"), bellValues.getInteger("bell_id")));
                }
                result.append("</tr>");
            }
            // пустая строка
            result.append("<tr>");
            result.append("<td colspan='" + (Integer) (2 * depart.size() + 1) + "'>");
            result.append("</td>");
            result.append("</tr>");
        }
        // заголовки строк
        result.append("</table>");
        html = result.toString();
    }
    
}
