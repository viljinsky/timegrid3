package ru.viljinsky.forms;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import ru.viljinsky.sqlite.DataModule;
import ru.viljinsky.sqlite.Dataset;
import ru.viljinsky.sqlite.Recordset;
import ru.viljinsky.sqlite.Values;

/**
 *
 * @author вадик
 */

abstract class TreeElement{
    int id;
    String label;
    @Override
    public String toString(){
        return label;
    }

    public abstract Values getFilter();
    public abstract Set<Point> getAvalabelCells();
}
    

    class Depart extends TreeElement{
        public static final String sql = 
                "select day_id-1,bell_id-1 from shift_detail a inner join "+
                "depart b on a.shift_id=b.shift_id where b.id=%d;";
    
        public Depart(Values values) throws Exception{
            id = values.getInteger("id");
            label = values.getString("label");
        }

        @Override
        public Values getFilter() {
            Values result = new Values();
            result.put("depart_id", id);
            return result;
        }

        @Override
        public Set<Point> getAvalabelCells() {
            Set<Point> result = new HashSet<>();
            Object[] p;
            try{
                Recordset resordset = DataModule.getRecordet(String.format(sql, id));

                for (int i=0;i<resordset.size();i++){
                    p=resordset.get(i);
                    result.add(new Point((Integer)p[0],(Integer)p[1]));
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            return result;
        }
        
    }
    
    class Teacher extends TreeElement{
        public static final String sql = 
                "select day_id-1,bell_id-1 from shift_detail a inner join "+
                "teacher b on a.shift_id=b.shift_id where b.id=%d;";
                
        public Teacher(Values values) throws Exception{
            id = values.getInteger("id");
            label = values.getString("teacher_name");
        }
        @Override
        public Values getFilter() {
            Values result = new Values();
            result.put("teacher_id", id);
            return result;
        }

        @Override
        public Set<Point> getAvalabelCells() {
            Set<Point> result = new HashSet<>();
            Point p;
            Object[] r;
            try{
                Recordset recordset = DataModule.getRecordet(String.format(sql,id));
                for (int i=0;i<recordset.size();i++){
                    r=recordset.get(i);
                    result.add(new Point((Integer)r[0],(Integer)r[1]));
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            
            return result;
        }
    }
    
    class Room extends TreeElement{
        private static final String sql =
                "select day_id-1,bell_id-1 from shift_detail a inner join "+
                "room b on a.shift_id=b.shift_id where b.id=%d;";
        public Room(Values values) throws Exception{
            id= values.getInteger("id");
            label=values.getString("room_name");
        }
        @Override
        public Values getFilter() {
            Values result = new Values();
            result.put("room_id", id);
            return result;
        }

    @Override
    public Set<Point> getAvalabelCells() {
        Set<Point> result = new HashSet<>();
            Point p;
            Object[] r;
            try{
                Recordset recordset = DataModule.getRecordet(String.format(sql,id));
                for (int i=0;i<recordset.size();i++){
                    r=recordset.get(i);
                    result.add(new Point((Integer)r[0],(Integer)r[1]));
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        return result;
    }
        
    }
        

abstract class AbstractScheduleTree extends JTree{
    DefaultMutableTreeNode departNodes , teacherNodes, roomNodes;
    TreeElement selectedElement = null;
    
    public AbstractScheduleTree(){
        clear();
        addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
                selectedElement = null;
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)getLastSelectedPathComponent();
                if (node!=null){
                    Object userObject = node.getUserObject();
                    if (userObject!=null){
                        if (userObject instanceof TreeElement){
                            selectedElement  = (TreeElement)userObject;
                        }
                    }
                }
                ElementChange(selectedElement);
            }
        });
        
    }
    
    public static final String STR_SCHEDULE = "Расписание";
    public static final String STR_DEPART = "Класс";
    public static final String STR_TEACHER = "Преподаватель";
    public static final String STR_ROOM = "Помещение";
            
    public void clear(){
        selectedElement = null;
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(STR_SCHEDULE);
        departNodes = new DefaultMutableTreeNode(STR_DEPART);
        teacherNodes = new DefaultMutableTreeNode(STR_TEACHER);
        roomNodes = new DefaultMutableTreeNode(STR_ROOM);
        root.add(departNodes);
        root.add(teacherNodes);
        root.add(roomNodes);
        setModel(new DefaultTreeModel(root));
    }
    
    public abstract void ElementChange(TreeElement element);
   
        
    
    
    public void open() throws Exception{
        String teacherSQL = "select id,last_name || ' ' || substr(first_name,1,1)||'.'|| substr(patronymic,1,1)||'.' as teacher_name,profile_id "
              + "from teacher order by teacher_name" ;
        
        DefaultMutableTreeNode node;
        Dataset dataset ;
        dataset = DataModule.getSQLDataset("select id,label from depart order by skill_id");
        dataset.open();
        for (int i=0;i<dataset.getRowCount();i++){
            node = new DefaultMutableTreeNode(new Depart(dataset.getValues(i)));
            departNodes.add(node);
        }
        
        Dataset teachers = DataModule.getSQLDataset(teacherSQL);
//        dataset = DataModule.getSQLDataset(teacherSQL);
        teachers.open();
        for (int i=0;i<teachers.getRowCount();i++){
            node = new DefaultMutableTreeNode(new Teacher(teachers.getValues(i)));
            teacherNodes.add(node);
        }
        
        dataset = DataModule.getSQLDataset("select profile_name,id \n"
                + "from profile where exists(select * from teacher where profile_id=profile.id)");
        dataset.open();
        
//        Dataset dataset2 = DataModule.getSQLDataset(teacherSQL);
        Map<String,Object> map = new HashMap();
        
        for (int i=0;i<dataset.size();i++){
            Values v = dataset.getValues(i);
            node = new DefaultMutableTreeNode(v.getString("profile_name"));
            teacherNodes.add(node);
            map.put("profile_id", v.getInteger("id"));
            teachers.open(map);
            for (int j=0;j<teachers.size();j++){
                node.add(new DefaultMutableTreeNode( new Teacher(teachers.getValues(j))));
            }
        }
        
        dataset = DataModule.getSQLDataset("select id,room_name from room order by room_name");
        dataset.open();
        for (int i=0;i<dataset.getRowCount();i++){
            node = new DefaultMutableTreeNode(new Room(dataset.getValues(i)));
            roomNodes.add(node);
        }
        
    }
}

