
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
import java.util.Map;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import ru.viljinsky.dialogs.BaseDialog;
import ru.viljinsky.dialogs.EntryForm;
import ru.viljinsky.reports.ReportBuilder;
import ru.viljinsky.reports.Browser;
import ru.viljinsky.reports.PageProducer;
import ru.viljinsky.reports.ReportInfo;



public class ReportPanel extends JPanel implements IOpenedForm,CommandListener {
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
            ReportInfo info = new ReportInfo(reportName);
            ReportBuilder reportBuilder = new ReportBuilder();
            try{
                // Получить содержимое отчёта
                String reportText = reportBuilder.getReport(info);
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

    @Override
    public void requery() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Продьюсер для счётчиков
     */
    class ExtPoducer extends PageProducer{


        public ExtPoducer(String source) throws Exception{
            TestTag.readSource(source);
            for (String s:TestTag.getTagSet()){
                System.out.println(s);
                System.out.println(TestTag.getTagText(s));
            }
        }
        
        @Override
        public String getReplaceText(String tag) {
            System.out.println("*********>"+tag);
            String result = TestTag.getTagText(tag);
            if (result!=null)
                return result;
            return tag;
        }
        
    
    }
    class Producer extends PageProducer{
        
        ReportInfo info;
        String[] reports;
        String navigator;
        
        public Producer(ReportInfo info,String[] reports){
            super();
            this.info=info;
            this.reports = reports;

            ReportInfo ri;
            StringBuilder nav = new StringBuilder();
            nav.append("<ul>");
            for (String reportName:reports){
                ri=new ReportInfo(reportName);
                nav.append("<li><a href='"+ri.getPage()+"'>"+ri.getTitle()+"</a></li>");
            }
            nav.append("</ul>");
            navigator=nav.toString();
            
            
            
        }

        @Override
        public String getReplaceText(String tag) {
            try{
                switch(tag){
                    case "$TITLE$":
                        return info.getTitle();
                    case "$PAGE_CONTENT$":
                        return new ReportBuilder().getReport(info);
                    case "$NAVIGATOR$":
                        return navigator;
                }
                return tag;
            } catch (Exception e){
                return "<b>ERROR_ON_TAG<b>";
            }
        }
        
        
    }
    

    public static final String EXT_PATTERN = "extPattern";
    
    public void publichReport() throws Exception{
        String path ;
        String extPattern = "D:\\development\\schedule2\\site\\share.txt";
        String patternFileName = "D:\\development\\schedule2\\site\\current\\pattern.html";
        String destanationPath = "D:\\development\\schedule2\\site\\current\\example\\";
        
        EntryForm form = new EntryForm();
        form.setFields(new String[]{"patternFileName;Шаблон;FC_PATH",
            "destanationPath;Путь к папке;FC_DIR",
            "extPattern;;FC_PATH"} );
        
        form.setValue("patternFileName", patternFileName);
        form.setValue("destanationPath",destanationPath);
        form.setValue(EXT_PATTERN, extPattern);
        
        if (form.showModal(null)!=BaseDialog.RESULT_OK){
            return;
        }
        
        
        Map<String,Object> values = form.getValues();
        patternFileName = values.get("patternFileName").toString();
        destanationPath = values.get("destanationPath").toString();
        extPattern=values.get(EXT_PATTERN).toString();
        
        if (!destanationPath.endsWith("\\")){
            destanationPath+="\\";
         }
        
        
        if (JOptionPane.showConfirmDialog(null, String.format("%s \n %s ",destanationPath,patternFileName),"Продолжать",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
            
            ExtPoducer extP = new ExtPoducer(extPattern);
        
            String[] reports = {
                ReportInfo.RP_HOME,
                ReportInfo.RP_CURRICULUM,
                ReportInfo.RP_SCHEDULE_VAR_1,
                ReportInfo.RP_SCHEDULE_VAR_2,
                ReportInfo.RP_SCHEDULE_TEACHER
            };
            
            String html;
            ReportInfo info;
            for (String reportName:reports){
                info = new ReportInfo(reportName);
                
                Producer p = new Producer(info,reports);
                p.loadPattern(patternFileName);
                html = p.execute();
                
                extP.setHtmlPattern(html);
                html = extP.execute();
                
                String s = info.getPage();
                path = destanationPath+(s=="/" ?"index.html":s);

                printReport(path, html);
            }
            JOptionPane.showMessageDialog(this, "OK");
        }
    }
    
    @Override
    public void updateAction(Action action){
    }
    
}
