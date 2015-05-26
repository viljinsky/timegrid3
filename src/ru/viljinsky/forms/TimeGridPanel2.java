/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import ru.viljinsky.timetree.TreeElement;
import ru.viljinsky.timetree.AbstractScheduleTree;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import ru.viljinsky.sqlite.DataModule;
import ru.viljinsky.sqlite.Dataset;
import ru.viljinsky.sqlite.Grid;
import ru.viljinsky.sqlite.IDataset;
import ru.viljinsky.sqlite.Recordset;
import ru.viljinsky.sqlite.Values;
import ru.viljinsky.timegrid.CellElement;
import ru.viljinsky.timegrid.TimeTableGrid;
import ru.viljinsky.timegrid.TimeTableGroup;

/**
 *
 * @author вадик
 */

interface IScheduleState{
    public static final String STATE_NEW   = "STATE_NEW";
    public static final String STATE_WORK  = "STATE_WORK";
    public static final String STATE_ERROR = "STATE_ERROR";
    public static final String STATE_READY = "STATE_READY";
    public static final String STATE_USED  = "STATE_USED";
}

class ScheuleState implements IScheduleState{
    public static String[] getStateList(){
        return new String[]{
            STATE_NEW,
            STATE_WORK,
            STATE_ERROR,
            STATE_READY,
            STATE_USED
        };
    }
      

    public static Integer getStateKode(String state){
        switch (state){
            case STATE_NEW:
                return 0;
            case STATE_WORK:
                return 1;
            case STATE_ERROR:
                return 2;
            case STATE_READY:
                return 3;
            case STATE_USED:
                return 4;
            default:
                return null;
        }
    };
    
    public static String getStateDescription(String state){
        switch (state){
            case STATE_NEW:
                return "Новое";
            case STATE_WORK:
                return "В работе";
            case STATE_ERROR:
                return "Ошибки в расписании";
            case STATE_READY:
                return "Готово";
            case STATE_USED:
                return "Действует";
            default:
                return "???";
        }
    }
}


public class TimeGridPanel2 extends JPanel  implements IAppCommand,IOpenedForm,CommandListener{
    ScheduleTree tree = new ScheduleTree();
    ScheduleGrid grid = new ScheduleGrid();
    Grid unplacedGrid = new UnplacedGrid();
    JTextArea hintPane = new JTextArea();
    JTabbedPane tabbedPane = new JTabbedPane();
    Grid whoIsThere = new Grid();
    Grid whoComeHere =new Grid();
    
    Integer depart_id = null,
            teacher_id = null,
            room_id = null;
    
    
    class ScheduleTree extends AbstractScheduleTree{

        @Override
        public void ElementChange() {
            TreeElement element = getSelectedElement();
            Values filter = null;
            try{
                if (element!=null){
                    filter = element.getFilter();
                    depart_id = filter.containsKey("depart_id")?filter.getInteger("depart_id"):null;
                    teacher_id = filter.containsKey("teacher_id")?filter.getInteger("teacher_id"):null;
                    room_id = filter.containsKey("room_id")?filter.getInteger("room_id"):null;

                    grid.avalableCells = new HashSet(element.getAvalabelCells());
                    grid.SetFilter(filter);
                    unplacedGrid.setFilter(filter);
                } else {
                    grid.SetFilter(null);
                    depart_id  = null;
                    teacher_id = null;
                    room_id    = null;
                    unplacedGrid.close();
                }
            } catch (Exception e){
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
            TimeGridPanel2.this.manager.updateActionList();
        }
    }
    
    class ScheduleGrid extends TimeTableGrid{

        @Override
        public boolean allowStartDrag(CellElement element) {
            TimeTableGroup group = (TimeTableGroup)element;
            return (!group.isRedy() && !group.isUsed());
            
        }
        
        @Override
        public void cellElementClick(CellElement ce) {
            TimeTableGroup group = (TimeTableGroup)ce;
            Values values = group.getValues();
            try{
                emptyCells=getEmptyDepartCells(values);
            } catch (Exception e){
                e.printStackTrace();
            }
        }


        @Override
        public void cellClick(int col, int row) {
            super.cellClick(col, row);
            emptyCells.clear();
            try{    
            Dataset dataset = DataModule.getSQLDataset("select * from v_schedule where day_id="+(Integer)(col+1)+" and bell_id="+(Integer)(row+1));
            dataset.open();
            whoIsThere.setDataset(dataset);
            } catch (Exception e){
                e.printStackTrace();
            }

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

        @Override
        public void stopDrag(int col, int row) throws Exception {
            waitCursor(true);
            try{
                super.stopDrag(col, row);
            } catch (Exception e){
                Set<CellElement> set = getSelectedElements();
                TimeTableGroup group = null;
                for (CellElement ce:set){
                     group = (TimeTableGroup)ce;
                     analizCell(group, new Point(col+1,row+1));
                     break;
                }        
                JOptionPane.showMessageDialog(this,"Тут подсказка\n"+e.getMessage());                    
            } finally {
                waitCursor(false);
            }
        }
            
    }
    
    class UnplacedGrid extends Grid{

