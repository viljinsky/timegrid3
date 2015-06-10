
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.awt.BorderLayout;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import ru.viljinsky.reports.IReportBuilder;
import ru.viljinsky.reports.ReportBuilder;
import ru.viljinsky.reports.Browser;
import ru.viljinsky.reports.ReportInfo;



public class ReportPanel extends JPanel implements IOpenedForm,IReportBuilder,CommandListener {
    public static final String RP_PUBLISH = "RP_PUBLISH";
    public static final String RP_RELOAD = "RP_RELOAD";
    
    Browser browser = new Browser(){

        @Override
        public String getContentHtml(URL url) throws Exception{
            
            String reportName;
            
            String path = url.getPath();
            // По пути урла нужно определить имя отчётв
            reportName = ReportBuilder.getReportName(path);
            if (reportName==null){
                throw new Exception("URL_PATH_NOT_FOUND\n"+path);
            }
            
            System.out.println("-->"+reportName);
            ReportBuilder reportBuilder = new ReportBuilder();
            try{
                // Получить содержимое отчёта
                String reportText = reportBuilder.getReport(reportName);
                // Сгенерировать HTML код страницы
                return ReportBuilder.createPage(reportText);
            } catch (Exception e){
                e.printStackTrace();
                throw new Exception("GET_HTML_ERROR"+e.getMessage());
            }
        }

    };
    
    CommandMngr commands = new CommandMngr();
    
    public ReportPanel(){
        setLayout(new BorderLayout());
        add(browser);
        commands.setCommands(new String[]{
            RP_PUBLISH
            }
        );
        commands.addCommandListener(this);
        browser.addControl(new JButton(commands.getAction(RP_PUBLISH)));
        
    }

    @Override
    public void open() throws Exception {
        browser.home();
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
        browser.clear();
    }
 
    public void doCommand(String command){
        try{
            switch(command){
                case RP_PUBLISH:
                    publichReport();
                    break;
                default:    
                    throw new Exception("UNKNOW_COMMAND ");    
            }
        } catch (Exception e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
    
    
    protected void printReport(String path,String html) throws Exception{
        BufferedWriter bw = null;
        try{
            File file = new File(path);
            bw = new BufferedWriter(new FileWriter(file)); 
            bw.write(html);
        } catch (IOException e){
            throw new Exception("Ошибка записи\n"+e.getMessage());
        } finally {
            if (bw!=null)
                bw.close();
        }
    }
    
    public void publichReport() throws Exception{
        String path = System.getProperty("user.dir");
        File file = new File(path);
        JFileChooser fc = new JFileChooser(file);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int retValue = fc.showSaveDialog(this);
        if (retValue == JFileChooser.APPROVE_OPTION){
            file=fc.getSelectedFile();
            if (!file.exists()){
                if (!file.mkdir())
                    throw new Exception("CAN_NOT_CREATE_DIR");
            }
            
        
            String[] reports = {RP_HOME,RP_CURRICULUM,RP_SCHEDULE_VAR_1,RP_SCHEDULE_TEACHER};
            ReportBuilder repopBuilder = new ReportBuilder();
            String txt,html;
            
            ReportInfo info;
            for (String reportName:reports){
                info = new ReportInfo(reportName);
                txt = repopBuilder.getReport(info.getName());
                html=ReportBuilder.createPage(txt);
                String s = info.getPage();
                path = file.getPath()+"/"+(s=="/" ?"index.html":s);

                printReport(path, html);
            }
            JOptionPane.showMessageDialog(this, "OK");
        }
    }
    
    @Override
    public void updateAction(Action action){
    }
    
}
