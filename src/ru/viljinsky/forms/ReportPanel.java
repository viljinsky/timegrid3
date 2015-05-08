
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
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import ru.viljinsky.reports.IReportBuilder;
import ru.viljinsky.reports.ReportBuilder;
import ru.viljinsky.reports.Browser;



public class ReportPanel extends JPanel implements IOpenedForm,IReportBuilder {
    public static final String RP_PUBLISH = "RP_PUBLISH";
    public static final String RP_RELOAD = "RP_RELOAD";
    
    Browser browser = new Browser(){

        @Override
        public String getContentHtml(URL url) throws Exception{
            Map<String,String> linkMap = new HashMap<>();
            linkMap.put("/.", RP_HOME);
            linkMap.put("/page1.html", RP_SCHEDULE_VAR_1);
            linkMap.put("/page2.html", RP_SCHEDULE_VAR_2);
            linkMap.put("/page3.html", RP_SCHEDULE_TEACHER);
            linkMap.put("/page4.html", RP_SCHEDULE_ERRORS);
            
            String path = url.getPath();
            String reportName = linkMap.get(path);
            System.out.println("-->"+reportName);
            try{
                String reportText = new ReportBuilder().getReport(linkMap.get(path));
                return ReportBuilder.createPage(reportText);
            } catch (Exception e){
                e.printStackTrace();
                throw new Exception("GET_HTML_ERROR"+e.getMessage());
            }
        }

    };
    
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
        add(browser);
        commands.setCommandList(new String[]{
            RP_PUBLISH
            }
        );
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
    }
 
    public void doCommand(String command){
        try{
            switch(command){
//                case RP_RELOAD:
//                    browser.reload();
//                    break;
//                case RP_HOME:    
//                    browser.home();//setAddress("http://loaclhost:8080/.");
//                    break;
                case RP_PUBLISH:
//                        URI uri = new URI("http://www.timetabler.narod.ru");
//                        Desktop desktop = Desktop.getDesktop();
//                        desktop.browse(uri);                    
                    publichReport();
                    break;
                default:    
                    throw new Exception("UNKNOW_COMMAND ");    
            }
//            text.setCaretPosition(0);
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
            
        
            Map<String,String> reportMap = new HashMap<>();
            reportMap.put(RP_HOME  , "/index.html");        
            reportMap.put(RP_SCHEDULE_VAR_1  , "/page1.html");
            reportMap.put(RP_SCHEDULE_VAR_2  , "/page2.html");
            reportMap.put(RP_SCHEDULE_TEACHER, "/page3.html");
            reportMap.put(RP_SCHEDULE_ERRORS , "/page4.html");


            ReportBuilder repopBuilder = new ReportBuilder();
            String txt,html;
            try{
                for (String reportName:reportMap.keySet()){

                    txt = repopBuilder.getReport(reportName);
                    html = ReportBuilder.createPage(txt);
                    path = file.getPath()+reportMap.get(reportName);

                    printReport(path, html);
                }
                JOptionPane.showMessageDialog(this, "OK");
            } catch (Exception e){
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }
    
    public void updateAction(Action action){
    }
    
}
