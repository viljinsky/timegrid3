/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

/**
 *
 * @author вадик
 */
public interface IScheduleState{
    public static final String STATE_NEW   = "STATE_NEW";
    public static final String STATE_WORK  = "STATE_WORK";
    public static final String STATE_ERROR = "STATE_ERROR";
    public static final String STATE_READY = "STATE_READY";
    public static final String STATE_USED  = "STATE_USED";
}

class ScheuleState implements IScheduleState{
    public static String[] getStateList(){
        return new String[]{
            STATE_NEW,
            STATE_WORK,
            STATE_ERROR,
            STATE_READY,
            STATE_USED
        };
    }
      

    public static Integer getStateKode(String state){
        switch (state){
            case STATE_NEW:
                return 0;
            case STATE_WORK:
                return 1;
            case STATE_ERROR:
                return 2;
            case STATE_READY:
                return 3;
            case STATE_USED:
                return 4;
            default:
                return null;
        }
    };
    
    public static String getStateDescription(String state){
        switch (state){
            case STATE_NEW:
                return "Новое";
            case STATE_WORK:
                return "В работе";
            case STATE_ERROR:
                return "Ошибки в расписании";
            case STATE_READY:
                return "Готово";
            case STATE_USED:
                return "Действует";
            default:
                return "???";
        }
    }
}
