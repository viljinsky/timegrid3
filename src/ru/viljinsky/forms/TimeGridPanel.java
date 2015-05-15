/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
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
import ru.viljinsky.sqlite.DBComboBox;
import ru.viljinsky.sqlite.DataModule;
import ru.viljinsky.sqlite.Dataset;
import ru.viljinsky.sqlite.Grid;
import ru.viljinsky.sqlite.Values;
import ru.viljinsky.timegrid.*;




public class TimeGridPanel extends JPanel{
    TimeTableGrid timeGrid = new TimeTableGrid(7,10){

        @Override
        public void afterDataChange(int dCol, int dRow) {
            Values values = grSchedule.getValues();
            try{
                grSchedule.requery();
                if (values!=null){
                    Values v = new Values();
                    v.put("day_id", values.getInteger("day_id")+dCol);
                    v.put("bell_id", values.getInteger("bell_id")+dRow);
                    v.put("depart_id", values.getInteger("depart_id"));
                    v.put("group_id", values.getInteger("group_id"));
                    v.put("subject_id", values.getInteger("subject_id"));

                    System.out.println(values);
                    grSchedule.locate(v);
                }
            }  catch (Exception e){
            }
        }

        @Override
        public void cellElementClick(CellElement ce) {
            if (ce instanceof TimeTableGroup){
                TimeTableGroup sg = (TimeTableGroup)ce;
                System.out.println(sg);
                
                Values option = new Values();
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

        
    };
    Grid grSchedule = new Grid();
    Grid grSubjectGroup = new GridSubjectGroup();
    FilterPanel filterPanel = new FilterPanel();
    
    class GridSubjectGroup extends Grid{

        @Override
        public void gridSelectionChange() {
            Values values = getValues();
            System.out.println(values);
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
            
            dataset = DataModule.getDataset("depart");
            comboDepart.setDataset(dataset, "id", "label");
            filter.put("depart_id", null);
            
            dataset = DataModule.getDataset("teacher");
            comboTeacher.setDataset(dataset, "id","last_name");
            filter.put("teacher_id", null);
            
            dataset = DataModule.getDataset("room");
            comboRoom.setDataset(dataset, "id","room_name");
            filter.put("room_id",null);
            
            dataset = DataModule.getDataset("week");
            comboWeek.setDataset(dataset, "id","caption");                        
            filter.put("week_id", null);
            setFilter(filter);
        }

        public void setFilter(Map<String,Object> filter){
            for (FDBComboBox combo:combos){
                combo.setValue(filter.get(combo.keyFieldName));
            }
        }
        
        public Values getFilter(){
            Values filter = new Values();
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
        Values filter = filterPanel.getFilter();
        timeGrid.SetFilter(filter);
        
        grSchedule.setDataset(timeGrid.getDataset());
        grSubjectGroup.setFilter(filter);
        
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

        dataset = DataModule.getSQLDataset(sql);
        grSubjectGroup.setDataset(dataset);
        
        Values filter = new Values();
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
