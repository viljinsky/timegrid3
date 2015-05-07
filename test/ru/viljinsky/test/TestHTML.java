/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.test;

import ru.viljinsky.reports.PageGenerator;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import ru.viljinsky.DataModule;


public class TestHTML extends JPanel{
    JTextPane textPane = new JTextPane();
    JLabel label = new JLabel(">");
    PageGenerator generator = new PageGenerator();
    public TestHTML(){
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(800,600));
        add(new JScrollPane(textPane),BorderLayout.CENTER);
        add(label,BorderLayout.PAGE_END);
        textPane.setEditable(false);
        textPane.addHyperlinkListener(new HyperlinkListener() {

            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType()==HyperlinkEvent.EventType.ACTIVATED){
                    System.out.println(e.getDescription());
                    doCommand(e.getDescription());
                } else if(e.getEventType()==HyperlinkEvent.EventType.ENTERED){
                    label.setText(e.getDescription());
                } else if (e.getEventType()==HyperlinkEvent.EventType.EXITED){
                    label.setText("");
                }
            }
        });
        
        textPane.setContentType("text/html");
        textPane.setText(generator.getResponce());
        try{
            DataModule.open();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    public void doCommand(String request){
        String responce = generator.getResponce(request);
        textPane.setText(generator.getDefaultPage()+responce);
    }
    
    public static void main(String[] args){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new TestHTML());
        frame.pack();
        frame.setVisible(true);
    }
    
}
