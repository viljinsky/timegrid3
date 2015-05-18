/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 *   Манагер комманд
 * @author вадик
 */

class CommandDictionary extends HashMap<String, String> implements IAppCommand{
    private static CommandDictionary instance = null;
    private CommandDictionary(){

        
    put(CREATE_CURRICULUM,      "Создать;Создать новый учебный план");
    put(COPY_CURRICULUM,        "Копировать;Копировать учебный план из ранее созданного");
    put(EDIT_CURRICULUM,        "Изменить;Изменить содержание учебного плана");
    put(DELETE_CURRICULUM,      "Удалить;Удалить учебный план");
    
    put(FILL_CURRICULUM,        "Заполнить;Добавить/Удалить предметы в учебный план");
//    put(CLEAR_CURRICULUM,       "Очистить");
    put(EDIT_CURRICULUM_DETAIL, "Редактировать;Изменить свойства учебной дисциплины");
    
    // depart panel
    
    put(CREATE_DEPART,          "Создать класс;Добавить класс в учебный процесс");
    put(EDIT_DEPART,            "Изменить;Свойства учебного класса");
    put(DELETE_DEPART,          "Удалить;Удалить класс из учебного процесса");
    
    put(FILL_GROUP,             "Заполнить");
    put(CLEAR_GROUP,            "CLEAR");
//    public static final String EDIT_SHIFT ="EDIT_SHIFT";
    put(ADD_GROUP,              "Доб.группу");
    put( EDIT_GROUP,            "Изм.группу");
    put(DELETE_GROUP,           "Уд.группу");
    
    put( ADD_STREAM,            "Доб.поток");
    put(EDIT_STREAM,            "Изм.поток");
    put(REMOVE_STREAM,          "Уд.поток");

    // teacher
    
    put(CREATE_TEACHER,         "Создать;Добавить запись о преподавателе");
    put(EDIT_TEACHER,           "Изменить;Изменить запись о преподавателе");
    put(DELETE_TEACHER,         "Удалить;Удалить запись о преподавателе");
    
    // room
    put(CREATE_ROOM,            "Добавить");
    put(EDIT_ROOM,              "Изменить");
    put(DELETE_ROOM,            "Удалить");
    
    
    // shift panel
    put(CREATE_SHIFT,           "Создать график");
    put(REMOVE_SHIFT,           "Удалить");
    put(EDIT_SHIFT,             "Изменить");
    
    // profile_panel
    put(CREATE_PROFILE,         "Создать профиль");
    put(EDIT_PROFILE,           "Изменить");
    put(REMOVE_PROFILE,         "Удалить");
    
    put(TT_CLEAR     , "Очистить");
    put(TT_DELETE    , "Удалить");
    put(TT_PLACE     , "Разместить");
    put(TT_PLACE_ALL , "Разместить все");
    put(TT_FIX       , "Зафиксировать");
    put(TT_UNFIX     , "Отм.фиксирование");
    put(TT_REFRESH   , "Обновить");
    
        
        
    }
    
    public static String getCommandTranslate(String command){
        if (instance==null)
            instance = new CommandDictionary();
        String result = instance.get(command);
        if (result==null)
            return command;
        return result.split(";")[0];
    }
    
    public static String getToolTipText(String command){
        if (instance==null){
            instance=new CommandDictionary();
        }
        String result = instance.get(command);
        if ((result==null) || (result.split(";").length<2))
            return "Нет подсказки";
        return result.split(";")[1];
    }
    
}

public abstract class CommandMngr implements IAppCommand{
    private Action[] actions;
    
    public CommandMngr(){
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
    
    public Action[] getActionList(){
    return actions;
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
            putValue(NAME,CommandDictionary.getCommandTranslate(name));
            putValue(SHORT_DESCRIPTION,CommandDictionary.getToolTipText(name));
            
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
