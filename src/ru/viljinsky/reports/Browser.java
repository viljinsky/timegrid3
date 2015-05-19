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
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
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
import ru.viljinsky.forms.CommandMngr2;
import ru.viljinsky.forms.IAppCommand;
import ru.viljinsky.forms.ICommandListener;

/**
 *
 * @author вадик
 */

//interface ICommandMngr2{
//    public void doCommand(String command);
//    public void updateAction(Action action);
//}
//
//
//class CommandMngr2{
//    ICommandMngr2 manager=null;
//    Action[] actions = {};
//
//    public CommandMngr2() {
//    }
//    
//    public CommandMngr2(String[] commands) {
//        setCommands(commands);
//    }
//    
//    public Action[] getActions(){
//        return actions;
//    }
//    
//    public void updateActionList(){
//        if (manager!=null)
//            for (Action a:actions)
//                manager.updateAction(a);
//        
//    }
//    
//    class Act extends AbstractAction{
//
//        public Act(String name) {
//            super(name);
//            putValue(Action.ACTION_COMMAND_KEY, name);
//        }
//
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            if (manager!=null){
//                manager.doCommand(e.getActionCommand());
//                for (Action a:actions)
//                    manager.updateAction(a);
//            }
//        }
//    }
//    
//    public void setCommands(String[] commands){
//        actions = new Action[commands.length];
//        for (int i=0;i<commands.length;i++){
//            actions[i]=new Act(commands[i]);
//        }
//    }
//    
//}
public abstract class Browser extends JPanel implements IAppCommand,ICommandListener {
    Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);
    Cursor defaultCursor = Cursor.getDefaultCursor();
    List<URL> stack = new ArrayList<>();
    int pageIndex = -1;
    
    CommandMngr2 maneger;
    JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));

    @Override
    public void doCommand(String command) {
        try{
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
        } catch (Exception e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    @Override
    public void updateAction(Action action) {
        String command = (String)action.getValue(Action.ACTION_COMMAND_KEY);
        switch (command){
            case BTN_HOME:
                break;
            case BTN_NEXT:
                action.setEnabled(pageIndex<stack.size()-1);
                break;
            case BTN_PRIOR:
                action.setEnabled(pageIndex>0);
                break;
            case BTN_RELOAD:
                break;
        }
    }
    
    
//      class Controls extends JPanel{  
//        public Controls(){
//            setLayout(new FlowLayout(FlowLayout.LEFT));
//        }
//    }
    
    
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
        
        maneger = new CommandMngr2(new String[]{BTN_HOME,BTN_PRIOR,BTN_NEXT,BTN_RELOAD});
        maneger.addCommandListener(this);
        for (Action a:maneger.getActions()){
            controls.add(new JButton(a));
        }
        maneger.updateActionList();
       
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
//            System.out.println("-->"+pageIndex);

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
            maneger.updateActionList();
        }
    }

    public void setAddress(String address) {
        try{
            setAddress(new URL(address));
            maneger.updateActionList();
        } catch (Exception e){
            e.printStackTrace();
        }
    }


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
