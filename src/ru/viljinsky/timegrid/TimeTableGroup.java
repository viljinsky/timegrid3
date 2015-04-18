/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.timegrid;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import ru.viljinsky.Values;

/**
 *
 * @author вадик
 */
public class TimeTableGroup extends CellElement {
    public int day_no;
    public int bell_id;
    public Integer depart_id;
    public Integer subject_id;
    public Integer group_id;
    
    public Boolean checked = false;
    Integer group_type_id;
    Integer week_id;
    String subject_name;
    String room_no;
    String teacher_name;
    String group_label;
    String depart_label;
    Color color= Color.CYAN;
    Boolean ready;

    @Override
    public void draw(Graphics g, Rectangle b) {
        int h = g.getFontMetrics().getHeight();
        g.setColor(color);
        g.fillRect(b.x, b.y, b.width, b.height);
        if (selected) {
            g.setColor(Color.red);
            g.drawRect(b.x, b.y, b.width, b.height);
        }
        g.setColor(Color.BLACK);
        int x = b.x + 2;
        int y = b.y + h;
        g.drawString(depart_label, x, y);
        y += h;
        g.drawString(subject_name, x, y);
        y += h;
        g.drawString(teacher_name, x, y);
        y += h;
        g.drawString(room_no, x, y);
        y += h;
        g.drawString(group_label, x, y);
        if (week_id!=0){
            g.drawString((week_id==1?"I нед.":"II нед."), x+50, y);
        }
        
//        g.setColor(Color.WHITE);
//        g.fillRect(b.x+b.width-10, b.y, 10, 10);
        if (ready){
            g.setColor(color.YELLOW);
            g.fillRect(b.x+b.width-10, b.y, 10, 10);
            g.setColor(Color.BLACK);
            g.drawRect(b.x+b.width-10, b.y, 10, 10);
        }
        // checked
        g.setColor(Color.WHITE);
        g.fillRect(b.x+b.width-20, b.y, 10, 10);
        g.setColor(Color.BLACK);
        g.drawRect(b.x+b.width-20, b.y, 10, 10);
    }

    public TimeTableGroup(Values values) {
        try {
            day_no = values.getInteger("day_id");
            bell_id = values.getInteger("bell_id");
            teacher_name = (values.get("teacher_id") == null ? "?" : values.getString("teacher"));
            room_no = (values.get("room_id") == null ? "?" : values.getString("room"));
            subject_name = values.getString("subject_name");
            depart_id = values.getInteger("depart_id");
            subject_id = values.getInteger("subject_id");
            group_id = values.getInteger("group_id");
            group_label = values.getString("group_label");
            depart_label = values.getString("depart_label");
            ready = values.getBoolean("ready");
            week_id=values.getInteger("week_id");
//            moveble = !values.getBoolean("ready");
            
            
            String color_rgb = values.getString("color");
            if (color_rgb!=null){
                String[] rgb = color_rgb.split(" ");
                color = new Color(Integer.valueOf(rgb[0]),Integer.valueOf(rgb[1]),Integer.valueOf(rgb[2]));
            }
                
            
            setCell(day_no - 1, bell_id - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "day_no:" + day_no + " bell_id:" + bell_id + " depart_id:" + depart_id + " subject_id:" + subject_id + " group_id:" + group_id;
    }
    
}
