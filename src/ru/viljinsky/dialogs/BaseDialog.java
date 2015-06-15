/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.im.InputContext;
import java.net.URL;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import ru.viljinsky.forms.IAppError;

/**
 *
 * @author вадик
 */

public abstract class BaseDialog extends JDialog implements ActionListener,IAppError{
    private static final Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);
    private static final Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
    public static int RESULT_NONE = 0;
    public static int RESULT_OK = 1;
    public static int RESULT_CANCEL = 2;
    public static int RESULT_IGNORE = 3;
    
    static Locale ru = new Locale("ru","RU");
    
    public int modalResult = RESULT_NONE;
    
    public Integer getResult(){
        return modalResult;
    }
    
    public Integer showModal(Component owner){
        setMinimumSize(new Dimension(400,300));
        pack();
        
        int x,y;
        Point p;
        Dimension d;
        
        if (owner!=null){
            p = owner.getLocationOnScreen();
            d = owner.getSize();
            x = p.x+(d.width-getWidth())/2;
            y = p.y+(d.height-getHeight())/2;
        } else {
            d = getToolkit().getScreenSize();
            x = (d.width-getWidth())/2;
            y = (d.height-getHeight())/2;
        }
        
        setLocation(x,y);
        setVisible(true);
        return modalResult;
    }
    
    public BaseDialog(){
        InputContext iCon = getInputContext();
        if(iCon.selectInputMethod(ru)){
            System.out.println("Locale OK");
        }
        setIconImage(createFrameIcon());
        setModal(true);
        Container content = getContentPane();
        content.setLayout(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton buton;
        
        buton= new JButton("OK");
        buton.addActionListener(this);
        buttonPanel.add(buton);
        
        buton= new JButton("CANCEL");
        buton.addActionListener(this);
        buttonPanel.add(buton);
        JPanel p = (JPanel)getPanel();
        content.add(p);
        content.add(buttonPanel,BorderLayout.PAGE_END);
        
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()){
            case "OK":
                try{
                    setCursor(waitCursor);
                    doOnEntry();
                    modalResult=RESULT_OK;
                    setVisible(false);
                } catch(Exception ex){
                    JOptionPane.showMessageDialog(rootPane, ex.getMessage());
                } finally {
                    setCursor(defaultCursor);
                }
                break;
            case "CANCEL":
                modalResult=RESULT_CANCEL;
                setVisible(false);
                break;
        }
    }
    
    public abstract void doOnEntry() throws Exception;
    
    
    public Container getPanel(){
        JPanel panel = new JPanel();
        panel.setBackground(Color.red);
        return panel;
    }
    
    private Image createFrameIcon(){
        URL url = BaseDialog.class.getResource("/ru/viljinsky/images/icon.png");
        try{
        if (url!=null){
            
            return ImageIO.read(url);
        }
        } catch (Exception e){
        }
        return null;
    }
    
}

