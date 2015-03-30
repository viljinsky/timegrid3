/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import ru.viljinsky.timegrid.*;

/**
 *
 * @author вадик
 */
public class TimeGridPanel extends JPanel{
    DataModule dataModule = DataModule.getInstance();
    TimeGrid timeGrid = new TimeGrid();
    Grid grSchedule = new Grid();
    Grid grSubjectGroup = new Grid();
    
    class SubjectGroup extends CellElement{
        int day_no;
        int bell_id;
        String subject_name;
        String room_no;
        String teacher_name;
        int group_id;

        @Override
        public void draw(Graphics g, Rectangle b) {
            int h= g.getFontMetrics().getHeight();
            
            
            g.setColor(Color.white);
            g.fillRect(b.x, b.y, b.width, b.height);
            
            if (selected){
                g.setColor(Color.red);
                g.drawRect(b.x, b.y, b.width, b.height);
            }
            
            
            g.setColor(Color.BLUE);
            
            int x = b.x+2;int y = b.y+h;
            g.drawString(subject_name, x,y);
            y+=h;
            g.drawString(teacher_name, x, y);
            y+=h;
            g.drawString(room_no, x, y);
        }
        
        
        
        public SubjectGroup(Map<String,Object> values){
            day_no=(Integer)values.get("day_id");
            bell_id = (Integer)values.get("bell_id");
            teacher_name= (values.get("teacher_id")==null?"?":(String)values.get("teacher_name"));
            room_no=(values.get("room_id")==null?"?": (String)values.get("room_no"));
            subject_name =(String)values.get("subject_name");
            setCell(day_no-1, bell_id);
        }
    }
    
    
    public TimeGridPanel(){
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(500,400));
        timeGrid = new TimeGrid();
        timeGrid.setColCount(7);
        timeGrid.setRowCount(14);
//        timeGrid.calcRowHeight();
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(new JScrollPane(grSubjectGroup));
        splitPane.setRightComponent(new JScrollPane(timeGrid));
        splitPane.setDividerLocation(200);
        
        JSplitPane splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane1.setTopComponent(splitPane);
        splitPane1.setBottomComponent(new JScrollPane(grSchedule));
        splitPane1.setResizeWeight(0.7);
        add(splitPane1);
    }
    
    public void open() throws Exception{
        Dataset dataset;
        SubjectGroup sg;
        Map<String,Object> values;
        
        dataset = dataModule.getSQLDataset("select * from v_schedule "
                + "  where depart_id=1;");
        dataset.open();
        for (int i=0;i<dataset.size();i++){
            values=dataset.getValues(i);
            sg=new SubjectGroup(values);
            timeGrid.addElement(sg);
        }
        
        dataset = dataModule.getDataset("v_subject_group_on_schedule");
        dataset.open();
        grSubjectGroup.setDataset(dataset);
        
        dataset = dataModule.getSQLDataset("select * from v_schedule where depart_id=1");
        dataset.open();
        grSchedule.setDataset(dataset);
        
        repaint();
        
    }
    
    public void createGUI(){
        
    }
    
    public static void main(String[] args){
        TimeGridPanel panel = new TimeGridPanel();
        panel.createGUI();
        
        JFrame frame = new JFrame();
        
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        try{
            DataModule.getInstance().open();
            panel.open();
        } catch (Exception e){
            e.printStackTrace();
        }
        
    }
    
}
