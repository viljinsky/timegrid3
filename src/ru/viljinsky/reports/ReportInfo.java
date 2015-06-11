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
            case ReportBuilder.RP_HOME:
                title = "индекс";
                page = "/";
                break;
            case ReportBuilder.RP_CURRICULUM:
                title = "учебный план";
                page = "curriculum.html";
                reportClass = CurriculumReport.class;
                break;
            case ReportBuilder.RP_SCHEDULE_TEACHER:
                title = "преподаватели";
                page = "teacher.html";
                reportClass=TeacherReport.class;
                break;
            case ReportBuilder.RP_SCHEDULE_VAR_1:
                title = "расписание вар 1.";
                page = "schedule_var1.html";
                reportClass=ScheduleReport.class;
                break;
            case ReportBuilder.RP_SCHEDULE_VAR_2:
                title = "расписание вар 2.";
                page = "schedule_var2.html";
                reportClass = ScheduleReport2.class;
                break;
            case ReportBuilder.RP_SCHEDULE_ERRORS:
                title = "ошибки";
                page = "errors.html";
                reportClass=ErrorsReport.class;
                break;
        }
    }
    
}
