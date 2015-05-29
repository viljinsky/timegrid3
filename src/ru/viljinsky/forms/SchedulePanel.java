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
import java.util.List;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import ru.viljinsky.sqlite.DataModule;
import ru.viljinsky.sqlite.Dataset;
import ru.viljinsky.sqlite.Grid;
import ru.viljinsky.sqlite.Recordset;
import ru.viljinsky.sqlite.Values;
import ru.viljinsky.timegrid.Cell;
import ru.viljinsky.timegrid.CellElement;
import ru.viljinsky.timegrid.TimeTableGrid;
import ru.viljinsky.timegrid.TimeTableGroup;
import ru.viljinsky.timetree.AbstractScheduleTree;
import ru.viljinsky.timetree.Depart;
import ru.viljinsky.timetree.TreeElement;

/**
 *
 * @author вадик
 */
public class SchedulePanel extends JPanel implements CommandListener, IAppCommand,IOpenedForm {
    public static final String[] SCHEDULE_NAV = {
        CMD_PRIOR, CMD_NEXT, CMD_GO_TEACHER, CMD_GO_DEPART, CMD_GO_ROOM,
        TT_SCH_STATE, TT_FIX, TT_UNFIX, TT_DELETE,"TREE_REFRESH"};
    
    public static final String[] UNPLACED_CMD = {TT_PLACE, TT_PLACE_ALL, TT_CLEAR, TT_DELETE};
    // Дерево группы переподаватели помещения
    ScheduleTree tree = new ScheduleTree();
    // Сетка расписания
    ScheduleGrid scheduleGrid = new ScheduleGrid();
    // Не размещённые предметы
    UnplacedGrid unplacedGrid = new UnplacedGrid();
    // Кто сдесь
    WhoIsThereGrid whoIsThereGrid = new WhoIsThereGrid();
    // Кого сюда
    WhoInvateGrid whoInvateGrid = new WhoInvateGrid();
    // Манагер событий
    CommandMngr commands = new CommandMngr();
    // Панель управление
    JPanel commandsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    // Вкладки таблиц
    JTabbedPane tabbedPane = new JTabbedPane();

