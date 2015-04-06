/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.awt.Container;

/**
 *
 * @author вадик
 */


public class FormBuilder {
    public static final int TEACHER=1;
    public static final int DEPART=2;
    public static final int CURRICULUM=3;
    public static final int SCHEDULE=4;
    public static final int REPORTS=5;
    
    public static Container createForm(int form){
        Container result = null;
        switch (form){
            case TEACHER:
                result = new TeacherPanel();
                break;
            case DEPART:
                result = new DepartPanel();
                break;
            case CURRICULUM:
                result = new CurriculumPanel();
                break;
        }
        return result;
    }
    
}
