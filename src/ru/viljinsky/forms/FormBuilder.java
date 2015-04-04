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
    public static Container createForm(String formName){
        Container result = null;
        switch (formName){
            case "TEACHER":
                result = new TeacherPanel();
                break;
            case "DEPART":
                result = new DepartPanel();
                break;
            case "CURRICULUM":
                result = new CurriculumPanel();
                break;
        }
        return result;
    }
    
}
