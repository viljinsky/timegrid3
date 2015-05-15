/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.test;

import ru.viljinsky.sqlite.DataModule;
import java.awt.Container;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import ru.viljinsky.forms.*;
/**
 *
 * @author вадик
 */
public class Test1 extends JFrame{
    IOpenedForm iform=null;
    public Test1(){
        Container form = FormBuilder.createForm(FormBuilder.TEACHER);
        iform = (IOpenedForm)form;
        setContentPane(form);
    }
    
    public void open(){
        try{
            iform.open();
        } catch (Exception e){
            JOptionPane.showMessageDialog(rootPane, e.getMessage());
        }
    }
    
    public static void main(String[] args){
        try{
            DataModule.getInstance().open();
        } catch (Exception e){
        }
        Test1 frame = new Test1();
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.open();
    }
    
}
