package ru.viljinsky.forms;

import java.util.HashMap;

/**
 *
 * @author вадик
 */


public class CommandDictionary extends HashMap<String, String> implements IAppCommand{
    private static CommandDictionary instance = null;
    private static final String[][] SRC = {
        {REFRESH,                "Обновить"},
        {REFRESH_TREE,           "Обновить"},

        {CREATE_CURRICULUM,      "Создать;Создать новый учебный план"},
        {COPY_CURRICULUM,        "Копировать;Копировать учебный план из ранее созданного"},
        {EDIT_CURRICULUM,        "Изменить;Изменить содержание учебного плана"},
        {DELETE_CURRICULUM,      "Удалить;Удалить учебный план"},

        {FILL_CURRICULUM,        "Заполнить;Добавить/Удалить предметы в учебный план"},
        {EDIT_CURRICULUM_DETAIL, "Редактировать;Изменить свойства учебной дисциплины"},

    // depart panel

        {CREATE_DEPART,          "Создать класс;Добавить класс в учебный процесс"},
        {EDIT_DEPART,            "Изменить;Свойства учебного класса"},
        {DELETE_DEPART,          "Удалить;Удалить класс из учебного процесса"},

        {FILL_GROUP,             "Заполнить"},
        {CLEAR_GROUP,            "CLEAR"},

    //    public static final String EDIT_SHIFT ="EDIT_SHIFT";
        {ADD_GROUP,              "Доб.группу"},
        {EDIT_GROUP,            "Изм.группу"},
        {DELETE_GROUP,           "Уд.группу"},

        { ADD_STREAM,            "Доб.поток"},
        {EDIT_STREAM,            "Изм.поток"},
        {REMOVE_STREAM,          "Уд.поток"},

    // teacher

        {CREATE_TEACHER,         "Создать;Добавить запись о преподавателе"},
        {EDIT_TEACHER,           "Изменить;Изменить запись о преподавателе"},
        {DELETE_TEACHER,         "Удалить;Удалить запись о преподавателе"},

    // room
        {CREATE_ROOM,            "Добавить"},
        {EDIT_ROOM,              "Изменить"},
        {DELETE_ROOM,            "Удалить"},

    // shift panel
        {CREATE_SHIFT,           "Создать график"},
        {REMOVE_SHIFT,           "Удалить"},
        {EDIT_SHIFT,             "Изменить график;Редактировать график"},

    // profile_panel
        {CREATE_PROFILE,         "Создать профиль"},
        {EDIT_PROFILE,           "Изменить"},
        {REMOVE_PROFILE,         "Удалить"},

        {TT_CLEAR     , "Очистить;Удалить все елементы"},
        {TT_DELETE    , "Удалить;Удалить выделенные элементы"},
        {TT_PLACE     , "Разместить;Разместить нераставленные выделенные группы"},
        {TT_PLACE_ALL , "Разместить все;Разместить все нерасставленные группы "},
        {TT_FIX       , "Фикс-ть;Зафиксировать выделенные ячейки"},
        {TT_UNFIX     , "Отм.фикс-ю;Снять фиксацию с выделенных ячеек"},
        {TT_REFRESH   , "Обновить"},

        {TT_SCH_STATE, "Статус;Изменить статус расписания"},


    // select panel
        {INCLUDE,        "Включить>"},
        {EXCLUDE,        "<Исключить"},
        {INCLUDE_ALL,    "Вкл.всё>>"},
        {EXCLUDE_ALL,    "<<Искл.всё"},

    // broser

        {PAGE_HOME,      "Домой;Перейти на первую страницу сайта"},
        {PAGE_PRIOR,     "Пред.-я;Перейти на предыдущую страницу"},
        {PAGE_NEXT,      "След.-я;Перейти на следующую страницу "},
        {PAGE_RELOAD,    "Обновить;Перезагрузить текущую страницу"},

    // grid

        {GRID_APPEND,   "Добавить"},
        {GRID_EDIT,     "Изменить"},
        {GRID_DELETE,   "Удалить"},
        {GRID_REFRESH,  "Обновить"},
        {GRID_REQUERY,  "Перезагрузить"},

        {CMD_PRIOR,     "Назад;Переход к предыдущему расписанию"},
        {CMD_NEXT,      "Вперёд;Переход к следующему- расписанию"},
        {CMD_GO_TEACHER,"Преподаватель;Переход к расписанию перподавателя"},
        {CMD_GO_DEPART, "Класс;Переход к расписанию класса"},
        {CMD_GO_ROOM,   "Помещение;Переход к расписанию помещения"},
    };
    
    private CommandDictionary(){
        for (String[] s:SRC){
            put(s[0], s[1]);
        }
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
