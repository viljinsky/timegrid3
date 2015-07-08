package ru.viljinsky.forms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
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
        TT_SCH_STATE, TT_FIX, TT_UNFIX, TT_DELETE,REFRESH_TREE};
    
    public static final String[] UNPLACED_CMD = {TT_PLACE, TT_PLACE_ALL, TT_CLEAR, TT_DELETE};
    // Дерево группы переподаватели помещения
    ScheduleTree scheduleTree = new ScheduleTree();
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
    // Лейбл расписания 
    JLabel lblSchedule = new JLabel("Расписание");
    
    class ScheduleHistory{
        Values v;
        Cell cell;
        String scheduleType = "unknow";
        Integer id;
        public ScheduleHistory(Values v,Cell cell){
            this.v=v;
            try{
            if (v.containsKey("teachet_id")){
                scheduleType="TEACHER";
                id=v.getInteger("teacher_id");
            } else if (v.containsKey("room_id")){
                scheduleType="ROOM";
                id=v.getInteger("room_id");
            } else if (v.containsKey("depart_id")){
                scheduleType="DEPART";
                id=v.getInteger("depart_id");
            }
            this.cell=cell;
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    
    class ScheduleHistoryList extends ArrayList<ScheduleHistory>{
        Integer index = -1;
        
        public void AddHistory(ScheduleHistory history){
            add(history);
            index = indexOf(history);
        }
        
        public void prior() throws Exception{
            if (index>0){
                goTo(get(index-1));
                index-=1;
            }
        }
        
        public void next() throws Exception{
            if (index<size()-1){
                goTo(get(index+1));
                index+=1;
            }
        }
        
        public void goTo(ScheduleHistory hist) throws Exception{
            try{
                switch (hist.scheduleType){
                    case "TEACHER":
                        scheduleGrid.setTeacherSchedule(hist.id, hist.cell);
                        break;
                    case "ROOM":
                        scheduleGrid.setRoomSchedule(hist.id, hist.cell);
                        break;
                    case "DEPART":
                        scheduleGrid.setDepartSchedule(hist.id,hist.cell);
                        break;
                }
            } catch (Exception e){
                throw new Exception("HIDTORY"+e.getMessage());
            }
        }
    }
    
    
    
    ScheduleHistoryList history = new ScheduleHistoryList();

    public SchedulePanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(800, 600));
        initComponents();
    }

    public void initComponents() {
        
        JSplitPane splitTop;
        JSplitPane splitPane;

        tabbedPane.addTab("Группы", new UnplacedPanel(unplacedGrid));
        tabbedPane.addTab("Кто сдесь", new WhoIsTherePanel(whoIsThereGrid));
        tabbedPane.addTab("Кого сюда", new JScrollPane(whoInvateGrid));
        JScrollPane scrollPane = new JScrollPane(scheduleGrid);
        scrollPane.setColumnHeaderView(scheduleGrid.getColumnHeader());
        scrollPane.setRowHeaderView(scheduleGrid.getRowHeader());
        JPanel panel = new JPanel(new BorderLayout());
        
        splitTop = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitTop.setLeftComponent(new JScrollPane(scheduleTree));
        splitTop.setRightComponent(scrollPane);
        splitTop.setDividerLocation(200);
        
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(splitTop);
        splitPane.setBottomComponent(tabbedPane);
        splitPane.setResizeWeight(0.7);
        
        panel.add(splitPane);
        panel.add(lblSchedule,BorderLayout.PAGE_START);
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
        scheduleTree.open();
        scheduleGrid.open();
        whoIsThereGrid.init();
        whoInvateGrid.init();
        unplacedGrid.init();
        commands.updateActionList();
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
        scheduleTree.clear();
        scheduleGrid.SetFilter(null);
        
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
        
        
        private void placeAll() throws Exception{
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
            if (placed<unplaced){
                JOptionPane.showMessageDialog(null, String.format("Размещено %d из %d ",placed,unplaced), "Внимание", JOptionPane.WARNING_MESSAGE);
            } else
                JOptionPane.showMessageDialog(null, String.format("Успешно расставлено %d (из %d)", placed, unplaced));
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
                        SchedulePanel.this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                        try{
                            placeAll();
                        } finally {
                            SchedulePanel.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        }
                        break;
                    case TT_CLEAR:
                        if (JOptionPane.showConfirmDialog(SchedulePanel.this,"Удалить все рассаленные элементы","Внимание",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                            scheduleGrid.clear();
                            unplacedGrid.requery();
                        }
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

    class UnplacedGridCellRenderer extends DefaultTableCellRenderer{

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            Grid g =(Grid)table;
            if (g.getColumnName(column).equals("unplaced")){
                Values v = g.getDataset().getValues(row);
                try{
                if (v.getInteger("unplaced")>0)
                    setBackground(Color.yellow);
                else 
                    setBackground(Color.white);
                } catch (Exception e){
                }
            } else {
                setBackground(Color.white);
            };
                
            return this;
        }
        
    }
    class UnplacedGrid extends Grid {
        UnplacedGridCellRenderer renderer = new UnplacedGridCellRenderer();

        @Override
        public TableCellRenderer getCellRenderer(int row, int column) {
//            return super.getCellRenderer(row, column); 
            return renderer;
        }

        Dataset dataset;
        
        String   sql = "select b.label,a.group_label,c.subject_name,\n" +
                    "t.last_name || ' ' || substr(t.first_name,1,1) ||'.'|| substr(t.patronymic,1,1)||'.' as teacher_name,r.room_name,\n" +
                    "a.unplaced,a.depart_id,a.subject_id,a.group_id,\n" +
                    " a.default_teacher_id as teacher_id,a.default_room_id as room_id ,a.group_type_id \n" +
                    " from v_subject_group_on_schedule a \n" +
                    " inner join depart b on b.id = a.depart_id \n" +
                    " inner join subject c on c.id=a.subject_id\n" +
                    " left join teacher t on a.default_teacher_id=t.id\n" +
                    " left join room r on a.default_room_id=r.id";

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

        private static final String SQL_INVATE_DEPART = 
            "select * from v_schedule a \n"+    
            "inner join subject_group b on a.depart_id=b.depart_id and a.group_id=b.group_id and a.subject_id=b.subject_id\n"+
            "inner join subject s on s.id=a.subject_id\n" + "left join teacher t on t.id=a.teacher_id\n" + 
            "left join room r on r.id=a.room_id\n" + 
            "where a.depart_id=%d \n" + 
            "and not exists (select * from schedule \n" + 
            "                where teacher_id=a.teacher_id and day_id=%d and bell_id=%d)\n" + 
            "and exists (select * from teacher inner join shift_detail m on teacher.shift_id=m.shift_id \n" +
            "            where teacher.id=a.teacher_id and m.day_id=%d and m.bell_id=%d)\n" + 
            "and not exists (select * from schedule \n" + 
            "            where room_id=a.room_id and day_id=%d and bell_id=%d)\n" +
            "and exists (select * from room inner join shift_detail m on m.shift_id=room.shift_id \n" + 
            "            where room.id=a.room_id and m.day_id=%d and m.bell_id=%d);";
        
        private static final String SQL_INVATE_TEACHER = 
            "select * \n"    +
            "   from v_schedule a\n" +
            "where a.teacher_id=%teacher_id% \n" +
            "and not exists(select * from schedule \n"+
            "where day_id=%day_id% and bell_id=%bell_id% and depart_id=a.depart_id and group_id=a.group_id);";

        private static final String SQL_INVATE_ROOM = 
            "select * \n"+
            "from v_schedule a \n"+
            "  where a.room_id=%room_id% and not exists (\n"+
            "  select * from schedule where depart_id=a.depart_id and group_id=a.group_id and day_id=%day_id% and bell_id=%bell_id% "+
            "\n) ";

        @Override
        public void gridSelectionChange() {
            Values v = getValues();
            if (v != null) {
                scheduleGrid.selectValues(v);
            }
        }

        public void setTimeLocation(Integer depart_id, int day_id, int bell_id) throws Exception {
            String sql = String.format(SQL_INVATE_DEPART, depart_id, day_id, bell_id, day_id, bell_id, day_id, bell_id, day_id, bell_id);
            Dataset dataset = DataModule.getSQLDataset(sql);
            setDataset(dataset);
            dataset.open();
        }
        
        public void setTeacherTimeLocation(Integer teacher_id,Integer day_id,Integer bell_id) throws Exception{
            String sql = SQL_INVATE_TEACHER.replace("%teacher_id%", teacher_id.toString())
                    .replace("%day_id%",day_id.toString())
                    .replace("%bell_id%", bell_id.toString());
            System.out.println(sql);
            Dataset dataset = DataModule.getSQLDataset(sql);
            setDataset(dataset);
            dataset.open();
        }
        
        public void setRoomTimeLocation(Integer room_id,Integer day_id,Integer bell_id) throws Exception{
            String sql = SQL_INVATE_ROOM.replace("%room_id%", room_id.toString())
                    .replace("%day_id%",day_id.toString())
                    .replace("%bell_id%", bell_id.toString());
            System.out.println(sql);
            Dataset dataset = DataModule.getSQLDataset(sql);
            setDataset(dataset);
            dataset.open();
            
        }

        private void init() throws Exception {
            setDataset(DataModule.getDataset("subject_group"));
        }
    }

    class WhoIsTherePanel extends JPanel implements CommandListener{
        public static final String CMD_GO_TEACHER = "CMD_GO_TEACHER";
        public static final String CMD_GO_DEPART  = "CMD_GO_DEPART";
        public static final String CMD_GO_ROOM    = "CMD_GO_ROOM";
        Grid grid;
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        Action[] actions= new Action[]{};
        CommandMngr commands = new CommandMngr();
        Integer teacher_id = null;
        Integer room_id=null;
        Integer depart_id=null;
        
        public WhoIsTherePanel(Grid grid){
            setLayout(new BorderLayout());
            this.grid=grid;
            grid.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                @Override
                public void valueChanged(ListSelectionEvent e) {
                    commands.updateActionList();
                }
                
            });
            add(controls,BorderLayout.PAGE_START);
            add(new JScrollPane(grid));
            
            commands.setCommands(new String[]{CMD_GO_TEACHER,CMD_GO_DEPART,CMD_GO_ROOM,"CMD_DELETE"});
        
            for (Action a:commands.actions){
                controls.add(new JButton(a));
            }
            commands.addCommandListener(this);
            commands.updateActionList();
        }

        @Override
        public void doCommand(String command) {
            Values v = grid.getValues();
            Cell cell = null;
            if (v!=null) 
                try{
                    cell = new Cell(v.getInteger("day_id"), v.getInteger("bell_id"));
                } catch (Exception e){
                    e.printStackTrace();
                }
            try{
                switch (command){
                    case CMD_GO_TEACHER:
                        scheduleGrid.setTeacherSchedule(grid.getIntegerValue("teacher_id"),cell);
                        break;
                    case CMD_GO_ROOM:
                        scheduleGrid.setRoomSchedule(grid.getIntegerValue("room_id"),cell);                        
                        break;
                    case CMD_GO_DEPART:
                        scheduleGrid.setDepartSchedule(grid.getIntegerValue("depart_id"),cell);
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
            switch(command){
                case CMD_GO_TEACHER:
                    action.setEnabled(grid.getSelectedRow()>=0);
                    break;
                case CMD_GO_ROOM:
                    action.setEnabled(grid.getSelectedRow()>=0);
                    break;
                case CMD_GO_DEPART:
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
        TreeElement element = scheduleTree.getSelectedElement();
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
        DefaultMutableTreeNode node = scheduleTree.departNodes;
        DefaultMutableTreeNode n;
        for (int i=0;i<node.getChildCount();i++){
            n = (DefaultMutableTreeNode)node.getChildAt(i);
            Depart d = (Depart)n.getUserObject();
            if (d.id==depart_id)
                d.schedule_status_id=schedule_state_id;
        }
        scheduleTree.repaint();
    }

    @Override
    public void doCommand(String command) {
        Cell cell = scheduleGrid.getSelectedCell();
        try {
            switch (command) {
                case CMD_PRIOR:
                    history.prior();
                    break;
                case CMD_NEXT:
                    history.next();
                    break;
                case CMD_GO_DEPART:
                    scheduleGrid.setDepartSchedule(depart_id, cell);
                    break;
                case CMD_GO_TEACHER:
                    scheduleGrid.setTeacherSchedule(teacher_id, cell);
                    break;
                case CMD_GO_ROOM:
                    scheduleGrid.setRoomSchedule(room_id, cell);
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
                case REFRESH_TREE:
                    scheduleTree.requery();
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
        TreeElement element = scheduleTree.getSelectedElement();
        Boolean b = (element != null) && (element instanceof Depart);
        switch (command) {
            case REFRESH_TREE:
                action.setEnabled(DataModule.isActive());
                break;
            case CMD_PRIOR:
                action.setEnabled(history.index>0);
                break;
            case CMD_NEXT:
                action.setEnabled(history.index<history.size()-1);
                break;
                
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
                            scheduleGrid.setTeacherSchedule(element.id,null);
                            break;
                        case "ROOM":
                            scheduleGrid.setRoomSchedule(element.id,null);
                            break;
                        case "DEPART":
                            scheduleGrid.setDepartSchedule(element.id,null);
                            break;
                    }
                    unplacedGrid.setFilter(element.getFilter());
                } else {
                    scheduleGrid.SetFilter(null);
                    unplacedGrid.close();
                }
                commands.updateActionList();
                unplacedGrid.gridSelectionChange();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    
    
    class ScheduleGrid extends TimeTableGrid {

        @Override
        public void onTimeGridChange(Values values, Cell cell) {
            super.onTimeGridChange(values, cell); 
            lblSchedule.setText(getScheduleTitle());
            history.AddHistory(new ScheduleHistory(values, cell));
        }
        
        

        @Override
        public boolean allowStartDrag(CellElement element) {
            TimeTableGroup group = (TimeTableGroup) element;
            return !(group.isRedy() || group.isUsed());
        }

        public static final String MSG_TEACHER_HAS_NOT_HOUR=
                "Преподаватель выходной";
        public static final String MSG_TEACHER_IS_BUSY=
                "Преподаватель занят (проводит заняти в другом классе)";
        public static final String MSG_ROOM_HAS_NOT_HOUR=
                "Помещение недоступно (в графике в эти часы нельзя проводить занятия)";
        public static final String MSG_ROOM_IS_BUSY=
                "Помещения занято (проходят занятия в другом класе)";
        public static final String MSG_DEPART_HAS_NOT_HOUR=
                "Время на соответсвует графику(смене) класса";
        public static final String MSG_DEPART_IS_BUSY=
                "В классе в это время проходят другие занятия";
        public static final String MSG_OTHER_PLACE_ERROR =
                "Непонятная причина!?";

        public static final String SQL_TEACHER_HAS_NOT_HOUR =
            "select count(*) \n"+
            "from teacher a inner join shift_detail b on a.shift_id=b.shift_id \n"+
            "where a.id=%d and b.day_id=%d and b.bell_id=%d";
        
        public static final String SQL_TEACHER_IS_BUSY = 
            "select count(*) from schedule \n"+
            "where teacher_id=%d and day_id=%d and bell_id=%d";
        
        public static final String SQL_ROOM_HAS_NOT_HOUR =
            "select count(*) from \n"+
            "room a inner join shift_detail b on a.shift_id=b.shift_id \n"+
            "where a.id=%d and b.day_id=%d and b.bell_id=%d";
        
        public static final String SQL_ROOM_IS_BUSY = 
            "select count(*) from schedule \n"+
            "where room_id=%d and day_id=%d and bell_id=%d";
            
        public static final String SQL_DEPART_HAS_NOT_HOUR = 
            "select count(*) \n"+
            "from depart a inner join shift_detail b on a.shift_id=b.shift_id \n"+
            "where a.id=%d and b.day_id=%d and b.bell_id=%d";
        
        private String getPlaceErrorMesage(TimeTableGroup group,int day_id,int bell_id){
            String result = MSG_OTHER_PLACE_ERROR;
            Recordset  r;
            try{
                r=DataModule.getRecordet(String.format(
                        SQL_TEACHER_HAS_NOT_HOUR,group.teacher_id,day_id,bell_id));
                if (r.getInteger(0)==0)
                    return MSG_TEACHER_HAS_NOT_HOUR;
                
                r=DataModule.getRecordet(String.format(
                        SQL_ROOM_HAS_NOT_HOUR,group.room_id,day_id,bell_id));
                if (r.getInteger(0)==0)
                    return MSG_ROOM_HAS_NOT_HOUR;
                
                r=DataModule.getRecordet(String.format(
                        SQL_DEPART_HAS_NOT_HOUR,group.depart_id,day_id,bell_id));
                if (r.getInteger(0)==0)
                    return MSG_DEPART_HAS_NOT_HOUR;
                
                r=DataModule.getRecordet(String.format(
                        SQL_TEACHER_IS_BUSY,group.teacher_id,day_id,bell_id));
                if (r.getInteger(0)>0)
                    return MSG_TEACHER_IS_BUSY;
                
                r=DataModule.getRecordet(String.format(
                        SQL_ROOM_IS_BUSY,group.room_id,day_id,bell_id));
                
                if (r.getInteger(0)>0)
                    return MSG_ROOM_IS_BUSY;
                
            } catch (Exception e){
                e.printStackTrace();
                
            }
            
            return result;
        }
        
        /**
         * Возможны варианты:
         * 1.Переподаватель не имеет часов (выходной)
         * 2.Преподаватель проводит занятия в другом классе
         * 3.Помещение не имеет часов (нет в графике)
         * 4.В помещении уже проводит занятия др класс
         * 5.Класс не имеет часо (нет в графика)
         * 6.В классе другие занятия
         * 
         * @param group
         * @param day_id
         * @param bell_id 
         */
        @Override
        public void onPlaceGroupError(TimeTableGroup group, Integer day_id, Integer bell_id) {
            String msg = getPlaceErrorMesage(group, day_id, bell_id);
            JOptionPane.showMessageDialog(this, String.format("Невозможно разместить группу из день %d время %d  в  день %d время %d\n%s", group.day_no, group.bell_id, day_id, bell_id,msg));
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
        TreeElement el = scheduleTree.getSelectedElement();
        if (el == null) {
            return;
        }
        Values v = el.getFilter();
            try {
                if (v.containsKey("depart_id"))
                    whoInvateGrid.setTimeLocation(v.getInteger("depart_id"), day_id, bell_id);
                else if (v.containsKey("teacher_id"))
                    whoInvateGrid.setTeacherTimeLocation(v.getInteger("teacher_id"), day_id, bell_id);
                else if (v.containsKey("room_id"))
                    whoInvateGrid.setRoomTimeLocation(v.getInteger("room_id"),day_id,bell_id);
                else
                    whoInvateGrid.close();
                
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
    
}
