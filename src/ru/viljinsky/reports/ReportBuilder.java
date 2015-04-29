/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.reports;

import ru.viljinsky.Column;
import ru.viljinsky.DataModule;
import ru.viljinsky.Dataset;
import ru.viljinsky.Values;

/**
 *
 * @author вадик
 */


public class ReportBuilder {
    
    public static final String ERROR_NOT_DETECTED = "<p>Ошибок не обнаружено</p>";
    
    public static final String SQL_ERROR_HALL_IN_SCHEDULE =
            "select d.day_caption,l.time_start || ' ' || l.time_end as lesson_tyme,\n"+
            "                           b.label,a.day_id,a.bell_id,b.id as depart_id \n" +
            "from shift_detail a inner join depart b	on a.shift_id=b.shift_id\n" +
            "	inner join day_list d on a.day_id=d.day_no\n" +
            "	inner join bell_list l on l.bell_id=a.bell_id\n" +
            "where a.bell_id<(select max(bell_id) from schedule where day_id=a.day_id and depart_id=b.id)\n" +
            "   and not exists (select * \n" +
            "	from schedule where day_id=a.day_id and bell_id=a.bell_id and depart_id=b.id);";
    
    public static final String SQL_ERROR_DEPART_TIME_CONFLICT =
            "select a.depart_label,a.group_label,a.day_caption,a.lesson_time,a.subject_name,a.teacher,a.room from v_schedule a where not exists(\n"+
            "	select * from shift_detail b inner join depart d on d.shift_id=b.shift_id\n" +
            "        where b.bell_id=a.bell_id and b.day_id=a.day_id and a.depart_id=d.id\n" +
            ")\n" +
            "order by a.depart_id,a.day_id,a.bell_id;";
    
    public static final String SQL_ERROR_TEACHER_TIME_CONFLICT =
            "select a.day_caption,a.lesson_time,a.subject_name,a.depart_label,a.group_label,a.teacher,a.room\n" +
            " from v_schedule a where not exists(\n" +
            "select * from shift_detail b inner join teacher c on b.shift_id=c.shift_id and c.id=a.teacher_id\n" +
            "where b.day_id=a.day_id and b.bell_id=a.bell_id\n" +
            ");";
            
    public static final String SQL_ERROR_ROOM_TIME_CONFLICT =
            "select a.day_caption,a.lesson_time,a.depart_label,a.group_label,a.subject_name,a.teacher,a.room\n" +
            " from v_schedule a where not exists(\n" +
            "select * from shift_detail b inner join room c on b.shift_id=c.shift_id and c.id=a.room_id\n" +
            "where b.day_id=a.day_id and b.bell_id=a.bell_id\n" +
            ");";
    
   public static final String SQL_ERROR_EMPTY_ROOM_OR_TEACHER = 
            "select day_caption,lesson_time,subject_name,depart_label,group_label,teacher,room \n"
            + "from v_schedule where teacher_id is null or room_id is null;";        

    public static final String SQL_ERROR_UNPLACED_GROUP = 
            "select a.subject_name,c.label,a.group_label,t.last_name as teacher,r.room_name as room, b.placed,b.hour_per_week\n"+
            " from v_subject_group a\n" +
            "inner join v_subject_group_on_schedule b\n" +
            "on a.depart_id=b.depart_id and a.subject_id=b.subject_id and a.group_id=b.group_id\n" +
            "inner join depart c on c.id=a.depart_id\n" +
            "left join teacher t on t.id=a.default_teacher_id\n" +
            "left join room r on r.id=a.default_room_id\n" +
            "where b.placed<b.hour_per_week;";

    

    public static final String HTML_PATTERN = 
        "<!DOCTYPE html>"+
        "<html lang='ru'>"+
        "<head>"+
        "[style]"+
        "</head>"+
        "<body>"+
       
        "[body]"+   
        
        "</body>"+
        "</html>";
    
   public static final String STYLE = 
         "<style> table{width:90%;border:solid 1px silver;border-collapse:collapse;}"+
         "td, th{border: solid 1px silver;}\n" +
         "th{background:#eeeeee;}"+
         "</style>";

