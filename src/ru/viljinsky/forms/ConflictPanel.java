/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import ru.viljinsky.DataModule;
import ru.viljinsky.Dataset;
import ru.viljinsky.Grid;
import ru.viljinsky.Values;

/**
 *
 * @author вадик
 */
public class ConflictPanel extends JPanel{
    public static String script = "drop view if exists v_teacher_conflict;\n" +
"create view v_teacher_conflict as\n" +
"select day_id,bell_id,teacher_id,count(*) as count\n" +
"from schedule\n" +
"group by day_id,bell_id,teacher_id\n" +
"having count(*)>1;\n" +
"select * from v_teacher_conflict;";
    
    Grid grid1 = new Grid(){

        @Override
        public void gridSelectionChange() {
            String sql = "select b.day_id,b.bell_id,b.week_id,s.subject_name,g.depart_id,g.subject_id,g.group_id,b.teacher_id,b.room_id,a.count from v_teacher_conflict a \n"
                    + "inner join schedule b on a.day_id=b.day_id and a.bell_id=b.bell_id and a.teacher_id=b.teacher_id \n"
                    + "inner join subject s on s.id = b.subject_id\n"
                    + "inner join subject_group g on g.depart_id=b.depart_id and g.group_id=b.group_id and g.subject_id=b.subject_id\n"
                    + "where a.teacher_id=%d and b.day_id=%d and b.bell_id=%d";

            Values values = getValues();
            if (values!=null){
                try{
                    Dataset dataset = DataModule.getSQLDataset(String.format(sql,
                            values.getInteger("teacher_id"),
                            values.getInteger("day_id"),
                            values.getInteger("bell_id")));
                    dataset.open();
                    grid2.setDataset(dataset);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    
    };
    Grid grid2 = new Grid();
    
    public ConflictPanel(){
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(800,600));
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(new JScrollPane(grid1));
        splitPane.setBottomComponent(new JScrollPane(grid2));
        splitPane.setResizeWeight(0.5);
        add(splitPane);
                
    }
    
    public void open() throws Exception{
        String sql = "select b.day_id,b.bell_id,a.first_name,a.last_name,b.count,b.teacher_id from teacher a inner join v_teacher_conflict b on a.id=b.teacher_id";
        Dataset dataset = DataModule.getSQLDataset(sql);
        dataset.open();
        grid1.setDataset(dataset);
    }
    
    public static void main(String[] args){
        ConflictPanel panel = new ConflictPanel();
        
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.pack();
        frame.setVisible(true);
        try{
            DataModule.open();
            DataModule.execute(script);
            panel.open();
        } catch (Exception e){
            e.printStackTrace();
        }
        
    }
    
}
