package ru.viljinsky.forms;

import javax.swing.Action;

/**
 *
 * @author вадик
 */


public interface ICommandListener {
    public void doCommand(String command);
    public void updateAction(Action action);
    
}
