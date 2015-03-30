/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;

/**
 *
 * @author вадик
 */

class TestPanel extends JComponent implements Scrollable{

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return new Dimension(1000,1000);
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 10;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 10;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}


public class TestScroll extends JFrame{
    public TestScroll(){
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(500,400));
        setContentPane(panel);
        panel.add(new JScrollPane(new TestPanel()));
    }
    
    public static void main(String[] args){
        TestScroll frame = new TestScroll();
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
