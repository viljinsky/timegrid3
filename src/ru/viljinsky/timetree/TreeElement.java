/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.timetree;

import java.awt.Point;
import java.util.Set;
import ru.viljinsky.sqlite.Values;

/**
 *
 * @author вадик
 */
public abstract class TreeElement {
    public int id;
    String label;

    @Override
    public String toString() {
        return label;
    }

    public abstract Values getFilter();

    public abstract Set<Point> getAvalabelCells();
    
    public String getElementType(){
        return "NONE";
    }
    
}
