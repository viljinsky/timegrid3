/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import ru.viljinsky.Grid;

/**
 *
 * @author вадик
 */
class GridPanel extends JPanel {
    Grid grid;
    JLabel lblStatus = new JLabel("Status");
    JLabel lblTitle = new JLabel("Title");
    JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    public GridPanel(String title, Grid grid) {
        super(new BorderLayout());
        this.grid = grid;
        statusPanel.add(lblStatus);
        titlePanel.add(lblTitle);
        lblTitle.setText(title);
        add(titlePanel, BorderLayout.PAGE_START);
        add(new JScrollPane(grid), BorderLayout.CENTER);
        add(statusPanel, BorderLayout.PAGE_END);
        grid.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                gridSelectionChange();
            }
        });
    }

    protected void gridSelectionChange() {
        lblStatus.setText(String.format("Запись %d  из %d", grid.getSelectedRow(), grid.getRowCount()));
    }

    void AddButton(JComponent component) {
        titlePanel.add(component);
    }
    
    public void addAction(Action action){
        JButton button = new JButton(action);
        titlePanel.add(button);
    }
    
}