            @Override
            public void gridSelectionChange() {
                Values values = unplacedGrid.getValues();
                if (values!=null) 
                    try{
                        grid.selectValues(values);
                        if (values.getInteger("unplaced")==0)
                            grid.emptyCells.clear();
                        else                                    
                            grid.emptyCells = grid.getEmptyDepartCells(values);
                        grid.repaint();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                manager.updateActionList();
            }
    }
    
    
    public TimeGridPanel2(){
        JSplitPane  splitPane,
                    leftSplit,
                    rightSplit;
        
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(800, 600));
        
        // Пристёгиваются заголовки строк и колонок к сетке
        JScrollPane gridScroll = new JScrollPane(grid);
        gridScroll.setColumnHeaderView(grid.getColumnHeader());
        gridScroll.setRowHeaderView(grid.getRowHeader());
        
        
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        leftSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        
        splitPane.setLeftComponent(leftSplit);
        splitPane.setRightComponent(rightSplit);
        
        // левая сплит-панел распологает дерево и неразмещённые
        leftSplit.setTopComponent(new JScrollPane(tree));
        leftSplit.setBottomComponent(new JScrollPane(unplacedGrid));
        leftSplit.setResizeWeight(0.5);
        
        tabbedPane.addTab("Hint",new JScrollPane(hintPane));
        tabbedPane.addTab("Who is there",new JScrollPane(whoIsThere));
        tabbedPane.addTab("Who come here",new JScrollPane(whoComeHere));
        
        // правый сплит размещает сетку и подсказки
        rightSplit.setTopComponent(gridScroll);
        rightSplit.setBottomComponent(tabbedPane);//new JScrollPane(hintPane));
        rightSplit.setResizeWeight(0.9);
        
        splitPane.setDividerLocation(200);
        add(splitPane);
        
        
        manager.setCommands(SCHEDULE_COMMANDS);
        
        JPanel commands = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (Action a:manager.getActions()){
            commands.add(new JButton(a));
        }
        manager.addCommandListener(this);
        manager.updateActionList();
        add(commands,BorderLayout.PAGE_START);
        
    
    }

    public void waitCursor(boolean b){
    if (b)
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
    else
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    CommandMngr manager = new CommandMngr();
    
    @Override
    public void doCommand(String command){
        try{
            switch (command){
                case TT_PLACE_ALL:
                    
                    IDataset ds = unplacedGrid.getDataset();
                    Values values;
                    List<Point> L1;
                    int count,unplaced=0,placed=0;
                    waitCursor(true);
                    for (int i=0;i<ds.getRowCount();i++){
                        values=ds.getValues(i);
                        count = values.getInteger("unplaced");
                        unplaced +=count;
                        for (int n=0;n<count;n++){
                            L1 = grid.getEmptyDepartCells(values);
                            if (L1.isEmpty())
                                break;
                            grid.emptyCells=L1;
                            grid.insert(values);
                            placed+=1;
                        }
                    }
                    unplacedGrid.requery();
                    waitCursor(false);
                    JOptionPane.showMessageDialog(this, String.format("PLACEMENT_COMPLETED \n Расставлено %d из %d",placed,unplaced));
                    break;
                case TT_DELETE:
                    grid.delete();
                    unplacedGrid.requery();
                    break;
                case TT_PLACE:
                    waitCursor(true);
                    try{
                        grid.insert(unplacedGrid.getValues());
                        unplacedGrid.requery();
                    } finally {
                        waitCursor(false);
                    }
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
                case TT_SCH_STATE:
                    Dialogs.scheduleState(TimeGridPanel2.this,depart_id);
                    Recordset r = DataModule.getRecordet("select schedule_state_id from depart where id ="+depart_id);
                    for (CellElement ce :grid.getCells())
                        ((TimeTableGroup)ce).schedule_state_id=r.getInteger(0);
                    
                    break;
            }
        } catch (Exception e){
            JOptionPane.showMessageDialog(TimeGridPanel2.this, e.getMessage());
        }
        
    }
    
    protected void analizCell(TimeTableGroup group,Point pos){
        hintPane.setText("");
        hintPane.append(group.toString()+"\n");
        hintPane.append(pos.toString()+"\n");
        hintPane.append("----------------------\n");

        try{
            Dataset dataset = DataModule.getSQLDataset(String.format("select * from v_schedule where day_id=%d and bell_id=%d  and ((teacher_id=%d) or (room_id=%d))",pos.x,pos.y,group.teacher_id,group.room_id));
            dataset.open();
            Values values;
            for (int i=0;i<dataset.size();i++){
                values = dataset.getValues(i);
                hintPane.append(values.getString("depart_label")+" " +values.getString("group_label")+ " "+ values.getString("teacher")+ " "+values.getString("room")+"\n" );
            }
        } catch (Exception e){
            
        }
        
        System.out.println("----------------------");
        
    }
    
    
    @Override
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
        grid.open();
    }
  
    @Override
    public String getCaption() {
        return SCHEDULE;
    }

    @Override
    public JComponent getPanel() {
        return this;
    }

    @Override
    public void close() throws Exception {
        tree.clear();
        unplacedGrid.getDataset().close();
        grid.close();
        
    }

    @Override
    public void updateAction(Action a) {
        String command = (String)a.getValue(Action.ACTION_COMMAND_KEY);
        boolean b;
        switch (command){
            case TT_SCH_STATE:
                a.setEnabled(depart_id!=null);
                break;
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
    
}
