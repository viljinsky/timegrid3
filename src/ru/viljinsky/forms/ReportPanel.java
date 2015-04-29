/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import ru.viljinsky.reports.ReportBuilder;

/**
 *
 * @author вадик
 */
public class ReportPanel extends JPanel implements IOpenedForm {
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
//        text = new JTextArea();
        add(new JScrollPane(text));
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        commands.setCommandList(new String[]{"COMMAND1","COMMAND2","COMMAND3","COMMAND4"});
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
        text.setContentType("text/html");
        try{
            switch(command){
                case "COMMAND1":
                    text.setText(new ReportBuilder().getScheduleReport());
                    break;
                case "COMMAND2":
                    text.setText(new ReportBuilder().getSchedueReport2());
                    break;
                case "COMMAND3":
                    text.setText(new ReportBuilder().getScheduleError());
                    break;
                case "COMMAND4":
                    text.setText(new ReportBuilder().getTeacherSchedule());
                    break;
                default:    
                    throw new Exception("UNKNOW_COMMAND ");    
            }
            text.setCaretPosition(0);
        } catch (Exception e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
    
    public void updateAction(Action action){
    }
    
    
//    public void command1() throws Exception{
//        text.setContentType("text/html");
//        text.setCaretPosition(0);
//        
//    }
//    
//    public void command2() throws Exception{
//        text.setContentType("text/html");
//        text.setCaretPosition(0);
//    }
}
