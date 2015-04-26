/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

/**
 *
 * @author вадик
 */
public interface ICommand{
    public void doCommand(String command);
    public void updateAction(Action a);
    public void updateActionList();
    public JPopupMenu getPopup();
    public void addMenu(JMenu menu);
}
