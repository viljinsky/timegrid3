/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.reports;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 *
 * @author вадик
 */
public abstract class Browser extends JPanel {
    Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);
    Cursor defaultCursor = Cursor.getDefaultCursor();
    List<URL> stack = new ArrayList<>();
    int pageIndex = -1;
    public static final String BTN_HOME     = "HOME";
    public static final String BTN_PRIOR    = "PRIOR";
    public static final String BTN_NEXT     = "NEXT";
    public static final String BTN_RELOAD   = "RELOAD";
    Controls controls = new Controls();
    
    class Controls extends JPanel implements ActionListener{
        
        public Controls(){
            setLayout(new FlowLayout(FlowLayout.LEFT));
            JButton button;
            button = new JButton(BTN_HOME);
            button.addActionListener(this);
            add(button);
            button = new JButton(BTN_PRIOR);
            button.addActionListener(this);
            add(button);
            button = new JButton(BTN_NEXT);
            button.addActionListener(this);
            add(button);
            button = new JButton(BTN_RELOAD);
            button.addActionListener(this);
            add(button);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            switch (command){
                case BTN_HOME:
                    home();
                    break;
                case BTN_NEXT:
                    next();
                    break;
                case BTN_PRIOR:
                    prior();
                    break;
                case BTN_RELOAD:
                    reload();
                    break;
            }
        }
    }
    
    
    JEditorPane text = new JEditorPane();
    JLabel statusLabel = new JLabel();
    public String host = "localhost";
    public String protocol = "http";
    public int port = 8080;
    URL address = null;
    String homePage = "http://localhost:8080/.";

    public void addControl(JComponent control){
        controls.add(control);
    }
    public Browser() {
        setLayout(new BorderLayout());
        add(controls,BorderLayout.PAGE_START);
        add(new JScrollPane(text));
        add(statusLabel, BorderLayout.PAGE_END);
        text.setEditable(false);
        text.setContentType("text/html");
        text.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                URL link = null;
                String path;
                try {
                    if (e.getURL() == null) {
                        path = e.getDescription();
                        if (!path.startsWith("/")) {
                            path = "/" + path;
                        }
                        link = new URL(protocol, host, port, path);
                    } else {
                        link = e.getURL();
                    }
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
                HyperlinkEvent.EventType t = e.getEventType();
                if (t == HyperlinkEvent.EventType.ACTIVATED) 
                        setAddress(link);
                else if (t == HyperlinkEvent.EventType.ENTERED) 
                        statusLabel.setText(link.toString());
                
//                    hyperlinkEnter(link);
                
            }
        });
    }

    public void setAddress(URL address){
        Browser.this.setCursor(waitCursor);
        try{
            this.address = address;
            pageIndex = stack.indexOf(address);
            if (pageIndex<0){
                stack.add(address);
                pageIndex=stack.indexOf(address);
            }
            System.out.println("-->"+pageIndex);

            if (!address.getHost().equals(host)){
                Desktop desktop = Desktop.getDesktop();
                URI uri = new URI(address.toString());
                desktop.browse(uri);
            } else {
                text.setText(getContentHtml(address));
                text.setCaretPosition(0);
            }
        } catch (Exception e){
            text.setText("<h1>Error page</h1><b>"+e.getMessage()+"</b>");
        } finally {
            Browser.this.setCursor(defaultCursor);
        }
    }

    public void setAddress(String address) {
        try{
            setAddress(new URL(address));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

//    public void hyperlinkEnter(URL link) {
//        statusLabel.setText(link.toString());
//    }

    public abstract String getContentHtml(URL url) throws Exception;

    public void reload() {
        if (address != null) 
                setAddress(address);
    }

    public void home() {
        setAddress(homePage);
    }
    
    public void next(){
        if (pageIndex<stack.size()-1)
                setAddress(stack.get(pageIndex+1));
    }
    
    public void prior(){
        if (pageIndex>0){
            setAddress(stack.get(pageIndex-1));
        }
    }
    
    protected void setHtml(String html){
        text.setText(html);
    }
    
}
