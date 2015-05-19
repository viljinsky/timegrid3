package ru.viljinsky.forms;

import javax.swing.Action;

/**
 *
 * @author вадик
 */


public interface CommandListener {
    public void doCommand(String command);
    public void updateAction(Action action);
    
}
