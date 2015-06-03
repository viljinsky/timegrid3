/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.reports;

import ru.viljinsky.sqlite.DataModule;
import ru.viljinsky.sqlite.Dataset;
import ru.viljinsky.sqlite.Values;

/**
 *
 * @author вадик
 */
class CurriculumReport extends AbstractReport {
    String emptyString = "";
    Dataset curriculumDetails;
    Integer[] summary;
    private static final String SQL_SKILS = "select distinct a.id,a.caption \n" + "from skill a inner join curriculum_detail b on a.id=b.skill_id \n" + "where b.curriculum_id=%d";
    private static final String SQL_SUBJECTS = "select distinct a.id,a.subject_name \n" + "from subject a inner join curriculum_detail b on a.id=b.subject_id\n" + "left join subject_domain d on d.id = a.subject_domain_id \n" + "where b.curriculum_id=%d\n" + "order by a.sort_order;";

    private String getCurriculumDetails(Values vCurriculum) throws Exception {
        StringBuilder result = new StringBuilder();
        Values v,v2,v3,filter;
        filter = new Values();
        filter.put("curriculum_id", vCurriculum.getInteger("id"));
        Dataset skillList = DataModule.getSQLDataset(String.format(SQL_SKILS, vCurriculum.getInteger("id")));
        skillList.open();
        if (skillList.size()==0){
            return emptyString;
        }
        summary = new Integer[skillList.size()];
        for (int i = 0; i < summary.length; i++) {
            summary[i] = 0;
        }
        Dataset subjectList = DataModule.getSQLDataset(String.format(SQL_SUBJECTS, vCurriculum.getInteger("id")));
        subjectList.open();
        result.append("<table>");
        result.append("<tr>");
        result.append("<td>&nbsp;</td>");
        for (int i = 0; i < skillList.size(); i++) {
            v = skillList.getValues(i);
            result.append("<td>" + v.getString("caption") + "</td>");
        }
        result.append("</tr>");
        for (int i = 0; i < subjectList.size(); i++) {
            v = subjectList.getValues(i);
            filter.put("subject_id", v.getInteger("id"));
            result.append("<tr>");
            result.append("<td>" + v.getString("subject_name") + "</td>");
            for (int j = 0; j < skillList.size(); j++) {
                v2 = skillList.getValues(j);
                filter.put("skill_id", v2.getInteger("id"));
                int row = curriculumDetails.locate(filter);
                if (row >= 0) {
                    v3 = curriculumDetails.getValues(row);
                    result.append("<td>&nbsp;" + v3.getInteger("hour_per_week") + "&nbsp;</td>");
                    Integer n = v3.getInteger("hour_per_week") == null ? 0 : v3.getInteger("hour_per_week");
                    summary[j] += n;
                } else {
                    result.append("<td>&nbsp;</td>");
                }
            }
            result.append("</tr>");
        }
        // summary
        result.append("<tr><td>&nbsp;</td>");
        for (int i = 0; i < summary.length; i++) {
            result.append("<td>" + summary[i] + "</td>");
        }
        result.append("</tr>");
        result.append("</table>");
        return result.toString();
    }

    @Override
    public void prepare() throws Exception {
        StringBuilder result = new StringBuilder();
        Values v;
        curriculumDetails = DataModule.getDataset("curriculum_detail");
        curriculumDetails.open();
        result.append("<h1>Учебный план</h1>");
        result.append(getReportHeader());
        Dataset curriculumList = DataModule.getSQLDataset("select * from curriculum");
        curriculumList.open();
        String subReport;
        
        for (int i = 0; i < curriculumList.size(); i++) {
            v = curriculumList.getValues(i);
            subReport = getCurriculumDetails(v);
            if (!subReport.isEmpty()){
                result.append("<b>" + v.getString("caption") + "</b>");
                result.append(subReport);
            }
        }
        html = result.toString();
    }
    
}
