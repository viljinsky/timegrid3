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
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import ru.viljinsky.reports.IReportBuilder;
import ru.viljinsky.reports.ReportBuilder;

/**
 *
 * @author вадик
 */
public class ReportPanel extends JPanel implements IOpenedForm,IReportBuilder {
    public static final String RP_PUBLISH = "RP_PUBLISH";
    
    JEditorPane text = new JEditorPane();
    JLabel statusLabel = new JLabel();

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
        add(statusLabel,BorderLayout.PAGE_END);
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        commands.setCommandList(new String[]{
            RP_INDEX,
            RP_SCHEDULE_VAR_1,
            RP_SCHEDULE_VAR_2,
            RP_SCHEDULE_TEACHER,
            RP_SCHEDULE_ERRORS,
            RP_PUBLISH
            }
        );
       
        text.addHyperlinkListener(new HyperlinkListener() {

            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                String link = "";
                if (e.getURL()==null)
                    link = e.getDescription();
                else
                    link = e.getURL().toString();
                
                if (!link.startsWith("http:")){
                    link = "http://localhost:8080/"+link;
                }
                
                URL url =null;
                try{
                    url=new URL(link);
                } catch (Exception ee){
                    ee.printStackTrace();
                }
                
                if (e.getEventType()==HyperlinkEvent.EventType.ACTIVATED){
                    System.out.println(link);
                    if (url!=null){
                        System.out.println("protocol : "+url.getProtocol());
                        System.out.println("host     : "+url.getHost());
                        System.out.println("port     : "+url.getPort());
                        System.out.println("path     : "+url.getPath());
                        System.out.println("file     : "+url.getFile());
                        System.out.println("query    : "+url.getQuery());
                        
                        
                    }
                } else if (e.getEventType()==HyperlinkEvent.EventType.ENTERED){
                    statusLabel.setText(link);
                };
            }
        });
        
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
                case RP_SCHEDULE_VAR_2:
                case RP_SCHEDULE_TEACHER:
                case RP_SCHEDULE_ERRORS:
                case RP_INDEX:    
                    showReport(command);
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
    
    public void showReport(String command) throws Exception{
        text.setEditable(false);
        String reportText = new ReportBuilder().getReport(command);
        text.setContentType("text/html");
        String html = ReportBuilder.createPage(reportText);
        text.setText(html);
        
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
        
        if (JOptionPane.showConfirmDialog(this,"Export HTML","Export",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)
            return;
        Map<String,String> reportMap = new HashMap<>();
        reportMap.put(RP_INDEX  , "index.html");        
        reportMap.put(RP_SCHEDULE_VAR_1  , "page1.html");
        reportMap.put(RP_SCHEDULE_VAR_2  , "page2.html");
        reportMap.put(RP_SCHEDULE_TEACHER, "page3.html");
        reportMap.put(RP_SCHEDULE_ERRORS , "page4.html");
        
        text.setContentType("text/plain");
        
        ReportBuilder repopBuilder = new ReportBuilder();
        String txt,html,path;
        try{
            for (String reportName:reportMap.keySet()){

                txt = repopBuilder.getReport(reportName);
                html = ReportBuilder.createPage(txt);
                path = ".//site//"+reportMap.get(reportName);

                printReport(path, html);
            }
            JOptionPane.showMessageDialog(this, "OK");
        } catch (Exception e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
        
        
    }
    
    public void updateAction(Action action){
    }
    
}
