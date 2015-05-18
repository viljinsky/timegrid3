package ru.viljinsky.forms;

import java.awt.BorderLayout;
import java.awt.Dimension;
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


class RoomPanel extends JPanel implements IOpenedForm,ISchedulePanel,IAppCommand{
    
    
    MasterGrid grid = new MasterGrid();
    SelectRoomPanel selectPanel = new SelectRoomPanel();
    
    DetailPanel shiftPanel = new ShiftRoomPanel();
    DetailPanel profilePanel = new ProfileRoomPanel();
    CommandMngr commands = new CommandMngr() {

        @Override
        public void updateAction(Action a) {
        }

        @Override
        public void doCommand(String command) {
            RoomPanel.this.doCommand(command);
        }
    };
    
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

        commands.setCommandList(ROOM_COMMANDS);
        
       
        gridPanel.addAction(commands.getAction(CREATE_ROOM));
        gridPanel.addAction(commands.getAction(EDIT_ROOM));
        gridPanel.addAction(commands.getAction(DELETE_ROOM));
        
        shiftPanel.addAction(commands.getAction(CREATE_SHIFT));
        shiftPanel.addAction(commands.getAction(EDIT_SHIFT));
        shiftPanel.addAction(commands.getAction(REMOVE_SHIFT));
        