   /**
    * Преобразование dataseta в простую HTML таблицу
    * @param dataset
    * @return
    * @throws Exception 
    */
   private String datasetToHtml(Dataset dataset) throws Exception{
        StringBuilder result = new StringBuilder();
        Values values;
        if (!dataset.isActive()){
            throw new Exception("DATASET_NOT_ACTIVE");
        }
        
        result.append("<table>");
        result.append("<tr>");
        
        for (Column column:dataset.getColumns()){
            result.append("<th>");
            result.append(column.getColumnName());
            result.append("</th>");
        }
        
        result.append("</tr>");
        
        for (int i=0;i<dataset.size();i++){
            result.append("<tr>");
            values=dataset.getValues(i);
            for (Column column:dataset.getColumns()){
                result.append("<td>");
                result.append(values.getString(column.getColumnName()));
                result.append("</td>");
            }
            
            result.append("</tr>");
        }
        result.append("</table>");
        return result.toString();
    }

   
    Dataset day_list;
    Dataset bell_list;
    Dataset schedule;
    Dataset depart;
    Dataset hall_list;
    
    public String getScheduleCell(int bell_id,int day_id) throws Exception{
        Values values = new Values();
        values.put("day_id",day_id);
        values.put("bell_id",bell_id);
        if (hall_list.locate(values)>=0){
            return "<div align='center'>****</div>";
        }
                
        
        for (int i=0;i<schedule.size();i++){
            values = schedule.getValues(i);
            if (values.getInteger("day_id")==day_id && values.getInteger("bell_id")==bell_id){
                return values.getString("subject_name");
            }
        }
        
        return "&nbsp;";
    }

