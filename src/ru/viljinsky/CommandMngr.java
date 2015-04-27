/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 *   Манагер комманд
 * @author вадик
 */
public abstract class CommandMngr{
    private Action[] actions;
    
    public CommandMngr(){
    }
    
    public Action[] getActionList(){
    return actions;
    }
    public CommandMngr(String[] commands){
        setCommandList(commands);
    }
    
    public void setCommandList(String[] list){
        actions=new Action[list.length];
        for (int i=0;i<actions.length;i++){
            String[] s = list[i].split(";");
            actions[i]=new Act(s[0]);
        }
    }

    public Action getAction(String command){
        for (Action a:actions){
            if (a.getValue(Action.ACTION_COMMAND_KEY).equals(command))
                return a;
        }
        return null;
    }
    
    class Act extends AbstractAction{

        public Act(String name) {
            super(name);
            putValue(ACTION_COMMAND_KEY, name);
            switch (name){
                case "CREATE_CURRICULUM":
                case "CREATE_TEACHER":
                case "CREATE_ROOM":
//                case "CREATE_DEPART":
                    putValue(NAME, "Добавить");
                    break;
                case "EDIT_CURRICULUM":
                case "EDIT_TEACHER":
                case "EDIT_ROOM":
                case "EDIT_DEPART":
                    putValue(NAME, "Изменить");
                    break;
                case "DELETE_CURRICULUM":
                case "DELETE_TEACHER":
                case "DELETE_ROOM":
                case "DELETE_DEPART":
                    putValue(NAME, "Удалить");
                    break;
                    
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            doCommand(e.getActionCommand());
            updateActionList();
        }
    }
    
    public void updateActionList(){
        for (Action a:actions)
            updateAction(a);
    }
    
    protected String getActionCommand(Action a){
        return (String)a.getValue(Action.ACTION_COMMAND_KEY);
    }
    
    public abstract void updateAction(Action a);
    public abstract void doCommand(String command);
    
    
}
