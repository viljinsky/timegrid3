/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.reports;

/**
 *
 * @author вадик
 */
public class ReportInfo {
    
    public static final String RP_HOME = "RP_HOME";
    public static final String RP_CURRICULUM ="RP_CURRICULUM";
    public static final String RP_SCHEDULE_VAR_1 = "RP_SCHEDULE_VAR_1";
    public static final String RP_SCHEDULE_VAR_2 = "RP_SCHEDULE_VAR_2";
    public static final String RP_SCHEDULE_TEACHER = "RP_SCHEDULE_TEACHER";
    public static final String RP_SCHEDULE_ERRORS = "RP_SCHEDULE_ERRORS";
    
    
    
    String reportName;
    String title;
    String page;
    Class reportClass=null;

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }
    
    public Class getReportClass(){
        return reportClass;
    }
    

    public ReportInfo(String reportName) {
        this.reportName = reportName;
        switch (reportName) {
            case RP_HOME:
                title = "индекс";
                page = "/";
                break;
            case RP_CURRICULUM:
                title = "учебный план";
                page = "curriculum.html";
                reportClass = CurriculumReport.class;
                break;
            case RP_SCHEDULE_TEACHER:
                title = "преподаватели";
                page = "teacher.html";
                reportClass=TeacherReport.class;
                break;
            case RP_SCHEDULE_VAR_1:
                title = "расписание вар 1.";
                page = "schedule_var1.html";
                reportClass=ScheduleReport.class;
                break;
            case RP_SCHEDULE_VAR_2:
                title = "расписание вар 2.";
                page = "schedule_var2.html";
                reportClass = ScheduleReport2.class;
                break;
            case RP_SCHEDULE_ERRORS:
                title = "ошибки";
                page = "errors.html";
                reportClass=ErrorsReport.class;
                break;
        }
    }
    
}
