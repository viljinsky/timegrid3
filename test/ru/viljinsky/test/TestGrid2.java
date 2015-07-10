/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import ru.viljinsky.sqlite.DataModule;
import ru.viljinsky.sqlite.Dataset;
import ru.viljinsky.sqlite.DatasetInfo;
import ru.viljinsky.sqlite.Grid;

/**
 *
 * @author вадик
 */

public class TestGrid2  extends JFrame{
    Grid grid;
    
    class GridContainer extends JPanel implements ListSelectionListener{
        Grid g;
        JLabel label = new JLabel("container");
        public GridContainer(Grid g){
            setLayout(new BorderLayout());
            this.g = g;
            grid.addSelectionListener(this);
            add(new JScrollPane(g));
            add(label,BorderLayout.PAGE_END);
//            grid.addSelectionListener(this);
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()){
                label.setText(label.getText()+"+");
            }
        }
    }
    
    public TestGrid2(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(800,600));
        grid = new Grid();
        panel.add(new GridContainer(grid));
        JButton button = new JButton("test");
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    grid.setSelection(1);
                } catch (Exception ee){
                    System.err.println("-->"+ee.getMessage());
                }
            }
        });
        panel.add(button,BorderLayout.PAGE_START);
        

        setContentPane(panel);
    }
    
    public void open() throws Exception{
        DataModule.open();
        Dataset dataset = DataModule.getDataset("v_teacher");
        grid.setDataset(dataset);
        dataset.open();
    }
    
    public static void main(String[] args){
        TestGrid2 frame = new TestGrid2();
        frame.pack();
        frame.setVisible(true);
        try{
            frame.open();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
}
