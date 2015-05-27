package ru.viljinsky.forms;

import java.util.HashMap;

/**
 *
 * @author вадик
 */


public class CommandDictionary extends HashMap<String, String> implements IAppCommand{
    private static CommandDictionary instance = null;
    private CommandDictionary(){

    put(REFRESH,                "Обновить");   
        
    put(CREATE_CURRICULUM,      "Создать;Создать новый учебный план");
    put(COPY_CURRICULUM,        "Копировать;Копировать учебный план из ранее созданного");
    put(EDIT_CURRICULUM,        "Изменить;Изменить содержание учебного плана");
    put(DELETE_CURRICULUM,      "Удалить;Удалить учебный план");
    
    put(FILL_CURRICULUM,        "Заполнить;Добавить/Удалить предметы в учебный план");
    put(EDIT_CURRICULUM_DETAIL, "Редактировать;Изменить свойства учебной дисциплины");
    
    // depart panel
    
    put(CREATE_DEPART,          "Создать класс;Добавить класс в учебный процесс");
    put(EDIT_DEPART,            "Изменить;Свойства учебного класса");
    put(DELETE_DEPART,          "Удалить;Удалить класс из учебного процесса");
    
    put(FILL_GROUP,             "Заполнить");
    put(CLEAR_GROUP,            "CLEAR");
    
//    public static final String EDIT_SHIFT ="EDIT_SHIFT";
    put(ADD_GROUP,              "Доб.группу");
    put(EDIT_GROUP,            "Изм.группу");
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
    put(EDIT_SHIFT,             "Изменить график;Редактировать график");
    
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
    
    put(TT_SCH_STATE, "Статус;Изменить статус расписания");
    

    // select panel
    put(INCLUDE,        "Включить>");
    put(EXCLUDE,        "<Исключить");
    put(INCLUDE_ALL,    "Вкл.всё>>");
    put(EXCLUDE_ALL,    "<<Искл.всё");
    
    // broser
    
    put(PAGE_HOME,      "Домой;Перейти на первую страницу сайта");
    put(PAGE_PRIOR,     "Пред.-я;Перейти на предыдущую страницу");
    put(PAGE_NEXT,      "След.-я;Перейти на следующую страницу ");
    put(PAGE_RELOAD,    "Обновить;Перезагрузить текущую страницу");
    
    // grid
    
    put(GRID_APPEND,"Добавить");
    put(GRID_EDIT,"Изменить");
    put(GRID_DELETE,"Удалить");
    put(GRID_REFRESH,"Обновить");
    put(GRID_REQUERY,"Перезагрузить");
    
    put(CMD_PRIOR,"Назад;Переход к предыдущему расписанию");
    put(CMD_NEXT,"Вперёд;Переход к следующему- расписанию");
    put(CMD_GO_TEACHER,"Преподаватель;Переход к расписанию перподавателя");
    put(CMD_GO_DEPART,"Класс;Переход к расписанию класса");
    put(CMD_GO_ROOM,"Помещение;Переход к расписанию помещения");
    
    
        
        
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