        profilePanel.addAction(commands.getAction(CREATE_PROFILE));
        profilePanel.addAction(commands.getAction(EDIT_PROFILE));
        profilePanel.addAction(commands.getAction(REMOVE_PROFILE));
    }
    
    public void doCommand(String command){
        Integer shift_id,profile_id;
        Integer new_profile_id;
        Integer room_id;
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
                    room_id = grid.getIntegerValue("id");
                    if (Dialogs.editRoom(this, room_id))
                        grid.requery();
                    break;
                    
                case DELETE_ROOM:
                    room_id = grid.getIntegerValue("id");
                    if (Dialogs.deleteRoom(this, room_id))
                        grid.requery();
                    break;
                    
                case CREATE_PROFILE:
                    profile_id = grid.getIntegerValue("profile_id");
                    new_profile_id=Dialogs.createProfile(this, profile_id);
                    if (new_profile_id!=null){
                        try{
                            room_id=grid.getIntegerValue("id");
                            DataModule.execute("update room set profile_id="+new_profile_id+" where id="+room_id);
                            DataModule.commit();
                        } catch (Exception p){
                            DataModule.rollback();
                            throw new Exception("CREATE_PROFILE_ERROR\n"+p.getMessage());
                        }    
                        grid.requery();
                    }
                    break;
                    
                case EDIT_PROFILE:
                    profile_id=grid.getIntegerValue("profile_id");
                    if (Dialogs.editProfile(this, profile_id)){
                        grid.requery();
                    }
                    break;
                    
                case REMOVE_PROFILE:
                    profile_id = grid.getIntegerValue("profile_id");
                    if (Dialogs.removeProfile(this, profile_id)==true)
                        grid.requery();
                    break;
                    
                case CREATE_SHIFT:
                    room_id=grid.getIntegerValue("id");
                    shift_id=grid.getIntegerValue("shift_id");
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
                    shift_id= grid.getIntegerValue("shift_id");
                    if (Dialogs.editShift(this, shift_id)){
                        shiftPanel.grid.requery();
                    }
                    break;
                    
                case REMOVE_SHIFT:
                    shift_id=grid.getIntegerValue("shift_id");
                    if (Dialogs.removeShift(this,shift_id))
                        grid.requery();
                    break;
            }
        } catch (Exception e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
    
    
    //--------------------------------------------------------------------------
    
    class ShiftRoomPanel extends DetailPanel{
        DBShiftPanel shPanel = new DBShiftPanel();
        String sqlShift="select a.* from shift_detail a inner join room b on a.shift_id=b.shift_id where b.id=%room_id;";

        public ShiftRoomPanel() {
            super();
            gridPanel.add(shPanel,BorderLayout.WEST);
        }

        
        @Override
        public void reopen(Integer keyValue) throws Exception{
            dataset = DataModule.getSQLDataset(sqlShift.replace("%room_id",keyValue.toString()));
            dataset.open();
            grid.setDataset(dataset);
            shPanel.setDataset(dataset);
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
            int row = getSelectedRow();
            if (row>=0){
                try{
                    int room_id= grid.getIntegerValue("id");
                    selectPanel.setRoomId(room_id);
                    shiftPanel.reopen(room_id);
                    profilePanel.reopen(room_id);
                } catch (Exception e){
                    e.printStackTrace();
                }
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
        
        int room_id=-1;
        String sqlSource = 
            "select s.subject_name,d.label,v.group_label,\n"+
            "v.teacher,\n"+    
            "v.hour_per_week,v.pupil_count,v.depart_id,v.subject_id,v.group_id,v.stream_id,v.group_sequence_id "+
            " from room a inner join profile_item b "+
            "on a.profile_id=b.profile_id "+
            "inner join v_subject_group v on v.subject_id=b.subject_id "+
            "inner join depart d on d.id=v.depart_id "+
            "inner join subject s on s.id=v.subject_id "+
            "where v.default_room_id is null";
        
        String sqlDest =
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
            updateActionList();
        }
        
        @Override
        public void include() throws Exception {
            Integer depart_id,subject_id,group_id;
            Map<String,Object> values;
            IDataset dataset = sourceGrid.getDataset();
            try{
                for (int row:sourceGrid.getSelectedRows()){
                    values=dataset.getValues(sourceGrid.convertRowIndexToModel(row));
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
            requery();
        }

        @Override
        public void exclude() throws Exception {
            Integer depart_id,subject_id,group_id;
            Map<String,Object> values;
            IDataset dataset = destanationGrid.getDataset();
            try{
                for (int row: destanationGrid.getSelectedRows()){
                    values=dataset.getValues(destanationGrid.convertRowIndexToModel(row));
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
        }

        @Override
        public void includeAll() throws Exception {
            Integer depart_id,subject_id,group_id;
            Map<String,Object> values;
            IDataset dataset = sourceGrid.getDataset();
            try{
                for (int row=0;row<dataset.getRowCount();row++){
                    values=dataset.getValues(row);
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
            requery();
        }

        @Override
        public void excludeAll() throws Exception {
            Integer depart_id,subject_id,group_id;
            Map<String,Object> values;
            IDataset dataset = destanationGrid.getDataset();
            try{
                for (int row=0;row<dataset.getRowCount();row++){
                    values=dataset.getValues(row);
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


class DepartPanel extends MasterDetailPanel implements IOpenedForm,IAppCommand{

    CommandMngr commands = new CommandMngr() {

        @Override
        public void updateAction(Action a) {
        }

        @Override
        public void doCommand(String command) {
            DepartPanel.this.doCommand(command);
        }
            
    };


    @Override
    public Map<String, String> getParams() {
        Map<String,String> params;
        params = new HashMap<>();
        params.put(MASTER_DATASET,"v_depart");
        params.put(SLAVE_DATASET,"v_subject_group");
        params.put(REFERENCES,"depart_id=depart_id");
        return params;
    }

    public DepartPanel() {
        super();
        commands.setCommandList(new String[]{
            EDIT_DEPART,DELETE_DEPART,
            FILL_GROUP,CLEAR_GROUP,EDIT_SHIFT,ADD_GROUP,
            DELETE_GROUP,ADD_STREAM,EDIT_STREAM,REMOVE_STREAM,REFRESH,EDIT_GROUP
        });
        
//        addMasterAction(commands.getAction(CREATE_DEPART));
        addMasterAction(commands.getAction(EDIT_DEPART));
        addMasterAction(commands.getAction(DELETE_DEPART));
        
        addMasterAction(commands.getAction(FILL_GROUP));
        addMasterAction(commands.getAction(CLEAR_GROUP));
        addMasterAction(commands.getAction(EDIT_SHIFT));
        
        addMasterAction(commands.getAction(REFRESH));
        
        addDetailAction(commands.getAction(ADD_GROUP));
        addDetailAction(commands.getAction(EDIT_GROUP));
        addDetailAction(commands.getAction(DELETE_GROUP));
        
        addDetailAction(commands.getAction(ADD_STREAM));
        addDetailAction(commands.getAction(EDIT_STREAM));
        addDetailAction(commands.getAction(REMOVE_STREAM));
}
    
    public void doCommand(String commad){
        Integer stream_id=-1,depart_id=-1,subject_id=-1,group_id=-1,skill_id=-1;
        try{
            switch(commad){
                case REFRESH:
                    grid1.requery();
                    break;
                    
                case EDIT_DEPART:
                    depart_id = grid1.getIntegerValue("depart_id");
                    if (Dialogs.editDepart(this, depart_id))
                        grid1.requery();
                    break;
                    
                case DELETE_DEPART:
                    depart_id = grid1.getIntegerValue("depart_id");
                    if (Dialogs.deleteDepart(this, depart_id))
                        grid1.requery();
                    break;
                    
                case FILL_GROUP:
                    depart_id=grid1.getIntegerValue("depart_id");
                    DataTask.fillSubjectGroup2(depart_id);
                    grid2.requery();
                    break;
                    
                case CLEAR_GROUP:
                    depart_id=grid1.getIntegerValue("depart_id");
                    DataTask.clearSubjectGroup(depart_id);
                    grid2.requery();
                    break;
                    
                case EDIT_SHIFT:
                    Integer shift_id= grid1.getIntegerValue("shift_id");
                    Dialogs.editShift(this, shift_id);
                    break;
                    
                case ADD_GROUP:
                    depart_id= grid2.getIntegerValue("depart_id");
                    subject_id = grid2.getIntegerValue("subject_id");
                    DataTask.addSubjectGroup(depart_id,subject_id);
                    grid2.requery();
                    break;
                    
                case EDIT_GROUP:
                    depart_id = grid1.getIntegerValue("depart_id");
                    subject_id = grid2.getIntegerValue("subject_id");
                    group_id = grid2.getIntegerValue("group_id");
                    if (Dialogs.editSubjectGroup(this,depart_id,subject_id,group_id))
                        grid2.requery();
                    break;
                    
                case DELETE_GROUP:
                    depart_id=grid2.getIntegerValue("depart_id");
                    subject_id=grid2.getIntegerValue("subject_id");
                    group_id = grid2.getIntegerValue("group_id");
                    DataTask.deleteSubjectGroup(depart_id,subject_id,group_id);
                    grid2.requery();
                    break;
                    
                case ADD_STREAM:
                    depart_id=grid2.getIntegerValue("depart_id");
                    subject_id=grid2.getIntegerValue("subject_id");
                    group_id=grid2.getIntegerValue("group_id");
                    if (Dialogs.createStream(this, depart_id, subject_id, group_id))
                      grid2.requery();
                    break;
                    
                case REMOVE_STREAM:
                    depart_id=grid2.getIntegerValue("depart_id");
                    subject_id=grid2.getIntegerValue("subject_id");
                    DataTask.deleteStream(depart_id,subject_id);
                    grid2.requery();
                    break;
                    
                case EDIT_STREAM:
                    stream_id= grid2.getIntegerValue("stream_id");
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
    public String getCaption() {
        return DEPART;
    }

    @Override
    public JComponent getPanel() {
        return this;
    }

    @Override
    public void edit() {
        doCommand(EDIT_DEPART);
    }

    @Override
    public void append() {
//        doCommand(DEPART);
    }

    @Override
    public void delete() {
        doCommand(DELETE_DEPART);
    }

}

/////////////////////   CURRICULUM PANEL //////////////////////////////////////


//class CurriculumPanel extends MasterDetailPanel implements ActionListener,IOpenedForm,IAppCommand{
//
//    DBComboBox curriculumComboBox = new DBComboBox(){
//
//        @Override
//        public void onValueChange() {
//            System.out.println("Выбран учебный план : "+getValue().toString());
//            Map<String,Object> map = new HashMap<>();
//            map.put("curriculum_id", getValue());
//            try{
//                masterPanel.grid.setFilter(map);
//            } catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//    };
//    
//    CommandMngr commands = new CommandMngr() {
//
//        @Override
//        public void updateAction(Action a) {
////            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        @Override
//        public void doCommand(String command) {
//            CurriculumPanel.this.doCommand(command);
//        }
//
//    };
//    
//    @Override
//    public Map<String, String> getParams() {
//        Map<String,String> map = new HashMap<>();
//        map.put(MASTER_DATASET,"v_curriculum");
//        map.put(SLAVE_DATASET, "v_curriculum_detail");
//        map.put(REFERENCES, "curriculum_id=curriculum_id;skill_id=skill_id");
//        return map;
//    }
//
//    
//    public CurriculumPanel() {
//        super();
//        commands.setCommandList(new String[]{
//            CREATE_CURRICULUM,EDIT_CURRICULUM,DELETE_CURRICULUM,
//            FILL_CURRICULUM,CREATE_DEPART,EDIT_CURRICULUM_DETAIL});
//
//        addMasterControl(curriculumComboBox);
//
//        
//        addMasterAction(commands.getAction(CREATE_CURRICULUM));
//        addMasterAction(commands.getAction(EDIT_CURRICULUM));
//        addMasterAction(commands.getAction(DELETE_CURRICULUM));
////        addMasterAction(commands.getAction(CLEAR_CURRICULUM));
//        addMasterAction(commands.getAction(CREATE_DEPART));
//        
//        addDetailAction(commands.getAction(FILL_CURRICULUM));
//        addDetailAction(commands.getAction(EDIT_CURRICULUM_DETAIL));
//        
//        
//    }
//    
//    public void doCommand(String command){
//        Integer curriculum_id,subject_id,skill_id;
//        try{
//            switch (command){
//                case CREATE_CURRICULUM:
//                    curriculum_id =  Dialogs.createCurriculum(this);
//                    if (curriculum_id!=null){
//                        curriculumComboBox.requery();
//                        curriculumComboBox.setValue(curriculum_id);
//                    }
//                    break;
//                    
//                case EDIT_CURRICULUM:
//                    curriculum_id=(Integer)curriculumComboBox.getValue();
//                    if (Dialogs.editCurriculum(this,curriculum_id)){
//                        curriculumComboBox.requery();
//                        curriculumComboBox.setValue(curriculum_id);
//                    };
//                    break;
//                    
//                case DELETE_CURRICULUM:
//                    curriculum_id=(Integer)curriculumComboBox.getValue();
////                    curriculum_id=grid1.getIntegerValue("curriculum_id");
//                    if (Dialogs.deleteCurriculum(this,curriculum_id)){
//                        curriculumComboBox.requery();
////                        curriculumComboBox.setValue(curriculum_id);
////                          grid1.requery();
//                    }
//                    break;
//                    
//                case FILL_CURRICULUM:
//                    fillCurriculumnDetail();
//                    break;
//                    
//                case EDIT_CURRICULUM_DETAIL:
//                    curriculum_id = grid2.getIntegerValue("curriculum_id");
//                    subject_id = grid2.getIntegerValue("subject_id");
//                    skill_id=grid2.getIntegerValue("skill_id");
//                    if (Dialogs.editCurriculumDetail(this,curriculum_id,skill_id,subject_id))
//                        grid2.requery();
//                    break;
//                    
////                case CLEAR_CURRICULUM:
////                    clearCurriculumDetail();
////                    break;
//                    
//                case CREATE_DEPART:
//                    createDepart();
//                    break;
////                case EDIT_CURRICULUM:
////                    editDetails();
////                    break;
//            }
//        } catch (Exception e){
//            JOptionPane.showMessageDialog(this, e.getMessage());
//        }
//    }
//
//    @Override
//    public void actionPerformed(ActionEvent e) {
//        doCommand(e.getActionCommand());
//    }
//
//    @Override
//    public void edit() {
//        doCommand(EDIT_CURRICULUM);
////        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public void append() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public void delete() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//    
//    class CurriculumnDetailDialg extends SelectDialog{
//        public Integer skill_id;
//        public Integer curriculum_id;
//
//        @Override
//        public void doOnEntry() throws Exception {
//            Integer subject_id;
//            try{
//                for (Object n:getAdded()){
//                    subject_id=(Integer)n;
//                    DataTask.includeSubjectToCurriculumn(curriculum_id, skill_id,subject_id);
//                }
//
//                for (Object n:getRemoved()){
//                    subject_id=(Integer)n;
//                    DataTask.excludeSubjectFromCurriculumn(curriculum_id, skill_id, subject_id);
//                }
//                DataModule.commit();
//            } catch (Exception e){
//                DataModule.rollback();
//                throw new Exception("FILL_CURRICULUM_ERROR\n"+e.getMessage());
//            }
//        }
//    }
//    
//    protected void fillCurriculumnDetail() throws Exception {
//        CurriculumnDetailDialg dlg = new CurriculumnDetailDialg();
//        dlg.curriculum_id=grid1.getIntegerValue("curriculum_id");
//        dlg.skill_id=grid1.getIntegerValue("skill_id");
//        
//        IDataset ds = grid2.getDataset();
//        Set<Object> set= ds.getColumnSet("subject_id");
//        Dataset dataDataset = DataModule.getSQLDataset("select id,subject_name from subject");
//        dlg.setDataset(dataDataset, "id", "subject_name");
//        dlg.setSelected(set);
//        if (dlg.showModal(this)==SelectDialog.RESULT_OK){
//            grid2.requery();
//        }
//    }
//    
//    public static final String MSG_GREATE_DEPART_OK = "Класс \"%s\" успешно создан";
//    public void createDepart() throws Exception{
//        Integer skill_id,curriculum_id;
//        skill_id=grid1.getIntegerValue("skill_id");
//        curriculum_id = grid1.getIntegerValue("curriculum_id");
//        Integer depart_id = Dialogs.createDepart(this, curriculum_id, skill_id);
//        if (depart_id!=null){
//            try {
//                DataTask.fillSubjectGroup2(depart_id);
//                DataModule.commit();
//                Recordset r=DataModule.getRecordet("select label from depart where id="+depart_id);
//                JOptionPane.showMessageDialog(this,String.format(MSG_GREATE_DEPART_OK,r.getString(0)));
//            } catch (Exception e){
//                DataModule.rollback();
//                throw new Exception("CREATE_DEPART_ERROR\n"+e.getMessage());
//            }
//        }
//        
//    }
//    
////    protected void clearCurriculumDetail() throws Exception{
////        Integer curriculum_id = grid1.getIntegerValue("curriculum_id");
////        Integer skill_id = grid1.getIntegerValue("skill_id");
////        DataTask.removeCurriculum(curriculum_id,skill_id);
////        grid2.requery();
////    }
//    
//
//    @Override
//    public String getCaption() {
//        return CURRICULUM;
//    }
//
//    @Override
//    public JComponent getPanel() {
//        return this;
//    }
//
//    @Override
//    public void open() {
//        super.open(); 
//        try{
//            Dataset ds = DataModule.getDataset("curriculum");
//            curriculumComboBox.setDataset(ds, "id", "caption");
//            if (!ds.isEmpty()){
//                curriculumComboBox.setValue(ds.getValues(0).getInteger("id"));
//            }
//        } catch (Exception e){
//            JOptionPane.showMessageDialog(this, e.getMessage());
//        }
//    }
//
//    
//}

///////////////////////////  SCHEDULE PANEL ///////////////////////////////////


//class SchedulePanel extends JPanel implements ActionListener,IOpenedForm{
//    Grid grid = new Grid();
//    DataModule dataModule = DataModule.getInstance();
//    GridPanel panel;
//    Combo combo = new Combo();
//
//    @Override
//    public void close() throws Exception {
////        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//    
//    class Combo extends DBComboBox{
//
//        @Override
//        public void onValueChange() {
//            System.out.println("->>"+getValue());
//            Map<String,Object> filter = new HashMap<>();
//            filter.put("depart_id",getValue());
//            try{
//                grid.setFilter(filter);
//            } catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//        
//    }
//    
//    public SchedulePanel(){
//        super(new BorderLayout());
//        panel = new GridPanel("Schedule", grid);
//        add(panel,BorderLayout.CENTER);
//        JButton button;
//        
//        button = new JButton("Clear");
//        button.addActionListener(this);
//        panel.AddButton(button);
//        
//        button = new JButton("Fill");
//        button.addActionListener(this);
//        panel.AddButton(button);
//        
//        panel.AddButton(combo);
//        
//    }
//    
//    public void doCommand(String command){
//        Integer depart_id;        
//        try{
//            depart_id = (Integer)combo.getValue();
//            switch (command){
//                case "Fill":
////                    ScheduleBuilder.placeDepart(depart_id);
////                    DataTask.fillSchedule(depart_id);
//                    break;
//                case "Clear":
//                    DataTask.clearSchedule(depart_id);
//                    break;
//            }
//            grid.requery();
//            
//        } catch (Exception e){
//            JOptionPane.showMessageDialog(this, e.getMessage());
//        }
//    }
//    @Override
//    public void open(){
//        Dataset dataset;
//        try{
//            
//            dataset = dataModule.getDataset("depart");
//            dataset.open();
//            combo.setDataset(dataset,"id","label");
//            
//            dataset = dataModule.getSQLDataset("select * from v_schedule order by depart_id,day_id,bell_id");
//            grid.setDataset(dataset);
//            
//            
//        } catch (Exception e){
//        }
//    }
//
//    @Override
//    public void actionPerformed(ActionEvent e) {
//        doCommand(e.getActionCommand());
//    }
//
//    @Override
//    public String getCaption() {
//        return "SCHEDULE";
//    }
//
//    @Override
//    public JComponent getPanel() {
//        return this;
//    }
//}

////////////////////////////////  TEACHER PANEL ////////////////////////////////
interface ISchedulePanel {
    public static final String SUBJECT_GROUP_PANEL = "Группы";
    public static final String PROFILE_PANEL = "Специализация";
    public static final String SHIFT_PANEL = "График";

}
class TeacherPanel extends JPanel implements IOpenedForm,ISchedulePanel, IAppCommand {
//    DataModule dataModule = DataModule.getInstance();
    MasterGrid grid = new MasterGrid();
    JTabbedPane tabs = new JTabbedPane();
    
    TeacherSelectPanel selctPanel = new TeacherSelectPanel();
    DetailPanel profilePanel = new ProfileTeacherPanel();
    DetailPanel shiftPanel = new ShiftTeacherPanel();
    CommandMngr commands = new CommandMngr() {

        @Override
        public void updateAction(Action a) {
        }

        @Override
        public void doCommand(String command) {
            TeacherPanel.this.doCommand(command);
        }
    };
    
   
    public void doCommand(String command){
        Integer shift_id,profile_id;
        Integer newProfileId,newShiftId;
        Integer teacher_id;
        Values values;
        try{
//            profile_id=grid.getIntegerValue("profile_id");
//            shift_id=grid.getIntegerValue("shift_id");
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
                    teacher_id = grid.getIntegerValue("id");
                    if (Dialogs.editTeacher(this, teacher_id)==true)
                        grid.requery();
                    break;
                    
                case DELETE_TEACHER:
                    teacher_id = grid.getIntegerValue("id");
                    if (Dialogs.deleteTeacher(this, teacher_id))
                        grid.requery();
                    break;
                    
                case CREATE_PROFILE:
                    profile_id=grid.getIntegerValue("profile_id");
//            shift_id=grid.getIntegerValue("shift_id");
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
                    profile_id=grid.getIntegerValue("profile_id");
                    Dialogs.editProfile(this,profile_id);
                    grid.requery();
                    break;
                    
                case REMOVE_PROFILE:
                    profile_id=grid.getIntegerValue("profile_id");
                    Dialogs.removeProfile(this, profile_id);
                    grid.requery();
                    break;
                 
                case CREATE_SHIFT:
                    shift_id=grid.getIntegerValue("shift_id");
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
                    shift_id=grid.getIntegerValue("shift_id");
                    Dialogs.editShift(this, shift_id);
                    grid.requery();
                    break;
                    
                case REMOVE_SHIFT:
                    shift_id=grid.getIntegerValue("shift_id");
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
    
    class ShiftTeacherPanel extends DetailPanel{
        DBShiftPanel shPanel = new DBShiftPanel();
        String sqlTeacherShift = "select a.* from shift_detail a inner join teacher b on a.shift_id=b.shift_id where b.id=%teacher_id;";

        public ShiftTeacherPanel() {
            super();
            gridPanel.add(shPanel,BorderLayout.WEST);
                    
        }
        
        @Override
        public void reopen(Integer keyValue) throws Exception{
            dataset = DataModule.getSQLDataset(sqlTeacherShift.replace("%teacher_id", keyValue.toString()));
            dataset.open();
            grid.setDataset(dataset);
            shPanel.setDataset(dataset);
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
            int row = getSelectedRow();
            if (row >= 0) {
                try {
                    Integer teacher_id = getIntegerValue("id");
                    selctPanel.setTeacherId(teacher_id);
                    shiftPanel.reopen(teacher_id);
                    profilePanel.reopen(teacher_id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        
          "select s.subject_name,d.label,sg.group_label,sg.hour_per_week,\n" +
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
                updateActionList();
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
            Map<String,Object> values;
            IDataset dataset = sourceGrid.getDataset();
            int depart_id,subject_id,group_id;
            try{
                for (int row : sourceGrid.getSelectedRows()){
                    values=dataset.getValues(sourceGrid.convertRowIndexToModel(row));
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
            requery();
        }

        @Override
        public void exclude() throws Exception {
            Map<String,Object> values;
            IDataset dataset = destanationGrid.getDataset();
            int depart_id,subject_id,group_id;
            try{
                for (int row : destanationGrid.getSelectedRows()){
                    values=dataset.getValues(destanationGrid.convertRowIndexToModel(row));
                    depart_id=(Integer)values.get("depart_id");
                    subject_id=(Integer)values.get("subject_id");
                    group_id=(Integer)values.get("group_id");
                    DataTask.excludeGroupFromTeacher(depart_id, subject_id, group_id);
                }
                DataModule.commit();
            } catch (Exception e){
                DataModule.rollback();
                throw new Exception("EXCLUDE_ERROR\n"+e.getMessage());
            }
            requery();
        }

        @Override
        public void includeAll() throws Exception{
            IDataset dataset = sourceGrid.getDataset();
            Integer depart_id;
            Integer subject_id;
            Integer group_id;
            Map<String,Object> values;
            try{
                for (int row=0;row<dataset.getRowCount();row++){
                    values=dataset.getValues(row);
                    depart_id= (Integer)values.get("depart_id");
                    subject_id=(Integer)values.get("subject_id");
                    group_id=(Integer)values.get("group_id");
                    DataTask.inclideGroupToTeacher(depart_id, subject_id, group_id, teacher_id);
                }
                DataModule.commit();
            } catch (Exception e){
                DataModule.rollback();
                throw new Exception("INCLUDE_ALL\n"+e.getMessage());
            }
            requery();
        }

        @Override
        public void excludeAll() throws Exception{
            IDataset dataset = destanationGrid.getDataset();
            Integer depart_id;
            Integer subject_id;
            Integer group_id;
            Map<String,Object> values;
            try{
                for (int row=0;row<dataset.getRowCount();row++){
                    values=dataset.getValues(row);
                    depart_id= (Integer)values.get("depart_id");
                    subject_id=(Integer)values.get("subject_id");
                    group_id=(Integer)values.get("group_id");
                    DataTask.excludeGroupFromTeacher(depart_id, subject_id, group_id);
                }
                DataModule.commit();
            } catch (Exception e){
                DataModule.rollback();
                throw new Exception("EXCLUDE_ALL_ERROR\n"+e.getMessage());
            }
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
        
        commands.setCommandList(TEACHER_COMMANDS);
        
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