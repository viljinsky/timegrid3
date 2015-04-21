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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import ru.viljinsky.CommandMngr;
import ru.viljinsky.DataModule;
import ru.viljinsky.Dataset;
import ru.viljinsky.Grid;
import ru.viljinsky.IDataset;
import ru.viljinsky.Recordset;
import ru.viljinsky.Values;
import ru.viljinsky.timegrid.CellElement;
import ru.viljinsky.timegrid.TimeTableGrid;
import ru.viljinsky.timegrid.TimeTableGroup;

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
            label = values.getString("last_name");
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

class ScheduleTree extends JTree{
    DefaultMutableTreeNode departNodes , teacherNodes, roomNodes;
    TreeElement selectedElement = null;
    
    public ScheduleTree(){
        clear();
        addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)getLastSelectedPathComponent();
                if (node!=null){
                    Object userObject = node.getUserObject();
                    if (userObject!=null){
                        if (userObject instanceof TreeElement){
                            selectedElement  = (TreeElement)userObject;
                            ElementChange();
                        }
                    }
                }
            }
        });
        
    }
    
    public void clear(){
        selectedElement = null;
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Расписания");
        departNodes = new DefaultMutableTreeNode("Классы");
        teacherNodes = new DefaultMutableTreeNode("Преподаватели");
        roomNodes = new DefaultMutableTreeNode("Помещения");
        root.add(departNodes);
        root.add(teacherNodes);
        root.add(roomNodes);
        setModel(new DefaultTreeModel(root));
    }
    
    public void ElementChange(){
        
    }
    
    public void open() throws Exception{
        
        DefaultMutableTreeNode node;
        Dataset dataset ;
        dataset = DataModule.getSQLDataset("select id,label from depart order by skill_id");
        dataset.open();
        for (int i=0;i<dataset.getRowCount();i++){
            node = new DefaultMutableTreeNode(new Depart(dataset.getValues(i)));
            departNodes.add(node);
        }
        
        dataset = DataModule.getSQLDataset("select id,last_name,first_name,patronymic from teacher order by last_name,first_name,patronymic");
        dataset.open();
        for (int i=0;i<dataset.getRowCount();i++){
            node = new DefaultMutableTreeNode(new Teacher(dataset.getValues(i)));
            teacherNodes.add(node);
        }
        
        dataset = DataModule.getSQLDataset("select id,room_name from room order by room_name");
        dataset.open();
        for (int i=0;i<dataset.getRowCount();i++){
            node = new DefaultMutableTreeNode(new Room(dataset.getValues(i)));
            roomNodes.add(node);
        }
        
    }
}

interface TimeTableCommand {
    public static final String TT_CLEAR     = "TT_CLEAR";
    public static final String TT_DELETE    = "TT_DELETE";
    public static final String TT_PLACE     = "TT_PLACE";
    public static final String TT_PLACE_ALL = "TT_PLACE_ALL";
    public static final String TT_FIX       = "TT_FIX";
    public static final String TT_UNFIX     = "TT_UNFIX";
    public static final String TT_REFRESH   = "TT_REFRESH";
}

public class TimeGridPanel2 extends JPanel  implements TimeTableCommand,IOpenedForm{
    ScheduleTree tree;
    TimeTableGrid grid;
    Grid unplacedGrid;
    
    CommandMngr manager = new CommandMngr() {

        @Override
        public void updateAction(Action a) {
            String command =getActionCommand(a);
            System.out.println(command);
            boolean b;
            switch (command){
                case TT_DELETE:
                    a.setEnabled(!grid.getSelectedElements().isEmpty());
                    break;
                case TT_PLACE:
                    try{
                    Values v=unplacedGrid.getValues();
                    b= (v!=null && (v.getInteger("unplaced")>0));
                    a.setEnabled(b);
                    } catch (Exception e){}
                    break;
            }
        }

        @Override
        public void doCommand(String command) {
            try{
                switch (command){
                    case TT_PLACE_ALL:
                        IDataset ds = unplacedGrid.getDataset();
                        Values values;
                        for (int i=0;i<ds.getRowCount();i++){
                            values=ds.getValues(i);
                            if (values.getInteger("unplaced")==0)
                                continue;
                            List<Point> L1 = TimeGridPanel2.this.getEmptyDepartCells(values);
                            if (L1.isEmpty())
                                continue;
                            grid.emptyCells=L1;
                            grid.insert(values);
                        }
                        unplacedGrid.requery();
                        break;
                    case TT_DELETE:
                        grid.delete();
                        unplacedGrid.requery();
                        break;
                    case TT_PLACE:
                        grid.insert(unplacedGrid.getValues());
                        unplacedGrid.requery();
                        break;
                    case TT_CLEAR:
                        grid.clear();
                        unplacedGrid.requery();
                        break;
                    case TT_FIX:
                        grid.fix();
                        break;
                    case TT_UNFIX:
                        grid.unfix();
                        break;
                    case TT_REFRESH:
                        tree.clear();
                        tree.open();
                        unplacedGrid.close();
                        break;
                }
            } catch (Exception e){
                JOptionPane.showMessageDialog(TimeGridPanel2.this, e.getMessage());
            }
        }
    };
    
