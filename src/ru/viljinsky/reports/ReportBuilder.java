/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.reports;

import ru.viljinsky.sqlite.Column;
import ru.viljinsky.sqlite.DataModule;
import ru.viljinsky.sqlite.Dataset;
import ru.viljinsky.sqlite.Recordset;
import ru.viljinsky.sqlite.Values;

/**
 *
 * @author вадик
 */

public class ReportBuilder implements IReportBuilder{
    
    
    public String getReport(String reportName) throws Exception{
        switch(reportName){
            case RP_CURRICULUM:
                return getCurriculumReport();
            case RP_SCHEDULE_VAR_1:
                return getScheduleReport();
            case RP_SCHEDULE_VAR_2:
                return getSchedueReport2();
            case RP_SCHEDULE_TEACHER:
                return getTeacherSchedule();
            case RP_SCHEDULE_ERRORS:
                return getScheduleError();
            case RP_HOME:
                return getIndexPage();
            default:
                throw new Exception("UNKNOW_REPORT_NAME\n"+reportName);
        }
    }
    
    public static String[] getReportList(){
        return  new String[]{
            RP_CURRICULUM,
            RP_SCHEDULE_VAR_1,
            RP_SCHEDULE_VAR_2,
            RP_SCHEDULE_TEACHER,
            RP_SCHEDULE_ERRORS
        };
    }
    
    private static final String ERROR_NOT_DETECTED = "<p>Ошибок не обнаружено</p>";
    
    private static final String SQL_ERROR_HALL_IN_SCHEDULE =
            "select d.day_caption,l.time_start || ' ' || l.time_end as lesson_tyme,\n"+
            "                           b.label,a.day_id,a.bell_id,b.id as depart_id \n" +
            "from shift_detail a inner join depart b	on a.shift_id=b.shift_id\n" +
            "	inner join day_list d on a.day_id=d.day_no\n" +
            "	inner join bell_list l on l.bell_id=a.bell_id\n" +
            "where a.bell_id<(select max(bell_id) from schedule where day_id=a.day_id and depart_id=b.id)\n" +
            "   and not exists (select * \n" +
            "	from schedule where day_id=a.day_id and bell_id=a.bell_id and depart_id=b.id);";
    
    private static final String SQL_ERROR_DEPART_TIME_CONFLICT =
            "select a.depart_label,a.group_label,a.day_caption,a.lesson_time,a.subject_name,a.teacher,a.room from v_schedule a where not exists(\n"+
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
    
   private static final String SQL_ERROR_EMPTY_ROOM_OR_TEACHER = 
            "select day_caption,lesson_time,subject_name,depart_label,group_label,teacher,room \n"
            + "from v_schedule where teacher_id is null or room_id is null;";        

    private static final String SQL_ERROR_UNPLACED_GROUP = 
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
        
        "[navigator]"+    
       
        "[body]"+   
        
        "<div font='small' align='center'><a href='http://www.timetabler.narod.ru'>Составитель расписания</a> &copy; 2015</div>"+    
        "</body>"+
        "</html>";
    
   public static final String STYLE = 
         "<style> table{width:90%;border:solid 1px silver;border-collapse:collapse;}"+
         "td, th{border: solid 1px silver;}\n" +
         "th{color:#707070; background:#eeeeee;}"+
           
//        ".navigator li{display:inline;}"+
//        ".navigator li A{font-weight:bold;color:#00f;text-decoration:none;}"+
//        ".navigator li A:hover{text-decoration:underline;}"+           
           
         "</style>";

