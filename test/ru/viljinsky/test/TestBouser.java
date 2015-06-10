/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.test;

import java.awt.BorderLayout;
import java.net.URL;
import javax.swing.JFrame;
import javax.swing.JPanel;
import ru.viljinsky.reports.Browser;

/**
 *
 * @author вадик
 */

class DefaultBrouser extends Browser{

    @Override
    public String getContentHtml(URL url) throws Exception {
        String path = url.getPath();
        String host = url.getHost() ;
        String protocol =url.getProtocol();
        String query =url.getQuery();
        String file = url.getFile();
        Integer port = url.getPort();
        
        return "<h1>Привет браузер</h1>"
                + "<ul>"
                + "<li><a href='/'>home</a></li>"
                + "<li><a href='page1/images/favicon.ico'>page1/images/favicon.ico</a></li>"
                + "<li><a href='page2.html'>page2.html</a></li>"
                + "<li><a href='page3.html?p1=12&p2=qwwer'>page3.html?p1=12&p2=qwwer</a></li>"
                + "<li><a href='page4.html#qwerty?p1=12&p2=qwwer'>page4.html#qwerty?p1=12&p2=qwwer'</a></li>"
                + "<li><a href='http://localhost:8080/page5.html?p1=12&p2=qwwer'>http://localhost:8080/page5.html?p1=12&p2=qwwer</a></li>"
                + "<li><a href='http://www.составительрасписания.рф'>http://www.составительрасписания.рф</a></li>"
                + "</ul>"
                + "<br>"+
                String.format("protocol = %s <br> host = %s <br> port %d<br> path = %s <br> query= %s <br> file =%s ",protocol,host,port,path,query,file );
    }
}

public class TestBouser extends JFrame {
    Browser browser = new DefaultBrouser();
    
    public TestBouser() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(browser);
        setContentPane(panel);
        
//        browser.setAddress("http://www.составительрасписания.рф");
        browser.setAddress("http://localhost:8080/page1.html");
        
    }
    
    
    public static void main(String[] args){
        TestBouser frame = new TestBouser();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
    
}
