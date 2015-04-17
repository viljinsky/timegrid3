/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
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
        try{
            grid.setDataset(dataset);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

public class Dictonary extends JDialog{
    JTabbedPane tabbedPane = new JTabbedPane();
    String[] tableNames = {"day_list","bell_list","week","skill","subject","building"};
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
//        super("Справочники");
        setTitle("Справочники");
        setModal(true);
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
    
    private static Dictonary frame = null;
    public static Integer showDialog(JComponent owner) throws Exception{
        if (frame ==null){
            frame = new Dictonary();
            frame.setDefaultCloseOperation(HIDE_ON_CLOSE);
            frame.setModal(true);
            frame.pack();
            if (owner!=null){
                Dimension d = owner.getSize();
                Point p = owner.getLocationOnScreen();
                frame.setLocation(p.x+(d.width-frame.getWidth())/2, p.y+(d.height-frame.getHeight())/2);
            }
            frame.open();
        }
        frame.setVisible(true);
        System.out.println("OK");
        return 0;
    }
    
    public static void main(String[] args) throws Exception{
        Dictonary frame = new Dictonary();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        
        DataModule.open();
        frame.open();
    }
    
}
