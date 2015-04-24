/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Calendar;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;

/**
 *
 * @author вадик
 */

class MySpinner extends JSpinner{
    SpinnerDateModel model = new SpinnerDateModel();
    
    public MySpinner() {
        super();
        setModel(model);
        setEditor(new JSpinner.DateEditor(this, "HH:mm"));
        setPreferredSize(new Dimension(120,20));
    }
    
}
public class TestSpinner extends JPanel{

    
    public TestSpinner() {
        super(new BorderLayout());
        setPreferredSize(new Dimension(800, 600));
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JSpinner spinner = new MySpinner();
        controls.add(spinner);
        controls.add(new JButton("OK"));
        controls.add(new JTextField(20));
        add(controls,BorderLayout.PAGE_START);
                
    }
    
    public static void main(String[] args){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new TestSpinner());
        frame.pack();
        frame.setVisible(true);
                
    }
    
}
