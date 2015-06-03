/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.reports;

import ru.viljinsky.sqlite.DataModule;
import ru.viljinsky.sqlite.Dataset;

/**
 *
 * @author вадик
 */
class ErrorsReport extends AbstractReport {
    private static final String ERROR_NOT_DETECTED = "<p>Ошибок не обнаружено</p>";
    private static final String SQL_ERROR_DEPART_TIME_CONFLICT = 
            "select a.depart_label,a.group_label,a.day_caption,a.lesson_time,a.subject_name,a.teacher,a.room from v_schedule a where not exists(\n" + 
            "	select * from shift_detail b inner join depart d on d.shift_id=b.shift_id\n" + 
            "        where b.bell_id=a.bell_id and b.day_id=a.day_id and a.depart_id=d.id\n" + 
            ")\n" + 
            "order by a.depart_id,a.day_id,a.bell_id;";
    private static final String SQL_ERROR_TEACHER_TIME_CONFLICT = 
            "select a.day_caption,a.lesson_time,a.subject_name,a.depart_label,a.group_label,a.teacher,a.room\n" + 
            " from v_schedule a where not exists(\n" + 
            "select * from shift_detail b inner join teacher c on b.shift_id=c.shift_id and c.id=a.teacher_id\n" +
            "where b.day_id=a.day_id and b.bell_id=a.bell_id\n" + 
            ");";
    private static final String SQL_ERROR_ROOM_TIME_CONFLICT =
            "select a.day_caption,a.lesson_time,a.depart_label,a.group_label,a.subject_name,a.teacher,a.room\n" + 
            " from v_schedule a where not exists(\n" +
            "select * from shift_detail b inner join room c on b.shift_id=c.shift_id and c.id=a.room_id\n" +
            "where b.day_id=a.day_id and b.bell_id=a.bell_id\n" + 
            ");";
    private static final String SQL_ERROR_UNPLACED_GROUP = 
            "select a.subject_name,c.label,a.group_label,t.last_name as teacher,r.room_name as room, b.placed,b.hour_per_week\n" + 
            " from v_subject_group a\n" + "inner join v_subject_group_on_schedule b\n" + 
            "on a.depart_id=b.depart_id and a.subject_id=b.subject_id and a.group_id=b.group_id\n" +
            "inner join depart c on c.id=a.depart_id\n" + 
            "left join teacher t on t.id=a.default_teacher_id\n" +
            "left join room r on r.id=a.default_room_id\n" + "where b.placed<b.hour_per_week;";
    private static final String SQL_ERROR_EMPTY_ROOM_OR_TEACHER = 
            "select day_caption,lesson_time,subject_name,depart_label,group_label,teacher,room \n" + 
            "from v_schedule where teacher_id is null or room_id is null;";

    /**  HTML талица НЕ ВСЕ ЧАСЫ РАССТАВЛЕНЫ  */
    private String getUnplacedGroupTable() throws Exception {
        StringBuilder result = new StringBuilder();
        Dataset dataset = DataModule.getSQLDataset(SQL_ERROR_UNPLACED_GROUP);
        dataset.open();
        result.append("<b>Не размещённые часы</b>");
        if (dataset.isEmpty()) {
            result.append("<p>Ошибок не обнаружено</p>");
            return result.toString();
        }
        result.append(datasetToHtml(dataset));
        return result.toString();
    }

    /**
     * HTML талица ошибок НЕ УКАЗАН ПРЕПОДАВАТЕЛЬ ИЛИ ПОМЕЩЕНИЕ
     * @return
     * @throws Exception
     */
    private String getEmptyTeacherOrRoomTable() throws Exception {
        StringBuilder result = new StringBuilder();
        Dataset errors = DataModule.getSQLDataset(SQL_ERROR_EMPTY_ROOM_OR_TEACHER);
        errors.open();
        result.append("<b>Не указа преподаватель или помещение</b>");
        if (errors.isEmpty()) {
            result.append("<p>Ошибок не обнаружено</p>");
            return result.toString();
        }
        result.append(datasetToHtml(errors));
        return result.toString();
    }

    /**
     * HTML таблица Нарушение графиков Занятия проходя не по графику
     * @return
     */
    private String getTimeConflict() throws Exception {
        StringBuilder result = new StringBuilder();
        result.append("<b>Нарушение графиков классов</b>");
        Dataset dataset = DataModule.getSQLDataset(SQL_ERROR_DEPART_TIME_CONFLICT);
        dataset.open();
        if (dataset.isEmpty()) {
            result.append(ERROR_NOT_DETECTED);
        } else {
            result.append(datasetToHtml(dataset));
        }
        dataset = DataModule.getSQLDataset(SQL_ERROR_TEACHER_TIME_CONFLICT);
        dataset.open();
        result.append("<b>Нарушение графиков преподавателей</b>");
        if (dataset.isEmpty()) {
            result.append(ERROR_NOT_DETECTED);
        } else {
            result.append(datasetToHtml(dataset));
        }
        dataset = DataModule.getSQLDataset(SQL_ERROR_ROOM_TIME_CONFLICT);
        dataset.open();
        result.append("<b>Нарушение графиков помещений</b>");
        if (dataset.isEmpty()) {
            result.append(ERROR_NOT_DETECTED);
        } else {
            result.append(datasetToHtml(dataset));
        }
        dataset = DataModule.getSQLDataset(SQL_ERROR_HALL_IN_SCHEDULE);
        dataset.open();
        result.append("<b>Окна в расписании</b>");
        if (dataset.isEmpty()) {
            result.append(ERROR_NOT_DETECTED);
        } else {
            result.append(datasetToHtml(dataset));
        }
        return result.toString();
    }

    @Override
    public void prepare() throws Exception {
        StringBuilder result = new StringBuilder();
        result.append("<h1>Отчёты по ошибка в расписании </h1>");
        result.append(getEmptyTeacherOrRoomTable());
        result.append(getUnplacedGroupTable());
        result.append(getTimeConflict());
        html = result.toString();
    }
    
}
