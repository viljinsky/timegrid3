/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.Action;
import javax.swing.JButton;
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
import ru.viljinsky.Values;
import ru.viljinsky.timegrid.TimeTableGrid;

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
    }
    
    class Depart extends TreeElement{
    
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
        
    }
    
    class Teacher extends TreeElement{
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
    }
    
    class Room extends TreeElement{
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
        
    }

class ScheduleTree extends JTree{
    DefaultMutableTreeNode departNodes , teacherNodes, roomNodes;
    TreeElement selectedElement = null;
    
    public ScheduleTree(){
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Расписания");
        departNodes = new DefaultMutableTreeNode("Классы");
        teacherNodes = new DefaultMutableTreeNode("Преподаватели");
        roomNodes = new DefaultMutableTreeNode("Помещения");
        root.add(departNodes);
        root.add(teacherNodes);
        root.add(roomNodes);
        setModel(new DefaultTreeModel(root));
        addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)getLastSelectedPathComponent();
                Object userObject = node.getUserObject();
                if (userObject!=null){
                    if (userObject instanceof TreeElement){
                        selectedElement  = (TreeElement)userObject;
                        ElementChange();
                    }
                }
            }
        });
        
    }
    
    public void ElementChange(){
    }
    
    public void open() throws Exception{
        
        DefaultMutableTreeNode node;
        Dataset dataset ;
        dataset = DataModule.getSQLDataset("select id,label from depart");
        dataset.open();
        for (int i=0;i<dataset.getRowCount();i++){
            node = new DefaultMutableTreeNode(new Depart(dataset.getValues(i)));
            departNodes.add(node);
        }
        
        dataset = DataModule.getSQLDataset("select id,last_name from teacher");
        dataset.open();
        for (int i=0;i<dataset.getRowCount();i++){
            node = new DefaultMutableTreeNode(new Teacher(dataset.getValues(i)));
            teacherNodes.add(node);
        }
        
        dataset = DataModule.getSQLDataset("select id,room_name from room");
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
    
}

public class TimeGridPanel2 extends JPanel  implements TimeTableCommand{
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
            System.out.println(command);
            try{
                switch (command){
                    case TT_PLACE_ALL:
                        TreeElement element =tree.selectedElement;
                        if (element !=null){
                            if (element instanceof Depart){
                                Integer depart_id= element.id;
                                ScheduleBuilder.placeDepart(depart_id);
                                grid.reload();
                            }
                            unplacedGrid.requery();
                        }
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
             TT_PLACE_ALL+";Разместить всё",
             TT_PLACE+";Разместить",
             TT_DELETE+";Удалить",
             TT_FIX+";Болкировать",
             TT_UNFIX+";Отм.блок",
             TT_CLEAR+";Очистить"
        });
        
        JPanel commands = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (Action a:manager.getActionList()){
            commands.add(new JButton(a));
        }
        add(commands,BorderLayout.PAGE_START);
        
    }
    
    public void treeElementChange(TreeElement element){
        System.out.println(element.getFilter());
        try{
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
                   + " a.default_teacher_id as teacher_id,a.default_room_id as room_id "
                   + " from v_subject_group_on_schedule a "
                   + " inner join depart b on b.id = a.depart_id"
                   + " inner join subject c on c.id=a.subject_id"
        );
        unplacedGrid.setDataset(dataset);
        
    }
  
    public static void main(String[] args) throws Exception{
        DataModule.open();
        TimeGridPanel2 panel = new TimeGridPanel2();
        JFrame frame = new JFrame("TimeTablePanel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.pack();
        frame.setVisible(true);
        
        panel.open();
    }
    
}
