/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import ru.viljinsky.DataModule;
import ru.viljinsky.Dataset;
import ru.viljinsky.Values;

/**
 *
 * @author вадик
 */
public class Report extends JPanel{
    
    public static final String SQL_DEPART_TIME_GRID =
            "select a.id as depart_id,a.label, c.day_caption,d.time_start,d.time_end ,\n" +
            "(select count(*) from schedule r \n"+
            "where r.day_id=c.day_no and r.bell_id=d.bell_id and r.depart_id=a.id) as count,\n" +
            "c.day_no,d.bell_id\n" +
            "\n" +
            "from depart a inner join shift_detail b on a.shift_id=b.shift_id\n" +
            "inner join day_list c  on c.day_no=b.day_id\n" +
            "inner join bell_list d on d.bell_id=b.bell_id\n" +
            "order by a.id,c.day_no,d.bell_id;";
    
    public static final String SQL_SCHEDULE_GROUP = 
            "select sh.day_id,sh.bell_id,sh.depart_id,sh.subject_id,sh.group_id,s.subject_name,\n" +
            " case \n" +
            "   when m.group_type_id=0 then '' \n" +
            "   when m.group_type_id=1 then \n" +
            "       case a.group_id  when 1 then 'М'\n" +
            "                        when 2 then 'Д'\n" +
            "                        else '?' \n"+
            "       end\n" +
            "   else 'гр.' || a.group_id end\n" +
            "   as group_label,\n" +
            " sh.week_id,\n" +
            " a.stream_id,\n" +
            " t.last_name as teacher_name,r.room_name\n" +
            " from subject_group a inner join depart d on a.depart_id=d.id \n"+
            "     inner join curriculum_detail m\n" +
            "         on m.skill_id=d.skill_id and m.curriculum_id=d.curriculum_id and m.subject_id=a.subject_id\n" +
            "     inner join subject s on a.subject_id=s.id \n" +
            "     inner join schedule sh on sh.depart_id=a.depart_id \n"+
            "         and sh.subject_id=a.subject_id and sh.group_id=a.group_id\n" +
            "     left join teacher t on t.id=sh.teacher_id\n" +
            "     left join room r on r.id=sh.room_id;";
            
    
    JTextArea text;
    public Report(){
//        super(new BorderLayout());
        setLayout(new BorderLayout());
        text = new JTextArea();
        
        add(new JScrollPane(text));
        setPreferredSize(new Dimension(800,600));
    }
    
    
    public void departChange(String label){
        text.append(label+"\n");
    }
    
    public void dayChange(String day_caption){
        text.append(day_caption+"\n");
    }
    
    public void open() throws Exception{
        Dataset dataset = DataModule.getSQLDataset(SQL_DEPART_TIME_GRID);
        Dataset schedule_group = DataModule.getSQLDataset(SQL_SCHEDULE_GROUP);
        
        dataset.open();
        Values values ;
        Integer depart_id = null;
        Integer day_no = null;
        Integer bell_id=null;
        Map<String,Object> filter = new HashMap<>();
        for (int i=0;i<dataset.size();i++){
            values = dataset.getValues(i);
            
            if (depart_id!=null && !depart_id.equals(values.getInteger("depart_id"))){
                depart_id= values.getInteger("depart_id");
                text.append("\n\n\n");
                departChange(values.getString("label"));
            } else if (depart_id==null){
                depart_id= values.getInteger("depart_id");
                departChange(values.getString("label"));
            }
            
            if (day_no!=null && !day_no.equals(values.getInteger("day_no"))){
                text.append("\n");
                dayChange(values.getString("day_caption"));
            }   else if (day_no==null){
                dayChange(values.getString("day_caption"));
            }
            
            day_no = values.getInteger("day_no");
            bell_id = values.getInteger("bell_id");
            
            // Строка времени
            text.append(values.getString("time_start")+" " +values.getString("time_end")+"\n");
            
            if (values.getInteger("count")==0){
                text.append("\t---окно---"+"\n");
            } else {
                filter.put("depart_id",depart_id);
                filter.put("day_id",day_no);
                filter.put("bell_id",bell_id);
                schedule_group.open(filter);
                for (int n=0;n<schedule_group.size();n++){
                    Values v= schedule_group.getValues(n);
                    // строка групп
                    text.append("\t"+v.getString("subject_name")+" "+v.getString("teacher_name")
                            +" "+v.getString("room_name")+" "+v.getString("group_label")+"\n");
                }
            }
        }
    }
    
//    public void open() throws Exception{
//        Dataset depart = DataModule.getDataset("depart");
//        Dataset day_list = DataModule.getDataset("day_list");
//        Dataset bell_list = DataModule.getDataset("bell_list");
//        Dataset schedule = DataModule.getSQLDataset("select a.*,b.subject_name from schedule a inner join subject b on a.subject_id=b.id");
//        depart.open();
//        day_list.open();
//        bell_list.open();
//        Values departValues;
//        Values day_values;
//        Values bell_values;
//        Values schedule_values;
//        Map<String,Object> filter=new HashMap<>();
//        for (int i=0;i<depart.size();i++){
//            departValues= depart.getValues(i);
//            text.append(departValues.getString("label")+"\n");
//            for (int j=0;j<day_list.size();j++){
//                day_values = day_list.getValues(j);
//                text.append(day_values.getString("day_caption")+"\n");
//                for (int k=0;k<bell_list.size();k++){
//                    bell_values=bell_list.getValues(k);
//                    text.append("\t"+bell_values.getString("time_start")+"-"+bell_values.getString("time_end")+"\n");
//                    
//                    filter.clear();
//                    filter.put("day_id", day_values.getObject("day_no"));
//                    filter.put("bell_id", bell_values.getObject("bell_id"));
//                    filter.put("depart_id", departValues.getObject("id"));
//                    
//                    schedule.open(filter);
//                    for (int n=0;n<schedule.size();n++){
//                        schedule_values=schedule.getValues(n);
////                        System.out.println(schedule_values.toString());
//                        text.append("\t\t"+schedule_values.getString("group_id")+"  "+schedule_values.getString("subject_id")+"  "+schedule_values.getString("subject_name")+ "\n");
//                    }
//                    
//                    
//                }
//                // day
//                text.append("\n");
//            }
//            // depart
//            text.append("\n\n\n");
//            
//        }
//    }
    
    public static void main(String[] args){
        JFrame frame = new JFrame("Report");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Report report = new Report();
        frame.setContentPane(report);
        frame.pack();
        frame.setVisible(true);
        try{
            DataModule.open();
            report.open();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
}
