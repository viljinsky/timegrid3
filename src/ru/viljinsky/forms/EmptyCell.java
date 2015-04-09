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
public class EmptyCell implements Comparable {
    public int day_id;
    public int bell_id;

    public EmptyCell(Object[] rowset) {
        day_id = (Integer) rowset[0];
        bell_id = (Integer) rowset[1];
    }

    @Override
    public String toString() {
        return String.format("d:%d  b:%d", day_id, bell_id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EmptyCell) {
            EmptyCell ec = (EmptyCell) obj;
            return ec.bell_id == bell_id && ec.day_id == day_id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return day_id * 100 + bell_id;
    }

    @Override
    public int compareTo(Object o) {
        EmptyCell ec = (EmptyCell) o;
        if (bell_id < ec.bell_id) {
            return -1;
        }
        if (day_id < ec.day_id) {
            return -1;
        }
        if (ec.day_id == day_id && ec.bell_id == bell_id) {
            return 0;
        }
        return 1;
    }
    
}