   /**
    * Заголовок отчётов 
    * Название учебного заведения
    * Учебный период
    * Период расписания
    * @return 
    */
   private String getReportHeader(){
       String result = "Report header";
               
       try{
           result = ScheduleParams.getStringParamByName(ScheduleParams.SCHEDULE_SPAN)+"<br>"+
            ScheduleParams.getStringParamByName(ScheduleParams.SCHEDULE_TITLE)+"<br>"+
            ScheduleParams.getStringParamByName(ScheduleParams.EDUCATIONAL_INSTITUTION)+"<br>"+

            ScheduleParams.getStringParamByName(ScheduleParams.DATE_BEGIN)+"&nbsp;"+
            ScheduleParams.getStringParamByName(ScheduleParams.DATE_END)+"<br>";
       } catch (Exception e){
           e.printStackTrace();
       }
       
       return result;
   }
   
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
            result.append("<th>")
            .append(column.getColumnName())
            .append("</th>");
        }
        
        result.append("</tr>");
        
        for (int i=0;i<dataset.size();i++){
            result.append("<tr>");
            values=dataset.getValues(i);
            for (Column column:dataset.getColumns()){
                result.append("<td>")
                    .append(values.getString(column.getColumnName()))
                    .append("</td>");
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
    
    private String getRoomLabel(int depart_id,int day_id,int bell_id) throws Exception{
        Recordset r = DataModule.getRecordet(
                "select distinct room_name from room a inner join schedule b on a.id=b.room_id\n"
              + "where day_id="+day_id+" and bell_id="+bell_id+" and depart_id="+depart_id);
        String room_label="";
        if (r.isEmpty())
            room_label="?";
        else
            for (int i=0;i<r.size();i++){
                if (!room_label.isEmpty())
                    room_label+="<br>";
                room_label+=r.get(i)[0].toString();
            }
        return room_label;
    }
        
    
    private String getScheduleCell(int depart_id,int bell_id,int day_id) throws Exception{

        String room_label = getRoomLabel(depart_id, day_id, bell_id);
            
        Values values = new Values();
        values.put("day_id",day_id);
        values.put("bell_id",bell_id);
        if (hall_list.locate(values)>=0)
            return "<td colspan='2' align='center'>****</td>";
        
        for (int i=0;i<schedule.size();i++){
            values = schedule.getValues(i);
            if (values.getInteger("day_id")==day_id && values.getInteger("bell_id")==bell_id){
                return "<td nowrap>"+values.getString("subject_name")+"</td><td nowrap>"+room_label+"</td>";
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
    public String getScheduleReport(Integer depart_id) throws Exception{
        Integer day_id,bell_id;
        Values values,filter;
        String sql;

        sql = "select distinct a.* from day_list a inner join shift_detail b\n" +
                     "on a.day_no=b.day_id inner join depart d on d.shift_id=b.shift_id where d.id="+depart_id;
        day_list= DataModule.getSQLDataset(sql);
        day_list.open();
        
        sql = "select distinct a.* from bell_list a inner join shift_detail b on a.bell_id=b.bell_id \n"+
                "inner join depart d on d.shift_id=b.shift_id where d.id="+depart_id;
        
        bell_list = DataModule.getSQLDataset(sql);
        bell_list.open();
        
        
        StringBuilder result = new StringBuilder();
        
//        result.append(getReportHeader());
        
        Recordset r = DataModule.getRecordet("select label from depart where id="+depart_id);
        
        filter = new Values();
        filter.put("depart_id", depart_id);
        schedule.open(filter);
        hall_list.open(filter);

        result.append("<h3>")
            .append(r.getString(0))
            .append("</h3>");
        
        result.append("<table>")
            .append("<tr>")
            .append("<td>")
            .append("&nbsp;")
            .append("</td>");
     
        // заголовок колонок
        for (int i=0;i<day_list.size();i++){
            values = day_list.getValues(i);
            result.append("<th colspan='2'>")
                .append(values.getString("day_caption"))
                .append("</th>");
        }
        result.append("</tr>");
        
        for (int row=0;row<bell_list.size();row++){
            bell_id=bell_list.getValues(row).getInteger("bell_id");
            result.append("<tr>");
            // заголовок строк
            values = bell_list.getValues(row);
            result.append("<th nowrap>")
                .append(values.getString("time_start"))
                .append("</th>");
            
            // значения
            for (int col=0;col<day_list.size();col++){
                day_id=day_list.getValues(col).getInteger("day_no");
                result.append(getScheduleCell(depart_id,bell_id, day_id));
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
        StringBuilder result = new StringBuilder();
        result.append("<h1>Расписание занятий по классам</h1>");
        result.append(getReportHeader());
        
        Values values;
        
        depart = DataModule.getDataset("depart");
        depart.open();
                
        for (int i=0;i<depart.size();i++){
            values = depart.getValues(i);
            
            result.append(getScheduleReport(values.getInteger("id")));
            result.append("<br>");
        }
        
        return  result.toString() ;
    }
    
    
    /**
     * Возвращает ячейки subject_name room_labale
     * @param depart_id
     * @param day_id
     * @param bell_id
     * @return
     * @throws Exception 
     */
    private String getScheduleCell2(int depart_id,int day_id,int bell_id) throws Exception{
        Values filter = new Values();
        filter.put("day_id", day_id);
        filter.put("bell_id", bell_id);
        filter.put("depart_id", depart_id);
        if (hall_list.locate(filter)>=0){
            return "<td colspan='2' align='center'>****</td>";
        }
        schedule.open(filter);
        if (schedule.isEmpty())
            return "<td colspan='2'>&nbsp;</td>";
        
        String room_label = getRoomLabel(depart_id, day_id, bell_id);
        
        Values values = schedule.getValues(0);
        return "<td>"+values.getString("subject_name")+"</td><td nowrap>"+room_label+"</td>";
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
        String sql = "select distinct a.* from day_list a inner join shift_detail b on a.day_no=b.day_id inner join depart d on d.shift_id=b.shift_id";
        day_list = DataModule.getSQLDataset(sql);
        day_list.open();
        bell_list=DataModule.getDataset("bell_list");
        bell_list.open();
        depart = DataModule.getDataset("depart");
        depart.open();
        schedule = DataModule.getSQLDataset("select day_id,bell_id,depart_id,subject_name from schedule a inner join subject b on a.subject_id=b.id");
        
        StringBuilder result = new StringBuilder();
        result.append(getReportHeader());
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
            for (int col=0;col<day_list.size();col++){
                dayValues=day_list.getValues(col);
                
                // заголовок  строка день
                result.append("<tr>");
                result.append("<td colspan='"+(Integer)(2*depart.size()+1)+"'>");
                result.append(dayValues.getString("day_caption"));
                        
                result.append("</td>");                    
                result.append("</tr>");
                
                
                for (int row=0;row<bell_list.size();row++){
                    result.append("<tr>");
                    bellValues = bell_list.getValues(row);
                    result.append("<th nowrap>");
                    
                    result.append(bellValues.getString("time_start"));
                    result.append("</th>");
                    
                    for (int i=0;i<depart.size();i++){
                        values=depart.getValues(i);
                        result.append(getScheduleCell2(values.getInteger("id"),dayValues.getInteger("day_no"), bellValues.getInteger("bell_id")));
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
        
        
        return result.toString();
    }
 
    
    
    
    /**  HTML талица НЕ ВСЕ ЧАСЫ РАССТАВЛЕНЫ  */ 
    private String getUnplacedGroupTable() throws Exception{
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
    private String getEmptyTeacherOrRoomTable() throws Exception{

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
    private String getTimeConflict() throws Exception{
        
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
        
        return result.toString();
    }

    /**
     * Таблица расписания преподавателя
     * @param teacher_id
     * @return
     * @throws Exception 
     */
    public String getTeacherTable(Integer teacher_id) throws Exception{
        int min_bell_id,
            max_bell_id,
            recNo;
        
        StringBuilder result = new StringBuilder();

        Recordset r;
        
        r = DataModule.getRecordet("select teacher_fio,profile_name from v_teacher where id="+teacher_id+";");
        
        result.append("<h2>").append(r.getString(0)).append("</h2>");
        result.append("<div>").append(r.getString(1)).append("</div>");
        
        
        
        result.append("<table border='1' width='90%' align='center'>");
        
        result.append("<tr><th>Время</th><th>Предмет</th><th>Класс</th><th>Група</th><th>Кабинет</th><th>Здание</th></tr>");
        // заголовок
       
        day_list = DataModule.getSQLDataset("select * from day_list where exists (select * from schedule where day_id=day_list.day_no and teacher_id="+teacher_id+");");
        day_list.open();
                
        
        String day_caption;
        Integer day_id;
        String bell_caption;
        Integer bell_id;
        
        
        Values map = new Values();
        
        for (int row=0;row<day_list.size();row++){
            
            day_caption = day_list.getValues(row).getString("day_caption");
            day_id= day_list.getValues(row).getInteger("day_no");
            r = DataModule.getRecordet("select min(bell_id),max(bell_id) from schedule where day_id="+day_id+" and teacher_id="+teacher_id+";");
            min_bell_id = r.getInteger(0);
            max_bell_id = r.getInteger(1);
            bell_list = DataModule.getSQLDataset("select * from bell_list where bell_id between "+min_bell_id+" and "+max_bell_id+";");
            bell_list.open();
            
            result.append("<tr>")
                  .append("<td colspan='6'>")
                  .append(day_caption)
                  .append("</td>")
                  .append("</tr>");
            
            for (int col=0;col<bell_list.size();col++){
                bell_caption=bell_list.getValues(col).getString("time_start");
                bell_id = bell_list.getValues(col).getInteger("bell_id");

                result.append("<tr>")
                    .append("<td>")                
                    .append(bell_caption)                
                    .append("</td>");

                map.put("teacher_id",teacher_id);
                map.put("day_id", day_id);
                map.put("bell_id",bell_id);
                recNo = schedule.locate(map);
                if (recNo>=0){
                    Values v = null;
                    v= schedule.getValues(recNo);
                    result.append("<td>")
                        .append(v.getString("subject_name"))
                        .append("</td><td>")
                        .append(v.getString("depart_label"))
                        .append("</td><td>")
                        .append(v.getString("group_label"))
                        .append("</td><td>")        
                        .append(v.getString("room"))
                        .append("</td><td>")
                        .append(v.getString("building"))
                        .append("</td>");
                } else {
                    result.append("<td colspan='5' align='center'>****</td>");
                }
                result.append("</tr>");
            }
        }
        
        result.append("<tr>")
            .append("<td colspan='6'>")
            .append("&nbsp")
            .append("</dt>")
            .append("</tr>")        
            .append("</table>");
        
        return result.toString();
    }
    
    /**
     * Ощий отчёт по преподавателям
     * @return
     * @throws Exception 
     */
    public String getTeacherSchedule() throws Exception{
        StringBuilder result = new StringBuilder();
        result.append("<h1>Расписание занятий по преподавателям</h1>");
        result.append(getReportHeader());
        
        schedule = DataModule.getDataset("v_schedule");
        schedule.open();
        
        Dataset teacher = DataModule.getDataset("v_teacher");
        teacher.open();
        Values values ;
        for (int i=0;i<teacher.size();i++){
            values = teacher.getValues(i);
            result.append(getTeacherTable(values.getInteger("id")));
        }
        
        return result.toString();
    }
    
    public static String createPage(String reportContent){
        return HTML_PATTERN.replace("[body]", reportContent).replace("[style]", STYLE).replace("[navigator]", HTML_NAVIGATOR);
    }
    
    
    public static String HTML_NAVIGATOR =
                "<ul class='navigator'>"

                + "<li>"
                + "<a href='.'>Начальная страница</a>"
                + "</li>"
            
                + "<li>"
                + "<a href='page5.html'>Учебный план</a>"
                + "</li>"
            
                + "<li>"
                + "<a href='page1.html'>Расписание (вариант1)</a>"
                + "</li>"
                
                + "<li>"
                + "<a href='page2.html'>Расписание (вариант2)</a>"
                + "</li>"
                
                + "<li>"                
                + "<a href='page3.html'>Расписание преподователей</a>"
                + "</li>"
                
                + "<li>"
                + "<a href='page4.html'>Расписание ошибки</a>"
                + "</li>"
                
                + "</ul>";

    
    
    public String getIndexPage(){
        return "<h1>Пример расписания</h1>";
    }
    
   ////////////////////Curriculum report //////////////////////////////////////
    
    
    Dataset curriculumDetails;

    private String getCurriculumDetails(Values vCurriculum) throws Exception{
        StringBuilder result = new StringBuilder();
        Values v,v2,v3,filter;
        filter = new Values();
        filter.put("curriculum_id",vCurriculum.getInteger("id"));
        
        Dataset skillList = DataModule.getSQLDataset("select * from skill");
        skillList.open();
        Dataset subjectList = DataModule.getSQLDataset("select * from subject");
        subjectList.open();;
        
        result.append("<table>");
        result.append("<tr>");
        result.append("<td>&nbsp;</td>");
        for (int i=0;i<skillList.size();i++){
            v=skillList.getValues(i);
            result.append("<td>"+v.getString("caption")+"</td>");
        }
        result.append("</tr>");
        
        for (int i=0;i<subjectList.size();i++){
            v=subjectList.getValues(i);
            filter.put("subject_id", v.getInteger("id"));
            result.append("<tr>");
            result.append("<td>"+v.getString("subject_name")+"</td>");
            for (int j=0;j<skillList.size();j++){
                v2=skillList.getValues(j);
                filter.put("skill_id",v2.getInteger("id"));
                int row = (curriculumDetails.locate(filter));
                if (row >= 0){
                    v3 = curriculumDetails.getValues(row);
                    result.append("<td>&nbsp;"+v3.getInteger("hour_per_week")+"&nbsp;</td>");
                }  else
                    result.append("<td>&nbsp;</td>");
                    
            }
            result.append("</tr>");
        }
        
        result.append("</table>");
        
        return result.toString();
    };
    
    private String getCurriculumReport() throws Exception{
        StringBuilder result = new StringBuilder();
        Values v;
        
        curriculumDetails = DataModule.getDataset("curriculum_detail");
        curriculumDetails.open();
        
        result.append("<h1>Учебный план</h1>");
        result.append(getReportHeader());
        
        
        Dataset curriculumList = DataModule.getSQLDataset("select * from curriculum");
        curriculumList.open();
        for (int i=0;i<curriculumList.size();i++){
            v= curriculumList.getValues(i);
            result.append("<b>"+v.getString("caption")+"</b>");
            result.append(getCurriculumDetails(v));
        }
        return result.toString();
    }
}
