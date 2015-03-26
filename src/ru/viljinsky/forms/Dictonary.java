/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import ru.viljinsky.DataModule;
import ru.viljinsky.Dataset;
import ru.viljinsky.Grid;

/**
 *
 * @author вадик
 */

class DictPanel extends JPanel{
    Grid grid = new Grid();
    
    
    public DictPanel(){
        setLayout(new BorderLayout());
        add(new JScrollPane(grid));
    }
    
    public void setDataset(Dataset dataset){
        grid.setDataset(dataset);
    }
}

public class Dictonary extends JFrame{
    JTabbedPane tabbedPane = new JTabbedPane();
    String[] tableNames = {"subject","day_list","bell_list","building"};
    Controls controls=new Controls();

    class Controls extends JPanel implements ActionListener{
        String[] buttons={"ADD","EDIT","DELETE"};
        public Controls(){
            super();
            setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
            JButton button;
            Box box;
            for (String btnName:buttons){
                box=Box.createVerticalBox();
                
                button= new JButton(btnName);
                button.addActionListener(this);
                box.add(button);
                add(box);
                add(Box.createVerticalStrut(12));
            }
            setBorder(new EmptyBorder(12, 6, 12, 6));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            doCommand(e.getActionCommand());
        }
    }
    
    public void doCommand(String command){
        DictPanel panel = (DictPanel)tabbedPane.getSelectedComponent();
        if (panel!=null)
            try{
                switch(command){
                    case "ADD":
                        panel.grid.append();
                        break;
                    case "EDIT":
                        panel.grid.edit();
                        break;
                    case "DELETE":
                        panel.grid.delete();
                        break;
                }
            } catch (Exception e){
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
    }
    
    public Dictonary() {
        JPanel content = new JPanel(new BorderLayout());
        
        content.setPreferredSize(new Dimension(500,400));
        content.add(tabbedPane);
        content.add(controls,BorderLayout.EAST);
                
        setContentPane(content);
        
    }
   
    public void open() throws Exception{
        DataModule dataModule = DataModule.getInstance();
        Dataset dataset;
        DictPanel panel;
        for (String tableName:tableNames){
            dataset = dataModule.getDataset(tableName);
            dataset.open();
            panel = new DictPanel();
            panel.setDataset(dataset);
            tabbedPane.addTab(tableName,panel);
        }
       
    }
    
    public static void main(String[] args) throws Exception{
        Dictonary frame = new Dictonary();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        
        DataModule.getInstance().open();
        frame.open();
    }
    
}
