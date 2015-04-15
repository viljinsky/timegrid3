/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author вадик
 */
public abstract class BaseDialog extends JDialog implements ActionListener{
    public static int RESULT_NONE = 0;
    public static int RESULT_OK = 1;
    public static int RESULT_CANCEL = 2;
    public static int RESULT_IGNORE = 3;
    
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
                    doOnEntry();
                    modalResult=RESULT_OK;
                    setVisible(false);
                } catch(Exception ex){
                    JOptionPane.showMessageDialog(rootPane, ex.getMessage());
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
    
}

