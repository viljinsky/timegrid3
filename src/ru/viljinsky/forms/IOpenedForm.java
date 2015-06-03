package ru.viljinsky.forms;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import ru.viljinsky.sqlite.DataModule;
import ru.viljinsky.sqlite.Dataset;
import ru.viljinsky.sqlite.Grid;
import ru.viljinsky.sqlite.IDataset;
import ru.viljinsky.sqlite.Values;

/**
 *
 * @author вадик
 */
public interface IOpenedForm {
    public static final String CURRICULUM   = "Учебный план";
    public static final String DEPART       = "Классы";
    public static final String TEACHER      = "Преподаватели";
    public static final String ROOM         = "Помещения";
    public static final String SCHEDULE     = "Расписание";
    public static final String REPORTS      = "Отчёты";
    
    public void open() throws Exception;
    public String getCaption();
    public JComponent getPanel();
    public void close() throws Exception;
}

abstract class DetailPanel extends JPanel{
    Grid grid;
    Dataset dataset;
    GridPanel gridPanel;
    public DetailPanel(){
        super(new BorderLayout());
        grid = new Grid();
        gridPanel = new GridPanel("title", grid);
        add(gridPanel);
    }
    
    public void addAction(Action action){
        JButton button = new JButton(action);
        gridPanel.titlePanel.add(button);
    }
    
    public abstract void reopen(Integer keyValue) throws Exception;
}


////////////////////////////    ROOM PANEL /////////////////////////////////////


class RoomPanel extends JPanel implements IOpenedForm,ISchedulePanel,IAppCommand,CommandListener{
    
    Integer shift_id   = null,
            profile_id = null,
            room_id    = null;
    
    MasterGrid grid = new MasterGrid();
    
    SelectRoomPanel selectPanel = new SelectRoomPanel();    
    ShiftRoomPanel shiftPanel = new ShiftRoomPanel();
    DetailPanel profilePanel = new ProfileRoomPanel();
    
    CommandMngr commands = new CommandMngr(ROOM_COMMANDS);
    
    public RoomPanel(){
        super(new BorderLayout());
        JTabbedPane tabs =new JTabbedPane();
        tabs.addTab(SUBJECT_GROUP_PANEL, selectPanel);
        tabs.addTab(PROFILE_PANEL,profilePanel);
        tabs.addTab(SHIFT_PANEL,shiftPanel);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        
        GridPanel gridPanel = new GridPanel("Помещения", grid);
        splitPane.setTopComponent(gridPanel);
        splitPane.setBottomComponent(tabs);
        splitPane.setResizeWeight(0.5);
                
        add(splitPane);
        setPreferredSize(new Dimension(800,600));

        commands.addCommandListener(this);
       
        gridPanel.addAction(commands.getAction(CREATE_ROOM));
        gridPanel.addAction(commands.getAction(EDIT_ROOM));
        gridPanel.addAction(commands.getAction(DELETE_ROOM));
        
        shiftPanel.addAction(commands.getAction(CREATE_SHIFT));
        shiftPanel.addAction(commands.getAction(EDIT_SHIFT));
        shiftPanel.addAction(commands.getAction(REMOVE_SHIFT));
        
        profilePanel.addAction(commands.getAction(CREATE_PROFILE));
        profilePanel.addAction(commands.getAction(EDIT_PROFILE));
        profilePanel.addAction(commands.getAction(REMOVE_PROFILE));
        
        commands.updateActionList();
    }
    
