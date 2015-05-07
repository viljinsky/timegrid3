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
import javax.swing.JFileChooser;
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
class Browser extends JEditorPane{
    public Browser(){
        setEditable(false);
        setContentType("text/html");
        addHyperlinkListener(new HyperlinkListener() {

            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                URL link = null;
                try{
                    if (e.getURL()==null)
                        link= new URL("http://localhost:8080/"+e.getDescription());
                    else 
                        link= e.getURL();
                } catch (Exception ee){
                    ee.printStackTrace();
                }
                HyperlinkEvent.EventType t= e.getEventType();
                
                if(t==HyperlinkEvent.EventType.ACTIVATED){
                    hyperlinkClick(link);
                } else if (t==HyperlinkEvent.EventType.ENTERED){
                    hyperlinkEnter(link);
                }
            }
        });
    }
    
    public void hyperlinkClick(URL link){
    }
    public void hyperlinkEnter(URL link){
    }
}


public class ReportPanel extends JPanel implements IOpenedForm,IReportBuilder {
    public static final String RP_PUBLISH = "RP_PUBLISH";
    
//    JEditorPane text = new JEditorPane();
    Browser text = new Browser(){

        @Override
        public void hyperlinkEnter(URL link) {
            statusLabel.setText(link.toString());
        }

        @Override
        public void hyperlinkClick(URL link) {
            Map<String,String> linkMap = new HashMap<>();
            linkMap.put("/.", RP_INDEX);
            linkMap.put("/page1.html", RP_SCHEDULE_VAR_1);
            linkMap.put("/page2.html", RP_SCHEDULE_VAR_2);
            linkMap.put("/page3.html", RP_SCHEDULE_TEACHER);
            linkMap.put("/page4.html", RP_SCHEDULE_ERRORS);
            
            String path = link.getPath();
            String reportName = linkMap.get(path);
            System.out.println("-->"+reportName);
            try{
                showReport(reportName);
            } catch (Exception e){
                e.printStackTrace();
            }
            
            
        }

    };
    
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
//                        URI uri = new URI("http://www.timetabler.narod.ru");
//                        Desktop desktop = Desktop.getDesktop();
//                        desktop.browse(uri);                    
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
        String reportText = new ReportBuilder().getReport(command);
        String html = ReportBuilder.createPage(reportText);
        text.setText(html);
        text.setCaretPosition(0);
        
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
            reportMap.put(RP_INDEX  , "/index.html");        
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
