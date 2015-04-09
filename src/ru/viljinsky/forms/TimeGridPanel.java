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
import ru.viljinsky.IDataset;
import ru.viljinsky.timegrid.*;

/**
 *
 * @author вадик
 */
public class TimeGridPanel extends JPanel{
    DataModule dataModule = DataModule.getInstance();
    TG timeGrid = new TG(7,10);
    Grid grSchedule = new Grid();
    Grid grSubjectGroup = new Grid();
    FilterPanel filterPanel = new FilterPanel();
   
    
    class TG extends TimeGrid{

        public TG(int col,int row){
            super(col,row);
        }
        
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
            filter.put("depart_id", null);
            
            dataset = dataModule.getDataset("teacher");
            comboTeacher.setDataset(dataset, "id","last_name");
            filter.put("teacher_id", null);
            
            dataset = dataModule.getDataset("room");
            comboRoom.setDataset(dataset, "id","room_name");
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
        
        public Map<String,Object> getFilter(){
            Map<String,Object> filter = new HashMap<>();
            for (FDBComboBox combo:combos){
                if (combo.getValue()!=null)
                    filter.put(combo.keyFieldName, combo.getValue());
            }
            return filter;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            try{
                fillSchedulePanel();
            } catch (Exception ee){
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
            y+=h;
            g.drawString("Гр."+(group_id==null?"?":group_id.toString()),x,y);
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
        timeGrid.setColCount(7);
        timeGrid.setRowCount(14);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(new JScrollPane(grSubjectGroup));
        JScrollPane scrollPane = new JScrollPane(timeGrid);
        scrollPane.setColumnHeaderView(timeGrid.getColumnHeader());
        scrollPane.setRowHeaderView(timeGrid.getRowHeader());
        splitPane.setRightComponent(scrollPane);
        splitPane.setDividerLocation(200);
        
        JSplitPane splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane1.setTopComponent(splitPane);
        splitPane1.setBottomComponent(new JScrollPane(grSchedule));
        splitPane1.setResizeWeight(0.7);
        add(splitPane1);
    }
    
    
    private void fillSchedulePanel() throws Exception{
        Map<String,Object> filter = filterPanel.getFilter();
        grSubjectGroup.setFilter(filter);
        grSchedule.setFilter(filter);
        
        IDataset dataset;
        
        SubjectGroup sg;
        Map<String,Object> values;
        

        dataset = grSchedule.getDataset();
        
        timeGrid.clear();
        
        for (int i=0;i<dataset.getRowCount();i++){
            values=dataset.getValues(i);
            sg=new SubjectGroup(values);
            timeGrid.addElement(sg);
        }
        timeGrid.realign();
        
//        repaint();
    }
    
    public void open() throws Exception{
        String sql = 
                "select b.subject_name,"
                + "case a.group_type_id  when 0 then '' when 1 then a.group_id when 1 then group_id end as group_label,"
                + "a.hour_per_week,"
                + "a.placed, "
                + "a.depart_id,"
                + "a.group_id,"
                + "a.subject_id,"
                + "a.group_id,"
                + "a.hour_per_day "
                + " from v_subject_group_on_schedule a inner join subject b on a.subject_id=b.id";
        Dataset dataset;
        filterPanel.open();

        dataset = dataModule.getSQLDataset(sql);
        grSubjectGroup.setDataset(dataset);
        
        dataset = dataModule.getSQLDataset("select * from v_schedule order by day_id,bell_id,group_id");
        grSchedule.setDataset(dataset);
        
        Map<String,Object> filter = new HashMap<>();
        filter.put("depart_id",new Integer(1));
        filterPanel.setFilter(filter);
        fillSchedulePanel();
      
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