    @Override
    public void doCommand(String command){
        Values values;
        try{
            switch(command){

                case CREATE_ROOM:
                    room_id = Dialogs.createRoom(this);
                    if (room_id!=null){
                        values = new Values();
                        values.put("id",room_id);
                        grid.requery(values);
                    }
                    break;
                    
                case EDIT_ROOM:
                    if (Dialogs.editRoom(this, room_id))
                        grid.requery();
                    break;
                    
                case DELETE_ROOM:
                    if (Dialogs.deleteRoom(this, room_id))
                        grid.requery();
                    break;
                    
                case CREATE_PROFILE:
                    profile_id=Dialogs.createProfile(this, profile_id);
                    if (profile_id!=null){
                        try{
                            room_id=grid.getIntegerValue("id");
                            DataModule.execute("update room set profile_id="+profile_id+" where id="+room_id);
                            DataModule.commit();
                        } catch (Exception p){
                            DataModule.rollback();
                            throw new Exception("CREATE_PROFILE_ERROR\n"+p.getMessage());
                        }    
                        grid.requery();
                    }
                    break;
                    
                case EDIT_PROFILE:
                    if (Dialogs.editProfile(this, profile_id))
                        grid.requery();
                    break;
                    
                case REMOVE_PROFILE:
                    if (Dialogs.removeProfile(this, profile_id)==true)
                        grid.requery();
                    break;
                    
                case CREATE_SHIFT:
                    shift_id=Dialogs.createShift(this, shift_id);
                    if (shift_id!=null){
                        try{
                            DataModule.execute(String.format("update room set shift_id=%d where id=%d",shift_id,room_id));
                            DataModule.commit();
                        } catch (Exception e){
                            DataModule.rollback();
                            throw new Exception("CREATE_SHIFT_ERROR\n"+e.getMessage());
                        }
                        grid.requery();
                    }
                    break;
                    
                case EDIT_SHIFT:
//                    shift_id= grid.getIntegerValue("shift_id");
                    if (Dialogs.editShift(this, shift_id)){
//                        shiftPanel.grid.requery();
                    }
                    break;
                    
                case REMOVE_SHIFT:
                    if (Dialogs.removeShift(this,shift_id))
                        grid.requery();
                    break;
            }
        } catch (Exception e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    @Override
    public void updateAction(Action action) {
        String command = (String)action.getValue(Action.ACTION_COMMAND_KEY);
    
        switch (command){
            case CREATE_ROOM:
                action.setEnabled(DataModule.isActive());
                break;
            case (EDIT_ROOM):
                action.setEnabled(room_id!=null);
                break;
            case (DELETE_ROOM):
                action.setEnabled(room_id!=null);
                break;
                
            case (EDIT_PROFILE):
                action.setEnabled(room_id!=null && profile_id!=null);
                break;
            case (CREATE_PROFILE):
                action.setEnabled(room_id!=null);
                break;
                
            case (REMOVE_PROFILE):
                action.setEnabled(room_id!=null && profile_id!=null);
                break;
                        
            case (EDIT_SHIFT):
                action.setEnabled(room_id!=null && shift_id!=null);
                break;
                
            case (CREATE_SHIFT):
                action.setEnabled(room_id!=null);
                break;
                
            case (REMOVE_SHIFT):
                action.setEnabled(room_id!=null && shift_id!=null);
                break;
                
        }
    }
    
    
    //--------------------------------------------------------------------------
    
    class ShiftRoomPanel extends JPanel{
        DBShiftPanel shPanel = new DBShiftPanel();
        String sqlShift="select a.* from shift_detail a inner join room b on a.shift_id=b.shift_id where b.id=%room_id;";
        JPanel commands = new JPanel(new FlowLayout(FlowLayout.LEFT));

        public ShiftRoomPanel() {
            super(new BorderLayout());
            add(commands,BorderLayout.PAGE_START);
            add(shPanel);
        }
        
        public void reopen(Integer keyValue) throws Exception{
            Dataset dataset = DataModule.getSQLDataset(sqlShift.replace("%room_id",keyValue.toString()));
            dataset.open();
            shPanel.setDataset(dataset);
            shPanel.repaint();
        }
        
        public void addAction(Action action){
            commands.add(new JButton(action));
        }
    }
    
    class ProfileRoomPanel extends DetailPanel{
        String sqlProfile = "select * from v_room_profile where room_id=%room_id;";
        @Override
        public void reopen(Integer keyValue) throws Exception{
            dataset = DataModule.getSQLDataset(sqlProfile.replace("%room_id", keyValue.toString()));
            dataset.open();
            grid.setDataset(dataset);
        }
        
    }
    
    class MasterGrid extends Grid{

        @Override
        public void gridSelectionChange() {
            try{
                profile_id=getSelectedRow()>=0? getIntegerValue("profile_id"):null;
                shift_id=getSelectedRow()>=0? getIntegerValue("shift_id"): null;
                room_id = getSelectedRow()>=0? getIntegerValue("id"):null;
                if (room_id!=null){
                        selectPanel.setRoomId(room_id);
                        shiftPanel.reopen(room_id);
                        profilePanel.reopen(room_id);
                }
                
                commands.updateActionList();
            } catch (Exception e){
                e.printStackTrace();
            }
            
        }

        @Override
        public void requery() throws Exception {
            
            Integer room_id = null;
            if (getSelectedRow()>=0)
                    room_id = getIntegerValue("id");
            super.requery();
            if (room_id!=null){
                Map<String,Object> map = new HashMap<>();
                map.put("id", room_id);
                locate(map);
                gridSelectionChange();
            }
        }
        
        
        
    }
    
    class SelectRoomPanel extends SelectPanel{
        
        public void updateTotalHour(int hour) throws Exception{
            Values values = grid.getValues();
            Integer hour_per_week=hour+values.getInteger("hour_per_week",0);
            values.setValue("hour_per_week", hour_per_week);
            grid.setValues(values);
        }
        
        int room_id=-1;
        
        public static final String sqlSource = 
            "select distinct s.subject_name,d.label,v.group_label,\n"+
            "v.teacher,\n"+    
            "v.hour_per_week,v.pupil_count,v.depart_id,v.subject_id,v.group_id,v.stream_id,v.group_sequence_id "+
            " from room a inner join profile_item b "+
            "on a.profile_id=b.profile_id "+
            "inner join v_subject_group v on v.subject_id=b.subject_id "+
            "inner join depart d on d.id=v.depart_id "+
            "inner join subject s on s.id=v.subject_id "+
            "where v.default_room_id is null";
        
        
        public static final String sqlDest =
            "select s.subject_name,d.label,\n"+
            "a.teacher,\n"+    
            "a.hour_per_week,a.subject_id,a.depart_id,a.group_id\n"+
            " from v_subject_group a\n"+
            "inner join subject s on s.id=a.subject_id\n"+
            " inner join depart d on d.id=a.depart_id\n"+
            " where a.default_room_id=";

        
        public void setRoomId(int room_id) throws Exception{
            this.room_id=room_id;
            requery();
            commands.updateActionList();
        }
        
        @Override
        public void include() throws Exception {
            Integer hour = 0;
            Integer depart_id,subject_id,group_id;
            Values values;
            IDataset dataset = sourceGrid.getDataset();
            try{
                for (int row:sourceGrid.getSelectedRows()){
                    values=dataset.getValues(sourceGrid.convertRowIndexToModel(row));
                    hour+= values.getInteger("hour_per_week",0);
                    depart_id=(Integer)values.get("depart_id");
                    subject_id=(Integer)values.get("subject_id");
                    group_id=(Integer)values.get("group_id");
                    DataTask.includeGroupToRoom(depart_id, subject_id, group_id, room_id);
                }
                DataModule.commit();
            } catch (Exception e){
                DataModule.rollback();
                throw new Exception("INCLUDE_ERROR\n"+e.getMessage());
            }
            sourceGrid.removeSelectedRow();
            destanationGrid.requery();
            commands.updateActionList();
            updateTotalHour(hour);
//            requery();
        }

        @Override
        public void exclude() throws Exception {
            int hour = 0;
            Integer depart_id,subject_id,group_id;
            Values values;
            IDataset dataset = destanationGrid.getDataset();
            try{
                for (int row: destanationGrid.getSelectedRows()){
                    values=dataset.getValues(destanationGrid.convertRowIndexToModel(row));
                    hour-=values.getInteger("hour_per_week",0);
                    depart_id=values.getInteger("depart_id");
                    subject_id=values.getInteger("subject_id");
                    group_id=values.getInteger("group_id");
                    DataTask.excluderGroupFromRoom(depart_id, subject_id, group_id);
                }
                DataModule.commit();
            } catch (Exception e){
                DataModule.rollback();
                throw new Exception("EXCLUDE_ERROR\n"+e.getMessage());
            }
            
            updateTotalHour(hour);
            
//            requery();
        }

        @Override
        public void includeAll() throws Exception {
            int hour = 0;
            Integer depart_id,subject_id,group_id;
            Values values;
            IDataset dataset = sourceGrid.getDataset();
            try{
                for (int row=0;row<dataset.getRowCount();row++){
                    values=dataset.getValues(row);
                    hour+=values.getInteger("hour_per_week",0);
                    depart_id=values.getInteger("depart_id");
                    subject_id=values.getInteger("subject_id");
                    group_id=values.getInteger("group_id");
                    DataTask.includeGroupToRoom(depart_id, subject_id, group_id, room_id);
                }
                DataModule.commit();
            } catch (Exception e){
                DataModule.rollback();
                throw new Exception("INCLUDE_ERROR\n"+e.getMessage());
            }
            requery();
            updateTotalHour(hour);
        }

        @Override
        public void excludeAll() throws Exception {
            int hour = 0;
            Integer depart_id,subject_id,group_id;
            Values values;
            IDataset dataset = destanationGrid.getDataset();
            try{
                for (int row=0;row<dataset.getRowCount();row++){
                    values=dataset.getValues(row);
                    hour-= values.getInteger("hour_per_week",0);
                    depart_id=(Integer)values.get("depart_id");
                    subject_id=(Integer)values.get("subject_id");
                    group_id=(Integer)values.get("group_id");
                    DataTask.excluderGroupFromRoom(depart_id, subject_id, group_id);
                }
                DataModule.commit();
            } catch (Exception e){
                DataModule.rollback();
                throw new Exception("EXCLUDE_ERROR\n"+e.getMessage());
            }
            requery();
            updateTotalHour(hour);
        }

        @Override
        public void requery() throws Exception {
            Dataset dataset;
            if (chProfileOnly.isSelected())
                dataset = DataModule.getSQLDataset(sqlSource+" and a.id="+room_id);
            else
                dataset = DataModule.getSQLDataset(sqlSource);
                
            dataset.open();
            sourceGrid.setDataset(dataset);
            
            dataset = DataModule.getSQLDataset(sqlDest+room_id);
            dataset.open();
            destanationGrid.setDataset(dataset);
        }
    }
    
    @Override
    public void open() throws Exception {
        Dataset dataset=DataModule.getDataset("v_room");
        dataset.open();
        grid.setDataset(dataset);
        selectPanel.requery();
        commands.updateActionList();
    }
    
    @Override
    public void close() throws Exception {
        selectPanel.close();
        grid.setDataset(null);
    }
    

    @Override
    public String getCaption() {
        return ROOM;
    }

    @Override
    public JComponent getPanel() {
        return this;
    }
    
};

////////////////////////   DEPART PANEL ////////////////////////////////////////


class DepartPanel extends JPanel implements IOpenedForm,IAppCommand,CommandListener{

    Integer depart_id=null,
            skill_id=null,
            stream_id=null,
            subject_id=null,
            group_id=null,
            group_type_id=null;
    Boolean is_stream = false;
           
    
    Grid grid1 = new Grid(){

        @Override
        public void gridSelectionChange() {
            Map<String,Object> map = new HashMap<>();
            Values v = getValues();
            depart_id=null;
            skill_id=null;
            
            if (v!=null)
                try{
                    skill_id=v.getInteger("skill_id");
                    depart_id=v.getInteger("depart_id");
                    map.put("depart_id", depart_id);
                    System.out.println(map);
                    grid2.getDataset().open(map);
                    grid2.refresh();

                } catch(Exception e){
                    e.printStackTrace();
                }
            commands.updateActionList();
        }
    };
    Grid grid2 = new Grid(){

        @Override
        public void gridSelectionChange() {
            Values v = getValues();
            if (v==null){
                subject_id=null;
                group_id=null;
                stream_id=null;
                group_type_id=null;
                is_stream=false;
            } else {
                try{
                subject_id=v.getInteger("subject_id");
                group_id=v.getInteger("group_id");
                stream_id=v.getInteger("stream_id");
                group_type_id=v.getInteger("group_type_id");
                is_stream=v.getBoolean("is_stream");
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            commands.updateActionList();
        }
        
    };
    CommandMngr commands = new CommandMngr(DEPART_COMMANDS);



    public DepartPanel() {
        setLayout(new BorderLayout());
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        GridPanel topPanel = new GridPanel("Классы", grid1);
        GridPanel bottomPanel = new GridPanel("Группы класса", grid2);
        splitPane.setTopComponent(topPanel);
        splitPane.setBottomComponent(bottomPanel);
        splitPane.setResizeWeight(0.5);
        add(splitPane);
        
        commands.addCommandListener(this);
        commands.updateActionList();
  
        topPanel.addAction(commands.getAction(EDIT_DEPART));
        topPanel.addAction(commands.getAction(DELETE_DEPART));        
        topPanel.addAction(commands.getAction(REFRESH));
        topPanel.addAction(commands.getAction(TT_SCH_STATE));
        
        bottomPanel.addAction(commands.getAction(ADD_GROUP));
        bottomPanel.addAction(commands.getAction(EDIT_GROUP));
        bottomPanel.addAction(commands.getAction(DELETE_GROUP));
        
        bottomPanel.addAction(commands.getAction(ADD_STREAM));
        bottomPanel.addAction(commands.getAction(EDIT_STREAM));
        bottomPanel.addAction(commands.getAction(REMOVE_STREAM));
        
}
    
    @Override
    public void doCommand(String commad){
        try{
            switch(commad){
                case REFRESH:
                    grid1.requery();
                    break;
                    
                case EDIT_DEPART:
                    if (Dialogs.editDepart(this, depart_id))
                        grid1.requery();
                    break;
                    
                case DELETE_DEPART:
                    if (Dialogs.deleteDepart(this, depart_id))
                        grid1.requery();
                    break;
                    
                case TT_SCH_STATE:
                    if (Dialogs.scheduleState(this,depart_id))
                        grid1.requery();
                    break;
                    
                case FILL_GROUP:
                    DataTask.fillSubjectGroup2(depart_id);
                    grid2.requery();
                    break;
                    
                case CLEAR_GROUP:
                    DataTask.clearSubjectGroup(depart_id);
                    grid2.requery();
                    break;
                    
                case EDIT_SHIFT:
                    Integer shift_id= grid1.getIntegerValue("shift_id");
                    Dialogs.editShift(this, shift_id);
                    break;
                    
                case ADD_GROUP:
                    DataTask.addSubjectGroup(depart_id,subject_id);
                    grid2.requery();
                    break;
                    
                case EDIT_GROUP:
                    if (Dialogs.editSubjectGroup(this,depart_id,subject_id,group_id))
                        grid2.requery();
                    break;
                    
                case DELETE_GROUP:
                    DataTask.deleteSubjectGroup(depart_id,subject_id,group_id);
                    grid2.requery();
                    break;
                    
                case ADD_STREAM:
                    if (Dialogs.createStream(this, depart_id, subject_id, group_id))
                      grid2.requery();
                    break;
                    
                case REMOVE_STREAM:
                    DataTask.deleteStream(depart_id,subject_id);
                    grid2.requery();
                    break;
                    
                case EDIT_STREAM:
                    if (Dialogs.editStream(this, stream_id))
                        grid2.requery();
                    break;
                    
                default:
                    throw new Exception("UNSUPPOTED_COMMAND\n\""+commad+"\"");
            }
        } catch (Exception e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
    
    @Override
    public void updateAction(Action action) {
        String command = (String)action.getValue(Action.ACTION_COMMAND_KEY);
        switch (command){
            case REFRESH:
                action.setEnabled(DataModule.isActive());
                break;
            case EDIT_DEPART:
                action.setEnabled(depart_id!=null);
                break;
                
            case DELETE_DEPART:
                action.setEnabled(depart_id!=null);
                break;
                
            case ADD_GROUP:
                action.setEnabled(depart_id!=null && subject_id!=null && group_type_id>0);
                break;
                
            case EDIT_GROUP:
                action.setEnabled(depart_id!=null && group_id!=null);
                break;
                
            case DELETE_GROUP:
                action.setEnabled(depart_id!=null && group_id!=null && group_type_id>0);
                break;
                
            case ADD_STREAM:
                action.setEnabled(depart_id!=null && subject_id!=null && is_stream);
                break;
                
            case EDIT_STREAM:
                action.setEnabled(depart_id!=null && stream_id!=null && is_stream);
                break;
                
            case REMOVE_STREAM:
                action.setEnabled(depart_id!=null && stream_id!=null);
                break;
            case TT_SCH_STATE:
                action.setEnabled(depart_id!=null);
                break;
        }
    }

    @Override
    public String getCaption() {
        return DEPART;
    }

    @Override
    public JComponent getPanel() {
        return this;
    }

//    @Override
//    public void edit() {
//        doCommand(EDIT_DEPART);
//    }

//    @Override
//    public void append() {
////        doCommand(DEPART);
//    }

//    @Override
//    public void delete() {
//        doCommand(DELETE_DEPART);
//    }

    @Override
    public void open() throws Exception {
        Dataset dataset = DataModule.getDataset("v_depart");
        dataset.open();
        grid1.setDataset(dataset);
        
        dataset = DataModule.getDataset("v_subject_group");
        grid2.setDataset(dataset);
    }

    @Override
    public void close() throws Exception {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


}


////////////////////////////////  TEACHER PANEL ////////////////////////////////
interface ISchedulePanel {
    public static final String SUBJECT_GROUP_PANEL = "Группы";
    public static final String PROFILE_PANEL = "Специализация";
    public static final String SHIFT_PANEL = "График";

}

class TeacherPanel extends JPanel implements IOpenedForm,ISchedulePanel, IAppCommand,CommandListener {
    Integer shift_id=null,
            profile_id=null,
            newProfileId,newShiftId,
            teacher_id=null;
    
    MasterGrid grid = new MasterGrid();
    JTabbedPane tabs = new JTabbedPane();
    
    TeacherSelectPanel selctPanel = new TeacherSelectPanel();
    DetailPanel profilePanel = new ProfileTeacherPanel();
    ShiftTeacherPanel shiftPanel = new ShiftTeacherPanel();
    CommandMngr commands = new CommandMngr();
    
   
    @Override
    public void updateAction(Action action){
        String command = (String)action.getValue(Action.ACTION_COMMAND_KEY);
        switch (command){
            case CREATE_TEACHER:
                action.setEnabled(DataModule.isActive());
                break;
            case EDIT_TEACHER:
                action.setEnabled(teacher_id!=null);
                break;
            case DELETE_TEACHER:
                action.setEnabled(teacher_id!=null);
                break;
            case CREATE_PROFILE:
                action.setEnabled(teacher_id!=null && profile_id!=null);
                break;
            case EDIT_PROFILE:
                action.setEnabled(teacher_id!=null && profile_id!=null);
                break;
            case REMOVE_PROFILE:
                action.setEnabled(teacher_id!=null && profile_id!=null);
                break;
            case CREATE_SHIFT:
                action.setEnabled(teacher_id!=null && shift_id!=null);
                break;
            case EDIT_SHIFT:
                action.setEnabled(teacher_id!=null && shift_id!=null);
                break;
            case REMOVE_SHIFT:
                action.setEnabled(teacher_id!=null && shift_id!=null);
                break;
        }
    }
    
    @Override
    public void doCommand(String command){
        Values values;
        try{
            switch (command){
                
                case CREATE_TEACHER:
                    teacher_id = Dialogs.createTeacher(this);
                    if (teacher_id!=null){
                        values = new Values();
                        values.put("id", teacher_id);
                        grid.requery(values);
                    }
                    break;
                    
                case EDIT_TEACHER:
                    if (Dialogs.editTeacher(this, teacher_id)==true)
                        grid.requery();
                    break;
                    
                case DELETE_TEACHER:
                    if (Dialogs.deleteTeacher(this, teacher_id))
                        grid.requery();
                    break;
                    
                case CREATE_PROFILE:
                    newProfileId = Dialogs.createProfile(this, profile_id);
                    if (newProfileId!=null){
                        try{
                            DataModule.execute("update teacher set profile_id="+newProfileId+" where id="+grid.getIntegerValue("id"));
                            DataModule.commit();
                        } catch (Exception p){
                            DataModule.rollback();
                            throw new Exception("UPDATE_ERROR\n"+p.getMessage());
                        }
                    }
                    grid.requery();
                    break;
                    
                case EDIT_PROFILE:
                    Dialogs.editProfile(this,profile_id);
                    grid.requery();
                    break;
                    
                case REMOVE_PROFILE:
                    Dialogs.removeProfile(this, profile_id);
                    grid.requery();
                    break;
                 
                case CREATE_SHIFT:
                    newShiftId = Dialogs.createShift(this, shift_id);
                    if (newShiftId!=null){
                        try{
                            DataModule.execute("update teacher set shift_id="+newShiftId+" where id="+grid.getIntegerValue("id"));
                            DataModule.commit();
                        } catch (Exception p){
                            DataModule.rollback();
                            throw new Exception("CREATE_SHIFT_ERROR\n"+p.getMessage());
                        }
                    }
                    grid.requery();
                    break;
                    
                case EDIT_SHIFT:
                    Dialogs.editShift(this, shift_id);
                    grid.requery();
                    break;
                    
                case REMOVE_SHIFT:
                    if (Dialogs.removeShift(this,shift_id))
                        grid.requery();
                    break;
            }
        } catch (Exception e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    
    class ProfileTeacherPanel extends DetailPanel{
        String sqlTeacherProfilee = "select * from v_teacher_profile where teacher_id=%teacher_id";
        
        @Override
        public void reopen(Integer keyValue) throws Exception{
            dataset = DataModule.getSQLDataset(sqlTeacherProfilee.replace("%teacher_id",keyValue.toString()));
            dataset.open();
            grid.setDataset(dataset);
        }
    }
    
    class ShiftTeacherPanel extends JPanel{
        DBShiftPanel shPanel = new DBShiftPanel();
        String sqlTeacherShift = "select a.* from shift_detail a inner join teacher b on a.shift_id=b.shift_id where b.id=%teacher_id;";
        JPanel commandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        public ShiftTeacherPanel() {
            super(new BorderLayout());
            add(commandPanel,BorderLayout.PAGE_START);
            add(shPanel);
                    
        }
        
        public void reopen(Integer keyValue) throws Exception{
            Dataset dataset = DataModule.getSQLDataset(sqlTeacherShift.replace("%teacher_id", keyValue.toString()));
            dataset.open();
            shPanel.setDataset(dataset);
        }
        
        public void addAction(Action action){
            commandPanel.add(new JButton(action));
        }
        
    }

    @Override
    public void close() throws Exception {
        grid.setDataset(null);
        selctPanel.close();
    }

    class MasterGrid extends Grid {

        @Override
        public void gridSelectionChange() {
            
            try{
                teacher_id = getSelectedRow()>=0?getIntegerValue("id"):null;
                shift_id = getSelectedRow()>=0?getIntegerValue("shift_id"):null;
                profile_id = getSelectedRow()>=0?getIntegerValue("profile_id"):null;
                if (teacher_id!=null){
                    selctPanel.setTeacherId(teacher_id);
                    shiftPanel.reopen(teacher_id);
                    profilePanel.reopen(teacher_id);
                }
                commands.updateActionList();
            } catch (Exception e){
                e.printStackTrace();
            }
            
        }

        @Override
        public void requery() throws Exception {
            Integer row = getSelectedRow();
            super.requery();
            if (row!=null && row>=0){
                getSelectionModel().setSelectionInterval(row, row);
                scrollRectToVisible(getCellRect(row, getSelectedColumn(), true));
            };
            gridSelectionChange();
        }
    }

    class TeacherSelectPanel extends SelectPanel{
        int teacher_id = -1;
        String sourceSQL = 
        
          "select distinct s.subject_name,d.label,sg.group_label,sg.hour_per_week,\n" +
            "sg.depart_id,sg.group_id,sg.subject_id,\n" +
            "sg.group_sequence_id,sg.pupil_count,sg.stream_id,sg.default_teacher_id,sg.default_room_id \n" +
            "from teacher a inner join profile_item b\n" +
            "on a.profile_id=b.profile_id\n" +
            "inner join v_subject_group sg on sg.subject_id=b.subject_id\n" +
            "inner join subject s on b.subject_id=s.id\n" +
            "inner join depart d on d.id=sg.depart_id\n" +
            "where sg.default_teacher_id is null "; // and  a.id=9;";
        
        String destanationSQL = "select b.subject_name,d.label,a.group_label,a.hour_per_week,r.room_name, \n"
                + "a.subject_id,a.group_id,a.group_type_id,a.default_room_id,a.depart_id \n"
                + "from v_subject_group a \n"
                + " inner join subject b on a.subject_id=b.id \n"
                + " inner join depart d on d.id=a.depart_id \n"
                + " left join room r on r.id=a.default_room_id \n"
                + " where a.default_teacher_id=";

        public void setTeacherId(Integer teacher_id) {
            this.teacher_id = teacher_id;
            try {
                requery();
                commands.updateActionList();
//                updateActionList();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void requery() throws Exception {
            Dataset dataset;
            
            if (chProfileOnly.isSelected()){
                dataset = DataModule.getSQLDataset(sourceSQL+" and a.id="+teacher_id);
            } else {
                dataset = DataModule.getSQLDataset(sourceSQL);
            }
            dataset.open();            
            sourceGrid.setDataset(dataset);
            
            dataset = DataModule.getSQLDataset(destanationSQL+teacher_id);
            dataset.open();
            destanationGrid.setDataset(dataset);
        }
        
        @Override
        public void include() throws Exception {
            Values values;
            IDataset dataset = sourceGrid.getDataset();
            int depart_id,subject_id,group_id,hour=0;
            try{
                for (int row : sourceGrid.getSelectedRows()){
                   
                    values=dataset.getValues(sourceGrid.convertRowIndexToModel(row));
                    hour += values.getInteger("hour_per_week");
                    
                    depart_id=(Integer)values.get("depart_id");
                    subject_id=(Integer)values.get("subject_id");
                    group_id=(Integer)values.get("group_id");
                    DataTask.inclideGroupToTeacher(depart_id, subject_id, group_id, teacher_id);
                }
                DataModule.commit();
            } catch (Exception e){
                DataModule.rollback();
                throw new Exception("INCLUDE_ERROR\n"+e.getMessage());
            }
            
            sourceGrid.removeSelectedRow();
            destanationGrid.requery();
            commands.updateActionList();
            
            // Изменения кол.ва часов преподавателя
            updateTeacherHour(hour);
        }
        /**
         * Изменение количества часов преподавателя после
         *         добавления/исключения предметов 
         * @param hour + или - в зависимости добавление удаление
         * @throws Exception 
         */
        protected void updateTeacherHour(int hour) throws Exception{
            Values values = grid.getValues();
            Integer n = values.getInteger("total_hour",0)+hour;
//            n = (n==null?0:n)+hour;
            values.setValue("total_hour", n);
            grid.setValues(values);
        }

        @Override
        public void exclude() throws Exception {
            Values values;
            IDataset dataset = destanationGrid.getDataset();
            int depart_id,subject_id,group_id,hour=0;
            try{
                for (int row : destanationGrid.getSelectedRows()){
                    values=dataset.getValues(destanationGrid.convertRowIndexToModel(row));
                    hour+=values.getInteger("hour_per_week",0);
                    depart_id=values.getInteger("depart_id");
                    subject_id=values.getInteger("subject_id");
                    group_id=values.getInteger("group_id");
                    DataTask.excludeGroupFromTeacher(depart_id, subject_id, group_id);
                }
                DataModule.commit();
            } catch (Exception e){
                DataModule.rollback();
                throw new Exception("EXCLUDE_ERROR\n"+e.getMessage());
            }
            
            // Изменение кол.ва часов преподавателя
            updateTeacherHour(-hour);
            requery();
        }

        @Override
        public void includeAll() throws Exception{
            IDataset dataset = sourceGrid.getDataset();
            Integer depart_id;
            Integer subject_id;
            Integer group_id;
            Values values;
            Integer hour = 0;
            try{
                for (int row=0;row<dataset.getRowCount();row++){
                    values=dataset.getValues(row);
                    hour+=values.getInteger("hour_per_week",0);
                    depart_id= values.getInteger("depart_id");
                    subject_id=values.getInteger("subject_id");
                    group_id=values.getInteger("group_id");
                    DataTask.inclideGroupToTeacher(depart_id, subject_id, group_id, teacher_id);
                }
                DataModule.commit();
            } catch (Exception e){
                DataModule.rollback();
                throw new Exception("INCLUDE_ALL\n"+e.getMessage());
            }
            updateTeacherHour(hour);
            requery();
        }

        @Override
        public void excludeAll() throws Exception{
            IDataset dataset = destanationGrid.getDataset();
            Integer depart_id;
            Integer subject_id;
            Integer group_id;
            Integer hour=0;
            Values values;
            try{
                for (int row=0;row<dataset.getRowCount();row++){
                    values=dataset.getValues(row);
                    hour+=values.getInteger("hour_per_week",0);
                    depart_id= values.getInteger("depart_id");
                    subject_id=values.getInteger("subject_id");
                    group_id=values.getInteger("group_id");
                    DataTask.excludeGroupFromTeacher(depart_id, subject_id, group_id);
                }
                DataModule.commit();
            } catch (Exception e){
                DataModule.rollback();
                throw new Exception("EXCLUDE_ALL_ERROR\n"+e.getMessage());
            }
            updateTeacherHour(-hour);
            requery();
        }


        public void open() throws Exception {
//            setTeacherId(1);
        }

    }
    
    
    public TeacherPanel() {
        setLayout(new BorderLayout());
        
        tabs.addTab(SUBJECT_GROUP_PANEL, selctPanel);
        tabs.addTab(PROFILE_PANEL, profilePanel);
        tabs.addTab(SHIFT_PANEL,shiftPanel);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        GridPanel gridPanel = new GridPanel("Преподаватели", grid);
        splitPane.setTopComponent(gridPanel);
        splitPane.setBottomComponent(tabs);
        splitPane.setResizeWeight(0.5);
        add(splitPane);
        setPreferredSize(new Dimension(800,600));
        
        commands.setCommands(TEACHER_COMMANDS);
        commands.addCommandListener(this);
        commands.updateActionList();
        
        grid.setAction("GRID_APPEND",commands.getAction(CREATE_TEACHER));
        grid.setAction("GRID_EDIT", commands.getAction(EDIT_TEACHER));
        grid.setAction("GRID_DELETE",commands.getAction(DELETE_TEACHER));
        
        grid.addExtAction(commands.getAction(EDIT_SHIFT));
        grid.addExtAction(commands.getAction(EDIT_PROFILE));
        
        gridPanel.addAction(commands.getAction(CREATE_TEACHER));
        gridPanel.addAction(commands.getAction(EDIT_TEACHER));
        gridPanel.addAction(commands.getAction(DELETE_TEACHER));
        
        profilePanel.addAction(commands.getAction(CREATE_PROFILE));
        profilePanel.addAction(commands.getAction(EDIT_PROFILE));
        profilePanel.addAction(commands.getAction(REMOVE_PROFILE));
        
        shiftPanel.addAction(commands.getAction(CREATE_SHIFT));
        shiftPanel.addAction(commands.getAction(EDIT_SHIFT));
        shiftPanel.addAction(commands.getAction(REMOVE_SHIFT));
        
    }

    @Override
    public void open() throws Exception {
        Dataset dataset = DataModule.getDataset("v_teacher");
        dataset.open();
        grid.setDataset(dataset);
        selctPanel.requery();
    }

    @Override
    public String getCaption() {
        return TEACHER;
    }

    @Override
    public JComponent getPanel() {
        return this;
    }
    
}