    public SchedulePanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(800, 600));
        initComponents();
    }

    public void initComponents() {
        
        
        tabbedPane.addTab("Unplaced", new UnplacedPanel(unplacedGrid));
        tabbedPane.addTab("WhoIsThere", new WhoIsTherePanel(whoIsThereGrid));
        tabbedPane.addTab("WhoCome", new JScrollPane(whoInvateGrid));
        JScrollPane scrollPane = new JScrollPane(scheduleGrid);
        scrollPane.setColumnHeaderView(scheduleGrid.getColumnHeader());
        scrollPane.setRowHeaderView(scheduleGrid.getRowHeader());
        JPanel panel = new JPanel(new BorderLayout());
        JSplitPane splitTop = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitTop.setLeftComponent(new JScrollPane(tree));
        splitTop.setRightComponent(scrollPane);
        splitTop.setDividerLocation(200);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(splitTop);
        splitPane.setBottomComponent(tabbedPane);
        splitPane.setResizeWeight(0.7);
        panel.add(splitPane);
        add(panel);
        add(commandsPanel, BorderLayout.PAGE_START);
        commands.setCommands(SCHEDULE_NAV);
        commands.addCommandListener(this);
        for (Action a : commands.getActions()) {
            commandsPanel.add(new JButton(a));
        }
        commands.updateActionList();
    }

    @Override
    public void open() throws Exception {
        tree.open();
        scheduleGrid.open();
        whoIsThereGrid.init();
        whoInvateGrid.init();
        unplacedGrid.init();
    }

    @Override
    public String getCaption() {
        return "Расписание";
    }

    @Override
    public JComponent getPanel() {
        return this;
    }

    @Override
    public void close() throws Exception {
        
    }

    ///////////////////////  Внутренние классы  ////////////////////////////////
    class UnplacedPanel extends JPanel implements CommandListener, ListSelectionListener {

        Grid grid;
        JPanel commandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        CommandMngr commands = new CommandMngr();

        public UnplacedPanel(Grid grid) {
            super(new BorderLayout());
            this.grid = grid;
            grid.getSelectionModel().addListSelectionListener(this);
            add(commandPanel, BorderLayout.PAGE_START);
            add(new JScrollPane(grid));
            commands.setCommands(UNPLACED_CMD);
            for (Action a : commands.getActions()) {
                commandPanel.add(new JButton(a));
                grid.addExtAction(a);
            }
            commands.addCommandListener(this);
            commands.updateActionList();
        }

        @Override
        public void doCommand(String command) {
            try {
                switch (command) {
                    case TT_PLACE:
                        Values v = unplacedGrid.getValues();
                        scheduleGrid.emptyCells = scheduleGrid.getEmptyDepartCells(v);
                        scheduleGrid.insert(v);
                        unplacedGrid.requery();
                        break;
                    case TT_PLACE_ALL:
                        Dataset dataset = grid.getDataset();
                        Values values;
                        List<Point> L1;
                        Integer count,unplaced=0,placed=0;
                        for (int i = 0; i < dataset.size(); i++) {
                            values = dataset.getValues(i);
                            count = values.getInteger("unplaced");
                            unplaced += count;
                            for (int n = 0; n < count; n++) {
                                L1 = scheduleGrid.getEmptyDepartCells(values);
                                if (L1.isEmpty()) {
                                    break;
                                }
                                scheduleGrid.emptyCells = L1;
                                scheduleGrid.insert(values);
                                placed += 1;
                            }
                        }
                        unplacedGrid.requery();
                        JOptionPane.showMessageDialog(null, String.format("Успешно расставлено %d (из %d)", placed, unplaced));
                        break;
                    case TT_CLEAR:
                        scheduleGrid.clear();
                        unplacedGrid.requery();
                        break;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }

        @Override
        public void updateAction(Action action) {
            String command = (String) action.getValue(Action.ACTION_COMMAND_KEY);
            switch (command) {
                case TT_PLACE_ALL:
                    action.setEnabled(((UnplacedGrid) grid).getTotalUnplaced() > 0);
                    break;
                case TT_PLACE:
                    action.setEnabled(((UnplacedGrid) grid).getUnplaced() > 0);
                    break;
                case TT_DELETE:
                    action.setEnabled(grid.getSelectedRow() >= 0);
                    break;
                case TT_CLEAR:
                    action.setEnabled(!scheduleGrid.isEmpty());
                    break;
            }
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                commands.updateActionList();
            }
        }
    }

    class UnplacedGrid extends Grid {

        Dataset dataset;
        String sql = "select b.label,a.group_label,c.subject_name,a.unplaced,a.depart_id,a.subject_id,a.group_id," + " a.default_teacher_id as teacher_id,a.default_room_id as room_id ,a.group_type_id " + " from v_subject_group_on_schedule a " + " inner join depart b on b.id = a.depart_id" + " inner join subject c on c.id=a.subject_id";

        public void init() throws Exception {
            dataset = DataModule.getSQLDataset(sql);
            setDataset(dataset);
        }

        public int getTotalUnplaced() {
            Integer result = 0;
            if (dataset != null) {
                try {
                    for (int i = 0; i < dataset.size(); i++) {
                        result += dataset.getValues(i).getInteger("unplaced");
                    }
                } catch (Exception e) {
                    System.err.println("ERROR!!! getUnplacedAll()\n" + e.getMessage());
                }
            }
            return result;
        }

        public int getUnplaced() {
            Values values = getValues();
            if (values == null) {
                return 0;
            }
            try {
                return values.getInteger("unplaced");
            } catch (Exception e) {
                System.err.println("ERROR!!! getUnplaced()\n" + e.getMessage());
                return 0;
            }
        }
    }

    /**
     * Сетка включает те группы которые можно разместить в данное время в данном месте
     */
    class WhoInvateGrid extends Grid {

        private static final String sql = "select distinct b.depart_id,b.group_id,b.subject_id,a.teacher_id,a.room_id,t.last_name,s.subject_name,r.room_name from schedule a \n" + "inner join subject_group b on a.depart_id=b.depart_id and a.group_id=b.group_id and a.subject_id=b.subject_id\n" + "inner join subject s on s.id=a.subject_id\n" + "left join teacher t on t.id=a.teacher_id\n" + "left join room r on r.id=a.room_id\n" + "where a.depart_id=%d \n" + "and not exists (select * from schedule \n" + "                where teacher_id=a.teacher_id and day_id=%d and bell_id=%d)\n" + "and exists (select * from teacher inner join shift_detail m on teacher.shift_id=m.shift_id \n" + "            where teacher.id=a.teacher_id and m.day_id=%d and m.bell_id=%d)\n" + "and not exists (select * from schedule \n" + "            where room_id=a.room_id and day_id=%d and bell_id=%d)\n" + "and exists (select * from room inner join shift_detail m on m.shift_id=room.shift_id \n" + "            where room.id=a.room_id and m.day_id=%d and m.bell_id=%d);";

        @Override
        public void gridSelectionChange() {
            Values v = getValues();
            if (v != null) {
                scheduleGrid.selectValues(v);
            }
        }

        public void setTimeLocation(Integer depart_id, int day_id, int bell_id) throws Exception {
            String s = String.format(sql, depart_id, day_id, bell_id, day_id, bell_id, day_id, bell_id, day_id, bell_id);
            System.out.println(s);
            Dataset dataset = DataModule.getSQLDataset(s);
            setDataset(dataset);
            dataset.open();
        }

        private void init() throws Exception {
            setDataset(DataModule.getDataset("subject_group"));
        }
    }

    class WhoIsTherePanel extends JPanel implements CommandListener{
        Grid grid;
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        Action[] actions= new Action[]{};
        CommandMngr commands = new CommandMngr();
        Integer teacher_id = null;
        Integer room_id=null;
        Integer depart_id=null;
        
        public WhoIsTherePanel(Grid grid){
            setLayout(new BorderLayout());
            this.actions=actions;
            this.grid=grid;
            grid.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                @Override
                public void valueChanged(ListSelectionEvent e) {
                    commands.updateActionList();
                }
                
            });
            add(controls,BorderLayout.PAGE_START);
            add(new JScrollPane(grid));
            
            commands.setCommands(new String[]{"CMD_GO_TEACHER","CMD_GO_DEPART","CMD_GO_ROOM","CMD_DELETE"});
        
            for (Action a:commands.actions){
                controls.add(new JButton(a));
            }
            commands.addCommandListener(this);
            commands.updateActionList();
        }

        @Override
        public void doCommand(String command) {
            try{
                switch (command){
                    case "CMD_GO_TEACHER":
                        scheduleGrid.setTeacherSchedule(grid.getIntegerValue("depart_id"));
                        break;
                    case "CMD_GO_ROOM":
                        scheduleGrid.setRoomSchedule(grid.getIntegerValue("room_id"));                        
                        break;
                    case "CMD_GO_DEPART":
                        scheduleGrid.setDepartSchedule(grid.getIntegerValue("depart_id"));
                        break;
                    case "CMD_DELETE":
                        Values values = grid.getValues();
                        if (JOptionPane.showConfirmDialog(null, "Удалить запись","Внимание",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_NO_OPTION)
                        try{
                            DataModule.execute(String.format("delete from schedule where day_id=%d and bell_id=%d and depart_id=%d and group_id=%d and subject_id=%d",
                                    values.getInteger("day_id"),
                                    values.getInteger("bell_id"),
                                    values.getInteger("depart_id"),
                                    values.getInteger("group_id"),
                                    values.getInteger("subject_id")
                                    
                                    ));
                            DataModule.commit();
                        } catch (Exception e) {
                            DataModule.rollback();
                            throw new Exception("DELETE ERROR\n"+e.getMessage());
                        }
                        break;
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void updateAction(Action action) {
            String command = (String)action.getValue(Action.ACTION_COMMAND_KEY);
            System.out.println(command);
            switch(command){
                case "CMD_GO_TEACHER":
                    action.setEnabled(grid.getSelectedRow()>=0);
                    break;
                case "CMD_GO_ROOM":
                    action.setEnabled(grid.getSelectedRow()>=0);
                    break;
                case "CMD_GO_DEPART":
                    action.setEnabled(grid.getSelectedRow()>=0);
                    break;
                case "CMD_DELETE":
                    action.setEnabled(grid.getSelectedRow()>=0);
                    break;
                    
            }
        }
    }
    /**
     * Сетка включает группы которые находятся в данном месте в данное время
     */
    class WhoIsThereGrid extends Grid {

        public void init() throws Exception {
            Dataset dataset = DataModule.getDataset("v_schedule");
            setDataset(dataset);
        }

        @Override
        public void gridSelectionChange() {
            commands.updateActionList();
        }

        public void setTimeLocation(int day_id, int bell_id) throws Exception {
            Values v = new Values();
            v.put("day_id", day_id);
            v.put("bell_id", bell_id);
            setFilter(v);
        }
    }
    Integer depart_id = null;
    Integer teacher_id = null;
    Integer room_id = null;

    public void scheduleStatus() throws Exception {
        TreeElement element = tree.getSelectedElement();
        Integer depart_id = element.id;
        Dialogs.scheduleState(this, depart_id);
        Recordset r = DataModule.getRecordet("select schedule_state_id from depart where id =" + depart_id);
        Integer schedule_state_id= r.getInteger(0);
        for (CellElement ce : scheduleGrid.getCells()) {
            ((TimeTableGroup) ce).schedule_state_id = schedule_state_id;
        }
        setDepartScheduleState(depart_id,schedule_state_id);
        scheduleGrid.repaint();
    }
    
    public void setDepartScheduleState(Integer depart_id,Integer schedule_state_id){
        DefaultMutableTreeNode node = tree.departNodes;
        DefaultMutableTreeNode n;
        for (int i=0;i<node.getChildCount();i++){
            n = (DefaultMutableTreeNode)node.getChildAt(i);
            Depart d = (Depart)n.getUserObject();
            if (d.id==depart_id)
                d.schedule_status_id=schedule_state_id;
        }
        tree.repaint();
    }

    @Override
    public void doCommand(String command) {
        Cell cell = scheduleGrid.getSelectedCell();
        try {
            switch (command) {
                case CMD_GO_DEPART:
                    scheduleGrid.setDepartSchedule(depart_id, cell);
                    break;
                case CMD_GO_TEACHER:
                    scheduleGrid.setTeacherSchedule(teacher_id, cell);
                    break;
                case CMD_GO_ROOM:
                    scheduleGrid.setRoomSchedule(room_id, cell);
                    break;
                case CMD_NEXT:
                    break;
                case CMD_PRIOR:
                    break;
                case TT_SCH_STATE:
                    scheduleStatus();
                    break;
                case TT_DELETE:
                    scheduleGrid.delete();
                    unplacedGrid.requery();
                    break;
                case TT_FIX:
                    scheduleGrid.fix();
                    break;
                case TT_UNFIX:
                    scheduleGrid.unfix();
                    break;
                case "TREE_REFRESH":
                    tree.requery();
                    break;
            }
            System.out.println(scheduleGrid.getScheduleTitle());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    @Override
    public void updateAction(Action action) {
        String command = (String) action.getValue(Action.ACTION_COMMAND_KEY);
        TreeElement element = tree.getSelectedElement();
        Boolean b = (element != null) && (element instanceof Depart);
        switch (command) {
            case CMD_GO_TEACHER:
                action.setEnabled(teacher_id != null);
                break;
            case CMD_GO_ROOM:
                action.setEnabled(room_id != null);
                break;
            case CMD_GO_DEPART:
                action.setEnabled(depart_id != null);
                break;
            case TT_SCH_STATE:
                action.setEnabled(b);
                break;
            case TT_DELETE:
                action.setEnabled(scheduleGrid.getSelectedElements().size() > 0);
                break;
            case TT_FIX:
                action.setEnabled(scheduleGrid.getSelectedElements().size() > 0);
                break;
            case TT_UNFIX:
                action.setEnabled(scheduleGrid.getSelectedElements().size() > 0);
                break;
        }
    }

    class ScheduleTree extends AbstractScheduleTree {

        @Override
        public JPopupMenu getPopupMenu() {
            JPopupMenu result = super.getPopupMenu();
            for (Action a : commands.getActions()) {
                result.add(a);
            }
            return result;
        }

        @Override
        public void ElementChange() {
            TreeElement element = getSelectedElement();
            try {
                if (element != null) {
                    scheduleGrid.avalableCells = element.getAvalabelCells();
                    switch (element.getElementType()) {
                        case "TEACHER":
                            scheduleGrid.setTeacherSchedule(element.id);
                            break;
                        case "ROOM":
                            scheduleGrid.setRoomSchedule(element.id);
                            break;
                        case "DEPART":
                            scheduleGrid.setDepartSchedule(element.id);
                            break;
                    }
                    unplacedGrid.setFilter(element.getFilter());
                } else {
                    scheduleGrid.SetFilter(null);
                    unplacedGrid.close();
                }
                commands.updateActionList();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    class ScheduleGrid extends TimeTableGrid {

        @Override
        public boolean allowStartDrag(CellElement element) {
            TimeTableGroup group = (TimeTableGroup) element;
            return !(group.isRedy() || group.isUsed());
        }

        @Override
        public void onPlaceGroupError(TimeTableGroup group, Integer day_id, Integer bell_id) {
            JOptionPane.showMessageDialog(this, String.format("Невозможно разместить группу из день %d время %d  в  день %d время %d", group.day_no, group.bell_id, day_id, bell_id));
        }

        @Override
        public void cellElementClick(CellElement ce) {
            super.cellElementClick(ce);
            TimeTableGroup group = (TimeTableGroup) ce;
            try {
                teacher_id = group.teacher_id;
                depart_id = group.depart_id;
                room_id = group.room_id;
                emptyCells = getEmptyDepartCells(group.getValues());
                Values v = new Values();
                v.put("depart_id", group.depart_id);
                v.put("group_id", group.group_id);
                v.put("subject_id", group.subject_id);
                unplacedGrid.locate(v);
            } catch (Exception e) {
                e.printStackTrace();
            }
            commands.updateActionList();
        }

        @Override
        public void cellClick(int col, int row) {
            super.cellClick(col, row);
            if (isEmpty(col, row)) {
                depart_id = null;
                room_id = null;
                teacher_id = null;
            }
            whoInvate();
            try {
                whoIsThereGrid.setTimeLocation(col + 1, row + 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            commands.updateActionList();
        }
    }

    public void whoInvate() {
        Cell cell = scheduleGrid.getSelectedCell();
        int day_id  = cell.getCol() + 1;
        int bell_id = cell.getRow() + 1;
        TreeElement el = tree.getSelectedElement();
        if (el == null) {
            return;
        }
        Values v = el.getFilter();
        try {
            if (v.getInteger("depart_id") == null) {
                whoInvateGrid.close();
            } else {
                whoInvateGrid.setTimeLocation(v.getInteger("depart_id"), day_id, bell_id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
