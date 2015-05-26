/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.timegrid;

/**
 *
 * @author вадик
 */
public class Cell {
    int col;
    int row;

    public Cell(int col, int row) {
        this.col = col;
        this.row = row;
    }

    @Override
    public String toString() {
        return String.format("col %d row %d", col, row);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof Cell) {
            Cell o = (Cell) obj;
            return o.col == col && o.row == row;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + this.col;
        hash = 71 * hash + this.row;
        return hash;
    }
    
}
