/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.timegrid;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 *
 * @author вадик
 */
public class CellElement {
    int col=1;
    int row=1;
    boolean moveble = false;
    protected boolean selected = false;
    Rectangle bound = null;
    public static Integer WIDTH = 100;
    public static Integer HEIGHT = 85;
    Color color = Color.MAGENTA;// ; new Color(255, 255, 200);

    public void draw(Graphics g, Rectangle b) {
        g.setColor(color);
        g.fillRect(b.x, b.y, b.width, b.height);
        if (selected) {
            g.setColor(Color.BLACK);
            g.drawRect(b.x, b.y, b.width, b.height);
        }
        if (!moveble){
            g.setColor(Color.YELLOW);
            g.fillRect(b.x+b.width-5, b.y, 5, 5);
        }
    }

    public boolean hitTest(int x, int y) {
        if (bound==null)
            return false;
        return bound.contains(x, y);
    }

    public void setCell(int col, int row) {
        this.col = col;
        this.row = row;
    }
    
    public void moveCell(int dCol,int dRow){
        if (moveble){
            this.col+=dCol;
            this.row+=dRow;
        }
    }
    
}
