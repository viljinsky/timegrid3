/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.timegrid;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author вадик
 */
public class Cells extends ArrayList<CellElement> {

    public CellElement addElement(int col, int row) {
        CellElement element = new CellElement();
        element.col = col;
        element.row = row;
        this.add(element);
        return element;
    }

    public int elementCount(int col, int row) {
        Integer result = 0;
        for (CellElement ce : this) {
            if (ce.col == col && ce.row == row) {
                result += 1;
            }
        }
        return result;
    }

    boolean isExists(int col, int row) {
        for (CellElement ce : this) {
            if (ce.col == col && ce.row == row) {
                return true;
            }
        }
        return false;
    }

    public Set<CellElement> getCells(int col, int row) {
        Set<CellElement> list = new HashSet<>();
        for (CellElement ce : this) {
            if (ce.col == col && ce.row == row) {
                list.add(ce);
            }
        }
        return list;
    }

    public Set<CellElement> getSelected() {
        Set<CellElement> set = new HashSet<>();
        for (CellElement ce : this) {
            if (ce.selected) {
                set.add(ce);
            }
        }
        return set;
    }

    void setSelected(CellElement[] list) {
        for (CellElement element : this) {
            element.selected = false;
        }
        for (CellElement ce : list) {
            ce.selected = true;
        }
    }
    
}
