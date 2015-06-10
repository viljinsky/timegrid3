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
    String name;
    String title;
    String page;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    

    public ReportInfo(String reportName) {
        this.name = reportName;
        switch (reportName) {
            case ReportBuilder.RP_HOME:
                title = "индекс";
                page = "/";
                break;
            case ReportBuilder.RP_CURRICULUM:
                title = "учебный план";
                page = "curriculum.html";
                break;
            case ReportBuilder.RP_SCHEDULE_TEACHER:
                title = "преподаватели";
                page = "teacher.html";
                break;
            case ReportBuilder.RP_SCHEDULE_VAR_1:
                title = "расписание вар 2";
                page = "schedule_var1.html";
                break;
            case ReportBuilder.RP_SCHEDULE_VAR_2:
                title = "расписание вар 2.";
                page = "schedule_var2.html";
                break;
            case ReportBuilder.RP_SCHEDULE_ERRORS:
                title = "ошибки";
                page = "errors.html";
                break;
        }
    }
    
}
