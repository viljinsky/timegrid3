/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import ru.viljinsky.Grid;

/**
 *
 * @author вадик
 */
abstract class SelectPanel extends JPanel implements ActionListener {
    Grid sourceGrid;
    Grid destanationGrid;
    protected JCheckBox chProfileOnly;

    public SelectPanel() {
        setPreferredSize(new Dimension(500, 200));
        setLayout(new BorderLayout());
        sourceGrid = new Grid();
        sourceGrid.setAutoCreateRowSorter(true);
        destanationGrid = new Grid();
        destanationGrid.setAutoCreateRowSorter(true);
        chProfileOnly = new JCheckBox("Только по профилю",null,true);
        chProfileOnly.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    requery();
                } catch (Exception ee){
                    ee.printStackTrace();
                }
            }
        });
        Map<String, String> btns = new HashMap<>();
        btns.put("INCLUDE", ">");
        btns.put("EXCLUDE", "<");
        btns.put("INCLUDE_ALL", ">>");
        btns.put("EXCLUDE_ALL", "<<");
        JButton btn;
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        Box box;
        //            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        box = Box.createVerticalBox();
        box.add(new JScrollPane(sourceGrid));
//        box.add(chProfileOnly);
        panel.add(box);
        add(Box.createHorizontalStrut(6));
        box = Box.createVerticalBox();
        for (String btnName : btns.keySet()) {
            btn = new JButton(btns.get(btnName));
            btn.setActionCommand(btnName);
            //                btn.setMinimumSize(new Dimension(60,25));
            btn.addActionListener(this);
            Box box1 = Box.createVerticalBox();
            box1.setAlignmentX(CENTER_ALIGNMENT);
            box1.add(btn);
            box.add(box1);
            box.add(Box.createVerticalStrut(12));
        }
        panel.add(box);
        panel.add(Box.createHorizontalStrut(6));
        box = Box.createHorizontalBox();
        box.add(new JScrollPane(destanationGrid));
        panel.add(box);
        panel.setBorder(new EmptyBorder(12,6,12,6));
        add(panel);
        add(chProfileOnly,BorderLayout.PAGE_END);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        doCommand(e.getActionCommand());
    }

    public void doCommand(String command) {
        try {
            switch (command) {
                case "INCLUDE":
                    include();
                    break;
                case "EXCLUDE":
                    exclude();
                    break;
                case "INCLUDE_ALL":
                    includeAll();
                    break;
                case "EXCLUDE_ALL":
                    excludeAll();
                    break;
                default:
                    System.err.println(command);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    public void close() throws Exception{
        sourceGrid.setDataset(null);
        destanationGrid.setDataset(null);
    }
    
    public abstract void include() throws Exception;

    public abstract void exclude() throws Exception;

    public abstract void includeAll() throws Exception;

    public abstract void excludeAll() throws Exception;

    public abstract void requery() throws Exception;
    
}
