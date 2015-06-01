/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.timegrid;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import ru.viljinsky.sqlite.Values;

/**
 *
 * @author вадик
 */

class GroupImages{
    public static final Image image1 = createImage("../images/check_box.png");
    public static final Image image2= createImage("../images/check_box_checked.png");
    
    public static Image createImage(String path){
        URL url = GroupImages.class.getResource(path);
        if (url!=null){
            try{
                BufferedImage image = ImageIO.read(url);
                return image;
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        return null;
    }
}


public class TimeTableGroup extends CellElement {
    
    public int day_no;
    public int bell_id;
    public Integer depart_id;
    public Integer subject_id;
    public Integer group_id;

    public Integer teacher_id;
    public Integer room_id;
    
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
    public Integer schedule_state_id;
    
    Rectangle checkState=null;
    Rectangle checkReady = null;
    
    public Boolean isRedy(){
        return ready;
    }
    
    public Boolean isUsed(){
        return schedule_state_id==4;
    }

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
        
        // ready
        if (schedule_state_id<4){
        checkReady = new Rectangle(b.x+b.width-12,b.y,12,12);
        if (ready)
            g.drawImage(GroupImages.image2, checkReady.x, checkReady.y, null);
        else
            g.drawImage(GroupImages.image1,  checkReady.x, checkReady.y, null);
        
        }
//        // checked
//        checkState = new Rectangle(b.x+b.width-24, b.y, 12, 12);
//        if (schedule_state_id==4)
//            g.drawImage(GroupImages.image2, checkState.x, checkState.y, null);
//        else
//            g.drawImage(GroupImages.image1, checkState.x, checkState.y, null);
    }

    public Values getValues(){
        Values result = new Values();
        result.put("depart_id",depart_id);
        result.put("group_id",group_id);
        result.put("subject_id",subject_id);
        result.put("teacher_id",teacher_id);
        result.put("room_id",room_id);
        result.put("group_type_id",group_type_id);
        result.put("day_id", day_no);
        result.put("bell_id", bell_id);
        
        return result;
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
            teacher_id = values.getInteger("teacher_id");
            room_id = values.getInteger("room_id");
            group_type_id=values.getInteger("group_type_id");
            schedule_state_id = values.getInteger("schedule_state_id");
            
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

    @Override
    public boolean hitTest(int x, int y) {
        if (!super.hitTest(x, y))
            return false;
        
        if (checkReady!=null && checkReady.contains(x, y)){
            readyClick(this);
            return false;
        }
        
        if (checkState!=null && checkState.contains(x, y)){
            checkClick(this);
            return false;
        }
        
        
        return true;
        
    }
    /**
     * Клик по чекбоксу
     * @param group
     */     
    public void checkClick(TimeTableGroup group){
        System.out.println("CheckRectangle CLICK!!");        
    }
    
    /**
     * Клик по чекбоксу Ready
     * @param group
     */
    public void readyClick(TimeTableGroup group){
        System.out.println("READY CLICK!!!!");
    }
    
    
}
