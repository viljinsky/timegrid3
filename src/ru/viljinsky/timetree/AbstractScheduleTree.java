package ru.viljinsky.timetree;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import ru.viljinsky.sqlite.DataModule;
import ru.viljinsky.sqlite.Dataset;
import ru.viljinsky.sqlite.Values;

    

    
    
        

public abstract class AbstractScheduleTree extends JTree{
    
    public static final String STR_SCHEDULE = "Расписание";
    public static final String STR_DEPART   = "Класс";
    public static final String STR_TEACHER  = "Преподаватель";
    public static final String STR_ROOM     = "Помещение";
    
    DefaultMutableTreeNode departNodes , teacherNodes, roomNodes;
    TreeElement selectedElement = null;
    
    public AbstractScheduleTree(){
        clear();
        addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
                TreeElement oldElement = selectedElement;
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
                if (oldElement!=selectedElement)
                    ElementChange();
                
            }
        });
        
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                showPopup(e);
                
            }

            @Override
            public void mousePressed(MouseEvent e) {
                showPopup(e);
            }
            
            public void showPopup(MouseEvent e){
                if (e.isPopupTrigger()){
                    getPopupMenu().show(AbstractScheduleTree.this, e.getX(),e.getY());
                }
            }
        });
    }
    
    public JPopupMenu getPopupMenu(){
        JPopupMenu result = new JPopupMenu();
        result.add("cmd1");
        return result;
    }
    
    public TreeElement getSelectedElement(){
        return selectedElement;
    }
            
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
    
//    public abstract void ElementChange(TreeElement element);
    public abstract void ElementChange();
        
    
    
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

