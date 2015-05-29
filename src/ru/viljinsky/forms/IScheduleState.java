/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

/**
 *
 * @author вадик
 * 
 * Статусы расписания класссов
 */
public interface IScheduleState{
    /**Статус НОВОЕ РАСПИСАНИЕ*/
    public static final String STATE_NEW   = "STATE_NEW";
    /**Статус РАСПИСАНИЕ В РАБОТЕ. не все предметы расставлены*/
    public static final String STATE_WORK  = "STATE_WORK";
    /**Статус РАСПИСАНИЕ СОДЕРЖИТ ОШИБКИ все предметы расставлены есть ошибки*/
    public static final String STATE_ERROR = "STATE_ERROR";
    /**Статус РАСПИСАНИЕ ГОТОВО все предметы расставлены нет ошибок*/
    public static final String STATE_READY = "STATE_READY";
    /**Статус РАСПИСАНИЕ ДЕЙСТВУЕТ*/
    public static final String STATE_USED  = "STATE_USED";
}

