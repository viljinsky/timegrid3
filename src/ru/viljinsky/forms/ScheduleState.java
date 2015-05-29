/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.net.URL;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author вадик
 */
public class ScheduleState implements IScheduleState {
    private static ScheduleState instance = null;
    public Icon[] icons;

    public static String[] getStateList() {
        return new String[]{STATE_NEW, STATE_WORK, STATE_ERROR, STATE_READY, STATE_USED};
    }

    protected ScheduleState() {
        icons = new Icon[5];
        icons[0] = createIcon(STATE_NEW);
        icons[1] = createIcon(STATE_WORK);
        icons[2] = createIcon(STATE_ERROR);
        icons[3] = createIcon(STATE_READY);
        icons[4] = createIcon(STATE_USED);
    }

    protected Icon createIcon(String state) {
        String path = "";
        switch (state) {
            case STATE_NEW:
                path = "../images/state_new.png";
                break;
            case STATE_WORK:
                path = "../images/state_work.png";
                break;
            case STATE_ERROR:
                path = "../images/state_error.png";
                break;
            case STATE_READY:
                path = "../images/state_ready.png";
                break;
            case STATE_USED:
                path = "../images/state_used.png";
                break;
        }
        
        URL url = ScheduleState.class.getResource(path);
        if (url != null) {
            System.out.println(path);
            return new ImageIcon(url);
        }
        System.err.println("PATH_NOT_FOUND" + path);
        return null;
    }

    public static Icon getIcon(String state) {
        ScheduleState ss = getInstance();
        switch (state) {
            case STATE_NEW:
                return ss.icons[0];
            case STATE_WORK:
                return ss.icons[1];
            case STATE_READY:
                return ss.icons[2];
            case STATE_ERROR:
                return ss.icons[3];
            case STATE_USED:
                return ss.icons[4];
            default:
                return null;
        }
    }

    public static ScheduleState getInstance() {
        if (instance == null) {
            instance = new ScheduleState();
        }
        return instance;
    }

    public static Integer getStateKode(String state) {
        switch (state) {
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
    }

    public static String getStateDescription(String state) {
        switch (state) {
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