    /**
     * Расписание занятий по классам
     * каждый класс расположен в одельной таблице
     * @return
     * @throws Exception 
     */
    public String getScheduleGrid() throws Exception{
        day_list = DataModule.getDataset("day_list");
        day_list.open();
        bell_list = DataModule.getDataset("bell_list");
        bell_list.open();
        Integer day_id,bell_id;
        StringBuilder result = new StringBuilder();

        result.append("<table>");
        Values values;
        result.append("<tr>");
        
        result.append("<td>");
        result.append("&nbsp;");
        result.append("</td>");
     
        // заголовок колонок
        for (int i=0;i<day_list.size();i++){
            values = day_list.getValues(i);
            result.append("<th colspan='2'>");
            result.append(values.getString("day_caption"));
            result.append("</th>");
        }
        result.append("</tr>");
        
        for (int row=0;row<bell_list.size();row++){
            bell_id=bell_list.getValues(row).getInteger("bell_id");
            result.append("<tr>");
            // заголовок строк
            values = bell_list.getValues(row);
            result.append("<th nowrap>");
            result.append(values.getString("time_start"));
            result.append("</th>");
            
            // значения
            for (int col=0;col<day_list.size();col++){
                day_id=day_list.getValues(col).getInteger("day_no");
                result.append("<td>");
                result.append(getScheduleCell(bell_id, day_id));
                result.append("</td>");
                result.append("<td>");
                result.append("&nbsp;");
                
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
        hall_list =DataModule.getSQLDataset(SQL_ERROR_HALL_IN_SCHEDULE);
        hall_list.open();
        
        schedule = DataModule.getSQLDataset("select a.day_id,a.bell_id,b.subject_name,a.depart_id from schedule a inner join subject b on a.subject_id=b.id");
        StringBuilder resultText = new StringBuilder();
        resultText.append("<h1>Расписание занятий по классам</h1>");
        
        Values values,filter;
        
        filter = new Values();
        depart = DataModule.getDataset("depart");
        depart.open();
                
        for (int i=0;i<depart.size();i++){
            values = depart.getValues(i);
            filter.put("depart_id", values.getInteger("id"));
            schedule.open(filter);
            hall_list.open(filter);

            resultText.append("<h3>");
            resultText.append(values.getString("label"));
            resultText.append("</h3>");
            
            
            resultText.append(getScheduleGrid());
            resultText.append("<br>");
        
        }
        
        return  HTML_PATTERN.replace("[body]", resultText).replace("[style]", STYLE) ;//resultText.toString();
    }
    
    public String getScheduleCell2(int day_no,int bell_id,int depart_id) throws Exception{
        Values filter = new Values();
        filter.put("day_id", day_no);
        filter.put("bell_id", bell_id);
        filter.put("depart_id", depart_id);
        if (hall_list.locate(filter)>=0){
            return "<div align='center'>****</div>";
        }
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
        hall_list = DataModule.getSQLDataset(SQL_ERROR_HALL_IN_SCHEDULE);
        hall_list.open();
        day_list = DataModule.getDataset("day_list");
        day_list.open();
        bell_list=DataModule.getDataset("bell_list");
        bell_list.open();
        depart = DataModule.getDataset("depart");
        depart.open();
        schedule = DataModule.getSQLDataset("select day_id,bell_id,depart_id,subject_name from schedule a inner join subject b on a.subject_id=b.id");
        
        StringBuilder result = new StringBuilder();
        result.append("<h1>Расписание занятий по классам(2)</h1>");
        
        result.append("<table>");
        Values values;
        // заголовки колонок
        
            result.append("<tr>");
            result.append("<th>");            
            result.append("&nbsp;");
            result.append("</th>");
            for (int i=0;i<depart.size();i++){
                values = depart.getValues(i);
                result.append("<th colspan='2'>");
                result.append(values.getString("label"));
                result.append("</th>");
            }
            result.append("</tr>");
        
        // строки
        
            Values dayValues,bellValues;
            for (int day_no=0;day_no<day_list.size();day_no++){
                dayValues=day_list.getValues(day_no);
                
                // заголовок  строка день
                result.append("<tr>");
                result.append("<td colspan='"+(Integer)(2*depart.size()+1)+"'>");
                result.append(dayValues.getString("day_caption"));
                        
                result.append("</td>");                    
                result.append("</tr>");
                
                
                for (int bell_id=0;bell_id<bell_list.size();bell_id++){
                    result.append("<tr>");
                    bellValues = bell_list.getValues(bell_id);
                    result.append("<th>");
                    result.append(bellValues.getString("time_start"));
                    result.append("</th>");
                    
                    for (int i=0;i<depart.size();i++){
                        values=depart.getValues(i);
                        result.append("<td>");
                        result.append(getScheduleCell2(day_no+1, bell_id+1, values.getInteger("id")));
                        result.append("</td>");
                        result.append("<td>");
                        result.append("&nbsp;");
                        result.append("</td>");
                        
                    }
                    result.append("</tr>");
                    
                }
                // пустая строка
                result.append("<tr>");
                result.append("<td colspan='"+(Integer)(2*depart.size()+1)+"'>");
                result.append("</td>");                    
                result.append("</tr>");
            }
        
        // заголовки строк
        
        result.append("</table>");
        
        
        return HTML_PATTERN.replace("[body]", result).replace("[style]", STYLE);
    }
 
    
    
    
    /**  HTML талица НЕ ВСЕ ЧАСЫ РАССТАВЛЕНЫ  */ 
    public String getUnplacedGroupTable() throws Exception{
        StringBuilder result = new StringBuilder();
        Dataset dataset = DataModule.getSQLDataset(SQL_ERROR_UNPLACED_GROUP);
        dataset.open();
        result.append("<b>Не размещённые часы</b>");
        if (dataset.isEmpty()){
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
    public String getEmptyTeacherOrRoomTable() throws Exception{

        StringBuilder result = new StringBuilder();
        Dataset errors =  DataModule.getSQLDataset(SQL_ERROR_EMPTY_ROOM_OR_TEACHER);
        errors.open();
        result.append("<b>Не указа преподаватель или помещение</b>");
        if (errors.isEmpty()){
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
    public String getTimeConflict() throws Exception{
        
        StringBuilder result = new StringBuilder();
        result.append("<b>Нарушение графиков классов</b>");
        Dataset dataset = DataModule.getSQLDataset(SQL_ERROR_DEPART_TIME_CONFLICT);
       
        dataset.open();
        if (dataset.isEmpty())
            result.append(ERROR_NOT_DETECTED);
        else
            result.append(datasetToHtml(dataset));
        
        dataset= DataModule.getSQLDataset(SQL_ERROR_TEACHER_TIME_CONFLICT);
        dataset.open();
        result.append("<b>Нарушение графиков преподавателей</b>");        
        if (dataset.isEmpty())
            result.append(ERROR_NOT_DETECTED);
        else
            result.append(datasetToHtml(dataset));

        dataset= DataModule.getSQLDataset(SQL_ERROR_ROOM_TIME_CONFLICT);
        dataset.open();
        result.append("<b>Нарушение графиков помещений</b>");
        if (dataset.isEmpty())
            result.append(ERROR_NOT_DETECTED);
        else
            result.append(datasetToHtml(dataset));

        
        dataset = DataModule.getSQLDataset(SQL_ERROR_HALL_IN_SCHEDULE);
        dataset.open();
        result.append("<b>Окна в расписании</b>");
        if (dataset.isEmpty())
            result.append(ERROR_NOT_DETECTED);
        else
            result.append(datasetToHtml(dataset));
        
        return result.toString();
    }
    
    
    /**
     * Отчёт о всех ошибках в расписании
     * @return
     * @throws Exception 
     */
    public String getScheduleError() throws Exception{
        StringBuilder result = new StringBuilder();

        result.append("<h1>Отчёты по ошибка в расписании </h1>");
        result.append(getEmptyTeacherOrRoomTable());
        result.append(getUnplacedGroupTable());
        result.append(getTimeConflict());
        
        return HTML_PATTERN.replace("[body]", result).replace("[style]", STYLE);
    }
    
    protected String getTeacherTable(Integer teacher_id) throws Exception{
        StringBuilder result = new StringBuilder();
        result.append("<table border='1' width='90%' align='center'>");
        // заголовок
       
        String day_caption;
        Integer day_id;
        String bell_caption;
        Integer bell_id;
        
        
        Values map = new Values();
        
        for (int row=0;row<day_list.size();row++){
            day_caption = day_list.getValues(row).getString("day_caption");
            day_id= day_list.getValues(row).getInteger("day_no");
            
            result.append("<tr>");
            result.append("<td colspan='2'>");
            result.append(day_caption);
            result.append("</td>");
            result.append("</tr>");
            
            for (int col=0;col<bell_list.size();col++){
                bell_caption=bell_list.getValues(col).getString("time_start");
                bell_id = bell_list.getValues(col).getInteger("bell_id");

                result.append("<tr>");
                result.append("<td>");
                
                result.append(bell_caption);
                
                result.append("</td>");
                result.append("<td>");

                Integer i;
                map.put("teacher_id",teacher_id);
                map.put("day_id", day_id);
                map.put("bell_id",bell_id);
                i = schedule.locate(map);
                if (i>=0){
                    Values v = null;
                    v= schedule.getValues(i);
                    result.append(v.getString("subject_name")+" "+
                            v.getString("depart_label")+" "+
                            v.getString("group_label")+" "+
                            v.getString("room")+" "+
                            v.getString("building")+" "+
                            "<br>");
                } else {
                    result.append("&nbsp;");
                }
                
                result.append("</td>");               
                result.append("</tr>");
                
            }
        }
        
        
        
        
        result.append("<tr>");
        result.append("<td>");
        result.append("&nbsp");
        result.append("</dt>");
        result.append("</tr>");        
        result.append("</table>");
        return result.toString();
    }
    
    public String getTeacherSchedule() throws Exception{
        StringBuilder result = new StringBuilder();
        result.append("<h1>Расписание занятий по преподавателям</h1>");
        
        schedule = DataModule.getDataset("v_schedule");
        schedule.open();
        day_list = DataModule.getDataset("day_list");
        day_list.open();
        bell_list = DataModule.getDataset("bell_list");
        bell_list.open();
        Dataset teacher = DataModule.getDataset("v_teacher");
        teacher.open();
        Values values ;
        for (int i=0;i<teacher.size();i++){
            values = teacher.getValues(i);
            result.append("<p>"+values.getString("last_name")+"</p>");
            result.append(getTeacherTable(values.getInteger("id")));
        }
        
        
        
        return result.toString();
    }
}
