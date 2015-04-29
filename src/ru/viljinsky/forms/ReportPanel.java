/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import ru.viljinsky.reports.ReportBuilder;
import static ru.viljinsky.reports.ReportBuilder.HTML_PATTERN;
import static ru.viljinsky.reports.ReportBuilder.STYLE;

/**
 *
 * @author вадик
 */
public class ReportPanel extends JPanel implements IOpenedForm {
    public static final String RP_SCHEDULE_VAR_1 = "RP_SCHEDULE_VAR_1";
    public static final String RP_SCHEDULE_VAR_2 = "RP_SCHEDULE_VAR_2";
    public static final String RP_SCHEDULE_TEACHER = "RP_SCHEDULE_TEACHER";
    public static final String RP_SCHEDULE_ERRORS = "RP_SCHEDULE_ERRORS";
    public static final String RP_PUBLISH = "RP_PUBLISH";
    
    JEditorPane text = new JEditorPane();

    CommandMngr commands = new CommandMngr() {

        @Override
        public void updateAction(Action a) {
            ReportPanel.this.updateAction(a);
        }

        @Override
        public void doCommand(String command) {
            ReportPanel.this.doCommand(command);
        }
    };
    
    public ReportPanel(){
        setLayout(new BorderLayout());
        add(new JScrollPane(text));
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        commands.setCommandList(new String[]{RP_SCHEDULE_VAR_1,RP_SCHEDULE_VAR_2,RP_SCHEDULE_TEACHER,RP_SCHEDULE_ERRORS,RP_PUBLISH});
        for (Action a :commands.getActionList()){
            panel.add(new JButton(a));
        }
        add(panel,BorderLayout.PAGE_START);
    }

    @Override
    public void open() throws Exception {
    }

    @Override
    public String getCaption() {
        return REPORTS;
    }

    @Override
    public JComponent getPanel() {
        return this;
    }

    @Override
    public void close() throws Exception {
    }
 
    public void doCommand(String command){
        try{
            switch(command){
                case RP_SCHEDULE_VAR_1:
                    showReport(new ReportBuilder().getScheduleReport());
                    break;
                case RP_SCHEDULE_VAR_2:
                    showReport(new ReportBuilder().getSchedueReport2());
                    break;
                case RP_SCHEDULE_TEACHER:
                    showReport(new ReportBuilder().getTeacherSchedule());
                    break;
                case RP_SCHEDULE_ERRORS:
                    showReport(new ReportBuilder().getScheduleError());
                    break;
                case RP_PUBLISH:
                    publichReport();
                    break;
                default:    
                    throw new Exception("UNKNOW_COMMAND ");    
            }
            text.setCaretPosition(0);
        } catch (Exception e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
    
    public void showReport(String reportText){
        text.setContentType("text/html");
        String html = ReportBuilder.createPage(reportText);
        text.setText(html);
    }
    
    public void publichReport() throws Exception{
        text.setContentType("text/plain");
        
        ReportBuilder repopBuilder = new ReportBuilder();
        
        String txt = repopBuilder.getScheduleReport();
        String html = ReportBuilder.createPage(txt);
        
        try{
            File file = new File(".//site//html1.html");
            BufferedWriter bw = new BufferedWriter(new FileWriter(file)); 
            bw.write(html);
            bw.close();
        } catch (IOException e){
            throw new Exception("Ошибка записи\n"+e.getMessage());
        }
        
    }
    
    public void updateAction(Action action){
    }
    
}