    public TimeGridPanel2(){
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(800, 600));
        tree = new ScheduleTree(){

            @Override
            public void ElementChange() {
                treeElementChange(selectedElement);
            }
        };
        grid = new TimeTableGrid(){

            @Override
            public void cellElementClick(CellElement ce) {
                TimeTableGroup group = (TimeTableGroup)ce;
                Values values = group.getValues();
                System.out.println(values);
                try{
                    emptyCells=getEmptyDepartCells(values);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }


            @Override
            public void cellClick(int col, int row) {
                super.cellClick(col, row);
                manager.updateActionList();
                
                
            }

            @Override
            public void columnHeaderClick(int col) {
                super.columnHeaderClick(col);
                manager.updateActionList();
            }

            @Override
            public void rowHeaderClick(int row) {
                super.rowHeaderClick(row);
                manager.updateActionList();
            }
            
        };
        
        unplacedGrid = new Grid(){

            @Override
            public void gridSelectionChange() {
                Values values = unplacedGrid.getValues();
                if (values!=null) 
                    try{
                        grid.selectValues(values);
                        if (values.getInteger("unplaced")==0)
                            grid.emptyCells.clear();
                        else                                    
                            grid.emptyCells = getEmptyDepartCells(values);
                        grid.repaint();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                manager.updateActionList();
            }

        };
        JSplitPane splitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane2.setTopComponent(new JScrollPane(tree));
        splitPane2.setBottomComponent(new JScrollPane(unplacedGrid));
        splitPane2.setResizeWeight(0.5);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(splitPane2);
        JScrollPane scrollPane = new JScrollPane(grid);
        scrollPane.setColumnHeaderView(grid.getColumnHeader());
        scrollPane.setRowHeaderView(grid.getRowHeader());;
        splitPane.setRightComponent(scrollPane);
        splitPane.setDividerLocation(200);
        add(splitPane);
        
        
        manager.setCommandList(new String[]{
             TT_PLACE_ALL,
             TT_PLACE,
             TT_DELETE,
             TT_FIX,
             TT_UNFIX,
             TT_CLEAR,
             TT_REFRESH   
        });
        
        JPanel commands = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (Action a:manager.getActionList()){
            commands.add(new JButton(a));
        }
        add(commands,BorderLayout.PAGE_START);
        
    }
    /**
     * Поиск свободных ячеек преподователя
     * @param values  teacher_id
     * @return
     * @throws Exception 
     */
    public List<Point> getEmptyTeacherCells(Values values) throws Exception{
        Integer teacher_id;
        teacher_id=values.getInteger("teacher_id");
        if (teacher_id==null)
            return null;
        
        String sql = "select day_id,bell_id from shift_detail a inner join teacher b on a.shift_id=b.shift_id\n"
                + "where b.id=%teacher_id  and (select count(*) from schedule where day_id=a.day_id and bell_id=a.bell_id and teacher_id=b.id)=0";
        List<Point> result = new ArrayList<>();
        Recordset r = DataModule.getRecordet(sql.replace("%teacher_id", teacher_id.toString()));
        Object[] p;
        for (int i=0;i<r.size();i++){
            p=r.get(i);
            result.add(new Point((Integer)p[0]-1,(Integer)p[1]-1));
        }
        return result;
    }
    /**
     * Поиск свободных ячеек ккласса-группы
     * @param values depart_id,group_id,subject_id,group_type_id,teacher,room
     * @return
     * @throws Exception 
     */
    public List<Point> getEmptyDepartCells(Values values) throws Exception{
        // для всего класса
        String sql_depart = 
                "select a.day_id,a.bell_id from shift_detail a inner join depart d on a.shift_id=d.shift_id\n" +
                "  where d.id=%depart_id and not exists (select * from \n" +
                "  v_schedule_calc where depart_id=d.id and day_id=a.day_id and bell_id=a.bell_id)\n"+
                "order by a.bell_id,a.day_id;";
        // для групп
        String sql_group =
                "select a.day_id,a.bell_id \n" +
                "from shift_detail a inner join depart d\n" +
                "on a.shift_id=d.shift_id\n" +
                "where d.id=%depart_id  and ( " +
                "	select count(*) from v_schedule_calc \n" +
                "          where day_id=a.day_id and bell_id=a.bell_id and depart_id= d.id\n" +
                "		and ((group_type_id in (0,%group_type_a)) or ((group_type_id=%group_type_b) and  (group_id=%group_id))) \n" +
                
                ")=0 \n"+
                "order by a.bell_id,a.day_id;";
                
        Integer group_type_id = values.getInteger("group_type_id");
        Integer depart_id = values.getInteger("depart_id");
        Integer group_id= values.getInteger("group_id");
        Point p;
        Object[] r;
        List<Point> emptyCells = new ArrayList<>();
        
        String sql;
        switch (group_type_id){
            case 0:
                sql = sql_depart.replace("%depart_id",depart_id.toString());
                break;
            case 1:
                sql = sql_group.replace("%depart_id", 
                        depart_id.toString())
                        .replace("%group_id", group_id.toString())
                        .replace("%group_type_a", "2")
                        .replace("%group_type_b", "1")
                        ;
                break;
            case 2:     
                sql = sql_group.replace("%depart_id", 
                        depart_id.toString())
                        .replace("%group_id", group_id.toString())
                        .replace("%group_type_a", "1")
                        .replace("%group_type_b", "2")
                        ;
                System.out.println(sql);
                break;
            default:
                throw new Exception ("ONKNOW_GROUP_TYPE");
        }
        
        
        Recordset recordset = DataModule.getRecordet(sql);
        for (int i=0;i<recordset.size();i++){
            r = recordset.get(i);
            p= new Point((Integer)r[0]-1,(Integer)r[1]-1);
            System.out.println(p);
            emptyCells.add(p);
        }
        
        
        List<Point> result = new ArrayList<>(emptyCells);
        if (values.getInteger("teacher_id")!=null){
            List<Point> L = getEmptyTeacherCells(values);
            for (Point pp:emptyCells){
                if (!L.contains(pp)){
                    result.remove(pp);
                }
            }
        }
        
        return result;
        
    }
    
    public void treeElementChange(TreeElement element){
        System.out.println(element.getFilter());
        try{
            grid.avalableCells = new HashSet(element.getAvalabelCells());
//            System.out.println(element.getAvalabelCells());
            grid.SetFilter(element.getFilter());
            unplacedGrid.setFilter(element.getFilter());
            manager.updateActionList();
        } catch (Exception e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
    
    public void open() throws Exception{
        tree.open();
        Dataset dataset ;
        
        dataset = DataModule.getSQLDataset(
           "select b.label,a.group_label,c.subject_name,a.unplaced,a.depart_id,a.subject_id,a.group_id,"
                   + " a.default_teacher_id as teacher_id,a.default_room_id as room_id ,a.group_type_id "
                   + " from v_subject_group_on_schedule a "
                   + " inner join depart b on b.id = a.depart_id"
                   + " inner join subject c on c.id=a.subject_id"
        );
        unplacedGrid.setDataset(dataset);
        
        Map<Integer,String> columnHeaderCaprion = new HashMap<>();
        Recordset recordset = DataModule.getRecordet("select day_no,day_caption from day_list");
        Object[] r;
        for (int i=0;i<recordset.size();i++){
            r=recordset.get(i);
            columnHeaderCaprion.put(i,(String)r[1]);
        }
        grid.colCaption = columnHeaderCaprion;
        
        Map<Integer,String> rowHeaderCaption = new HashMap<>();
        recordset=DataModule.getRecordet("select bell_id,time_start || '\n' || time_end from bell_list");
        for (int i=0;i<recordset.size();i++){
            r=recordset.get(i);
            rowHeaderCaption.put(i, (String)r[1]);
        }
        
        grid.rowCaption = rowHeaderCaption;
        
    }
  
    public static void main(String[] args) throws Exception{
        DataModule.open();
        final TimeGridPanel2 panel = new TimeGridPanel2();
        JFrame frame = new JFrame("TimeTablePanel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.pack();
        frame.setVisible(true);
        
        panel.open();
        
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                try{
                    panel.close();
                } catch (Exception p){
                    p.printStackTrace();
                }
            }

        });
        
    }

    @Override
    public String getCaption() {
        return "TIMEGRIPPANEL2";
    }

    @Override
    public JComponent getPanel() {
        return this;
    }

    @Override
    public void close() throws Exception {
        System.out.println("close");
        tree.clear();
        unplacedGrid.getDataset().close();
        grid.close();
        
    }
    
}
