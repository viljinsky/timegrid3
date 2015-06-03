/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JPanel;
import ru.viljinsky.dialogs.BaseDialog;

/**
 *
 * @author вадик
 */

public class ShiftDialog extends BaseDialog{
    protected DBShiftPanel drawPanel;
    JPanel controls;

    @Override
    public Container getPanel() {
        JButton btn;
        controls =  new JPanel(new FlowLayout(FlowLayout.LEFT));
        drawPanel = new DBShiftPanel();
        btn = new JButton("SELECT_ALL");
        controls.add(btn);
        btn.addActionListener(this);
        
        btn = new JButton("UNSELECT_ALL");
        controls.add(btn);
        btn.addActionListener(this);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(drawPanel,BorderLayout.CENTER);
        panel.add(controls,BorderLayout.PAGE_END);
        
        getContentPane().add(panel,BorderLayout.CENTER);
        drawPanel.setAllowEdit(true);
        return panel;
        
    }

    
    
    public void setSelected(List<Integer[]> list){
//        drawPanel.setSelected(list);
    }
    
    public List<Integer[]> getSelected(){
        return new ArrayList<Integer[]>();
//        return drawPanel.getSelected();
    }
    
    public Set<Point> getAdded(){
        return drawPanel.getAdded();
    }
    
    public Set<Point> getRemoved(){
        return drawPanel.getRemoved();
    }
    
    @Override
    public void doOnEntry() throws Exception {
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command){
            case "SELECT_ALL":
                drawPanel.selectAll();
                break;
            case "UNSELECT_ALL":
                drawPanel.unSelectAll();
                break;
            default:
                super.actionPerformed(e);
        }
    }
    
    
    
}
