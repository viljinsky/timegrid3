/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.timetree;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;
import ru.viljinsky.forms.DataTask;
import ru.viljinsky.sqlite.Values;

/**
 *
 * @author вадик
 */
public class Teacher extends TreeElement {

    public Teacher(Values values) throws Exception {
        id = values.getInteger("id");
        label = values.getString("teacher_name");
    }

    @Override
    public Values getFilter() {
        Values result = new Values();
        result.put("teacher_id", id);
        return result;
    }

    @Override
    public Set<Point> getAvalabelCells() {
        Set<Point> result = new HashSet<>();
        try {
            result=DataTask.getTeacherAvalableCells(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String getElementType() {
        return "TEACHER";
    }
    
    
}
