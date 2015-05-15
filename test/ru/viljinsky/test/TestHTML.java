/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.net.URL;
import javax.swing.JFrame;
import javax.swing.JPanel;
import ru.viljinsky.sqlite.DataModule;
import ru.viljinsky.reports.Browser;
import ru.viljinsky.reports.PageGenerator;

/**
 *
 * @author вадик
 */
public class TestHTML extends JPanel{
    PageGenerator generator = new PageGenerator();
    Browser browser = new Browser() {

        @Override
        public String getContentHtml(URL url) throws Exception {
            System.out.println(url.toString());
            return generator.getPage(url);
        }

        @Override
        public void home() {
            setHtml(generator.getDefaultPage());
        }
    };
    
    public TestHTML(){
        setPreferredSize(new Dimension(800,600));
        setLayout(new BorderLayout());
        add(browser);
        browser.home();
        
    }
    public static void main(String[] args) throws Exception{
        DataModule.open();
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new TestHTML());
        frame.pack();
        frame.setVisible(true);
    } 
    
}
