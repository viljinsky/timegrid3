package ru.viljinsky.forms;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;


/**
 *
 * @author вадик
 */


public class CommandMngr {
    CommandListener listener=null;
    Action[] actions = {};

    public CommandMngr() {
    }
    
    public CommandMngr(String[] commands) {
        setCommands(commands);
    }
    
    public Action[] getActions(){
        return actions;
    }
    
    public Action[] getActions(String[] actionList){
        Action[] result = new Action[actionList.length];
        for (int i=0;i<actionList.length;i++){
            if (actionList[i]!=null)
                result[i]=getAction(actionList[i]);
            else
                result[i]=null;
        }
        return result;
    }
    
    public Action getAction(String actionCommand){
        for (Action a:actions)
            if (a.getValue(Action.ACTION_COMMAND_KEY).equals(actionCommand))
                return a;
        return null;
    }
    
    public void updateActionList(){
        if (listener!=null)
            for (Action a:actions)
                listener.updateAction(a);
        
    }
    
    class Act extends AbstractAction{

        public Act(String name) {
            super(name);
            putValue(Action.ACTION_COMMAND_KEY, name);
            putValue(NAME,CommandDictionary.getCommandTranslate(name));
            putValue(SHORT_DESCRIPTION,CommandDictionary.getToolTipText(name));
            
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (listener!=null){
                listener.doCommand(e.getActionCommand());
                for (Action a:actions)
                    listener.updateAction(a);
            }
        }
    }
    
    public void setCommands(String[] commands){
        actions = new Action[commands.length];
        for (int i=0;i<commands.length;i++){
            actions[i]=new Act(commands[i]);
        }
    }
    
    public void addCommandListener(CommandListener listener){
        this.listener = listener;
    }
    
    public void setAction(String actionName,Action action){
        for (int i=0;i<actions.length;i++){
            if (actions[i].getValue(Action.ACTION_COMMAND_KEY).equals(actionName)){
                actions[i]=action;
                return;
            }
        }
        System.err.println("ACTION_NOT_FOUND \""+actionName+"\"");
    }
    
}
