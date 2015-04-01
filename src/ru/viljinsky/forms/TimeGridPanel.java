/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import ru.viljinsky.DBComboBox;
import ru.viljinsky.DataModule;
import ru.viljinsky.Dataset;
import ru.viljinsky.Grid;
import ru.viljinsky.timegrid.*;

/**
 *
 * @author вадик
 */
public class TimeGridPanel extends JPanel{
    DataModule dataModule = DataModule.getInstance();
    TG timeGrid = new TG();
    Grid grSchedule = new Grid();
    Grid grSubjectGroup = new Grid();
    FilterPanel filterPanel = new FilterPanel();
   
    class TG extends TimeGrid{

        @Override
        public void cellElementClick(CellElement ce) {
            if (ce instanceof SubjectGroup){
                SubjectGroup sg = (SubjectGroup)ce;
                System.out.println(sg);
                
                Map<String,Object> option = new HashMap<>();
                option.put("day_id", sg.day_no);
                option.put("bell_id", sg.bell_id);
                option.put("subject_id", sg.subject_id);
                option.put("group_id", sg.group_id);
                try{
                    if (grSchedule.locate(option)){
                        System.out.println("OK");
                    };
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void cellClick(int col, int row) {
            
        }
        
        
    }

    ///////////////////////////  FILTER ////////////////////////////////////////
    class FDBComboBox extends DBComboBox{
        String keyFieldName;
        public FDBComboBox(String label,String keyFieldName){
            super();
            this.label=label;
            this.keyFieldName=keyFieldName;
        }
    }
    
    class FilterPanel extends JPanel implements ActionListener{
        FDBComboBox comboDepart = new FDBComboBox("depart","depart_id");
        FDBComboBox comboTeacher = new FDBComboBox("teacher","teacher_id");
        FDBComboBox comboRoom = new FDBComboBox("room","room_id");
        FDBComboBox comboWeek = new FDBComboBox("week","week_id");
        FDBComboBox[] combos = {comboDepart,comboTeacher,comboRoom,comboWeek};
        
        
        public FilterPanel(){
            setLayout(new FlowLayout(FlowLayout.LEFT));
            for (DBComboBox combo:combos){
                add(new JLabel(combo.getLabel()));
                combo.addActionListener(this);
                add(combo);
            }
        }
        
        public void open() throws Exception{
            Map<String,Object> filter = new HashMap<>();
            Dataset dataset;
            
            dataset = dataModule.getDataset("depart");
            comboDepart.setDataset(dataset, "id", "label");
            if (!dataset.isEmpty()){
                filter.put(comboDepart.keyFieldName, dataset.getValues(0).get("id"));
            }
            
            dataset = dataModule.getDataset("teacher");
            comboTeacher.setDataset(dataset, "id","last_name");
            filter.put("teacher_id", null);
            
            dataset = dataModule.getDataset("room");
            comboRoom.setDataset(dataset, "id","name");
            filter.put("room_id",null);
            
            dataset = dataModule.getDataset("week");
            comboWeek.setDataset(dataset, "id","caption");                        
            filter.put("week_id", null);
            setFilter(filter);
        }

        public void setFilter(Map<String,Object> filter){
            for (FDBComboBox combo:combos){
                combo.setValue(filter.get(combo.keyFieldName));
            }
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            Map<String,Object> filter = new HashMap<>();
            for (FDBComboBox combo:combos){
                if (combo.getValue()!=null)
                    filter.put(combo.keyFieldName, combo.getValue());
            }
            try{
                grSubjectGroup.setFilter(filter);
                grSchedule.setFilter(filter);
            } catch (Exception ee){
                System.out.println(filter);
                ee.printStackTrace();
            }
        }
    }
    ////////////////////////////////  FILTER //////////////////////////////////
    
    class SubjectGroup extends CellElement{
        int day_no;
        int bell_id;
        Integer depart_id;
        Integer subject_id;
        Integer group_type_id;
        Integer group_id;
        Integer week_id;
                
        String subject_name;
        String room_no;
        String teacher_name;
//        int group_id;

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
            teacher_name= (values.get("teacher_id")==null?"?":(String)values.get("teacher"));
            room_no=(values.get("room_id")==null?"?": (String)values.get("room"));
            subject_name =(String)values.get("subject_name");
            
            depart_id = (Integer)values.get("depart_id");
            subject_id=(Integer)values.get("subject_id");
            group_id=(Integer)values.get("group_id");
            
            setCell(day_no-1, bell_id-1);
        }
        
        public String toString(){
            return "day_no:"+day_no+" bell_id:"+bell_id+" depart_id:"+ depart_id+" subject_id:"+subject_id+" group_id:"+group_id;
        }
    }
    
    
    public TimeGridPanel(){
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(800,600));
        add(filterPanel,BorderLayout.PAGE_START);
//        timeGrid = new TimeGrid();
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
        
        filterPanel.open();
        
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
        
        dataset = dataModule.getSQLDataset("select * from v_schedule order by day_id,bell_id,group_id");
        dataset.open();
        grSchedule.setDataset(dataset);
        
        repaint();
        
    }
    
    private static JFrame frame = null;
    
    public static void showFrame(JComponent owner) throws Exception{
        if (frame ==null){
        
            TimeGridPanel panel = new TimeGridPanel();
            frame = new JFrame("Сетка расписания");
            frame.setContentPane(panel);
            frame.pack();
            if (owner!=null){
                Dimension d = owner.getSize();
                Point p =owner.getLocationOnScreen();
                frame.setLocation(p.x+10, p.y+10);
            }
            panel.open();
        }    
        frame.setVisible(true);
        
    }
    
    public static void main(String[] args){
        TimeGridPanel panel = new TimeGridPanel();
        
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
