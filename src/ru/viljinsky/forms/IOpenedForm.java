package ru.viljinsky.forms;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import ru.viljinsky.CommandMngr;
import ru.viljinsky.DBComboBox;
import ru.viljinsky.DataModule;
import ru.viljinsky.Dataset;
import ru.viljinsky.Grid;
import ru.viljinsky.IDataset;
import ru.viljinsky.Recordset;
import ru.viljinsky.SelectDialog;

/**
 *
 * @author вадик
 */
public interface IOpenedForm {
    
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

class RoomPanel extends JPanel implements IOpenedForm{
    MasterGrid grid = new MasterGrid();
    DataModule dataModule = DataModule.getInstance();
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
    
    
    public void doCommand(String command){
        try{
            switch(command){
                case "CREATE_SHIFT":
                    break;
                case "EDIT_SHIFT":
                    editShift();
                    break;
                case "DELETE_SHIFT":
                    break;
            }
        } catch (Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
    
    public void editShift() throws Exception{
        ShiftDialog dlg = new ShiftDialog(){

            @Override
            public void doOnEntry() throws Exception {
                Integer shift_id,bell_id,day_id;
                shift_id=RoomPanel.this.grid.getInegerValue("shift_id");
                try{
                    for (Integer[] n:getRemoved()){
                        day_id=n[0]+1;bell_id=n[1]+1;
                        dataModule.execute(String.format("delete from shift_detail where shift_id=%d and day_id=%d and bell_id=%d;",shift_id,day_id,bell_id));
                    }
                    for (Integer[] n:getAdded()){
                        day_id=n[0]+1;bell_id=n[1]+1;
                        dataModule.execute(String.format("insert into shift_detail (shift_id,day_id,bell_id)values (%d,%d,%d);",shift_id,day_id,bell_id));
                    }
                    dataModule.commit();
                } catch(Exception e){
                    dataModule.rollback();
                    throw new Exception("EDIT_ROOM_SIFT_ERROR\n"+e.getMessage());
                }
            }

        };
        Integer shift_id = grid.getInegerValue("shift_id");
        Recordset rs = dataModule.getRecordet(String.format("select day_id,bell_id from shift_detail where shift_id=%d;",shift_id));
        Object[] values;
        int day_id,bell_id;
        List<Integer[]> list = new ArrayList<>();
        for (int i=0;i<rs.size();i++){
            values = rs.get(i);
            day_id=(Integer)values[0]-1;
            bell_id=(Integer)values[1]-1;
            list.add(new Integer[]{day_id,bell_id});
                    
        }
        dlg.setSelected(list);
        dlg.showModal(RoomPanel.this);
        if (dlg.modalResult==SelectDialog.RESULT_OK)
            shiftPanel.grid.requery();
    }
    
    //--------------------------------------------------------------------------
    
    class ShiftRoomPanel extends DetailPanel{
        String sqlShift="select * from shift_detail a inner join room b on a.shift_id=b.shift_id where b.id=%room_id;";
        @Override
        public void reopen(Integer keyValue) throws Exception{
            dataset = dataModule.getSQLDataset(sqlShift.replace("%room_id",keyValue.toString()));
            dataset.open();
            grid.setDataset(dataset);
        }
    }
    
    class ProfileRoomPanel extends DetailPanel{
        String sqlProfile = "select * from v_room_profile where room_id=%room_id;";
        @Override
        public void reopen(Integer keyValue) throws Exception{
            dataset = dataModule.getSQLDataset(sqlProfile.replace("%room_id", keyValue.toString()));
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
                    int room_id= grid.getInegerValue("id");
                    selectPanel.setRoomId(room_id);
                    shiftPanel.reopen(room_id);
                    profilePanel.reopen(room_id);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        
    }
    
    class SelectRoomPanel extends SelectPanel{
        
        int room_id=-1;
        String sqlSource = "select s.subject_name,d.label,a.group_id,a.hour_per_week,a.subject_id,a.depart_id\n"+
                            " from v_subject_group a inner join profile_item b on a.subject_id=b.subject_id\n" +
                            "inner join room c on c.profile_id=b.profile_id\n" +
                            "inner join subject s on s.id=a.subject_id\n"+ 
                            "inner join depart d on d.id=a.depart_id\n"+
//                            "order by a.depart_id,a.subject_id,a.group_id\n"+
                            "where a.default_room_id is null and c.id=";
        String sqlDest ="select s.subject_name,d.label,a.group_id,a.hour_per_week,a.subject_id,a.depart_id\n"
                + " from v_subject_group a\n"+
                 "inner join subject s on s.id=a.subject_id\n"+
                " inner join depart d on d.id=a.depart_id\n"+
                 " where a.default_room_id=";

        
        public void setRoomId(int room_id) throws Exception{
            this.room_id=room_id;
            requery();
        }
        
        @Override
        public void include() throws Exception {
            Integer depart_id,subject_id,group_id;
            Map<String,Object> values;
            IDataset dataset = sourceGrid.getDataset();
            try{
                for (int row:sourceGrid.getSelectedRows()){
                    values=dataset.getValues(row);
                    depart_id=(Integer)values.get("depart_id");
                    subject_id=(Integer)values.get("subject_id");
                    group_id=(Integer)values.get("group_id");
                    DataTask.includeGroupToRoom(depart_id, subject_id, group_id, room_id);
                }
                dataModule.commit();
            } catch (Exception e){
                dataModule.rollback();
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
                    values=dataset.getValues(row);
                    depart_id=(Integer)values.get("depart_id");
                    subject_id=(Integer)values.get("subject_id");
                    group_id=(Integer)values.get("group_id");
                    DataTask.excluderGroupFromRoom(depart_id, subject_id, group_id);
                }
                dataModule.commit();
            } catch (Exception e){
                dataModule.rollback();
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
                dataModule.commit();
            } catch (Exception e){
                dataModule.rollback();
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
                dataModule.commit();
            } catch (Exception e){
                dataModule.rollback();
                throw new Exception("EXCLUDE_ERROR\n"+e.getMessage());
            }
            requery();
        }

        @Override
        public void requery() throws Exception {
            Dataset dataset = dataModule.getSQLDataset(sqlSource+room_id);
            dataset.open();
            sourceGrid.setDataset(dataset);
            
            dataset = dataModule.getSQLDataset(sqlDest+room_id);
            dataset.open();
            destanationGrid.setDataset(dataset);
        }
    }

    public RoomPanel(){
        super(new BorderLayout());
        JTabbedPane tabs =new JTabbedPane();
        tabs.addTab("Subject group", selectPanel);
        tabs.addTab("Profile",profilePanel);
        tabs.addTab("Shift",shiftPanel);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(new JScrollPane(grid));
        splitPane.setBottomComponent(tabs);
        splitPane.setResizeWeight(0.5);
                
        add(splitPane);
        setPreferredSize(new Dimension(800,600));
        commands.setCommandList(new String[]{"EDIT_SHIFT"});
        shiftPanel.addAction(commands.getAction("EDIT_SHIFT"));
    }
    
    
    @Override
    public void open() throws Exception {
        Dataset dataset=dataModule.getDataset("room");
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
        return "ROOM";
    }

    @Override
    public JComponent getPanel() {
        return this;
    }
    
};


class DepartPanel extends MasterDetailPanel implements IOpenedForm{

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
        params.put(MASTER_DATASET,"depart");
        params.put(SLAVE_DATASET,"subject_group");
        params.put(REFERENCES,"depart_id=id");
        return params;
    }

    public DepartPanel() {
        super();
        commands.setCommandList(new String[]{
            "FILL","CLEAR","ADD_GROUP","DELETE_GROUP","ADD_STREAM","EDIT_STREAM","REMOVE_STREAM"
        });
        addMasterAction(commands.getAction("FILL"));
        addMasterAction(commands.getAction("CLEAR"));
        
        addDetailAction(commands.getAction("ADD_GROUP"));
        addDetailAction(commands.getAction("DELETE_GROUP"));
        
        addDetailAction(commands.getAction("ADD_STREAM"));
        addDetailAction(commands.getAction("EDIT_STREAM"));
        addDetailAction(commands.getAction("REMOVE_STREAM"));
}
    
    public void doCommand(String commad){
        Integer stream_id=-1,depart_id=-1,subject_id=-1,group_id=-1;
        try{
            switch(commad){
                case "FILL":
                    fillSubjectGroup();
                    break;
                case "CLEAR":
                    clearSubjectGroup();
                    break;
                case "ADD_GROUP":
                    depart_id= grid2.getInegerValue("depart_id");
                    subject_id = grid2.getInegerValue("subject_id");
                    DataTask.addSubjectGroup(depart_id,subject_id);
                    grid2.requery();
                    break;
                case "DELETE_GROUP":
                    depart_id=grid2.getInegerValue("depart_id");
                    subject_id=grid2.getInegerValue("subject_id");
                    group_id = grid2.getInegerValue("group_id");
                    DataTask.deleteSubjectGroup(depart_id,subject_id,group_id);
                    grid2.requery();
                    break;
                case "ADD_STREAM":
                    depart_id=grid2.getInegerValue("depart_id");
                    subject_id=grid2.getInegerValue("subject_id");
                    DataTask.createStream(depart_id,subject_id);
                    grid2.requery();
                    break;
                case "REMOVE_STREAM":
                    depart_id=grid2.getInegerValue("depart_id");
                    subject_id=grid2.getInegerValue("subject_id");
                    DataTask.deleteStream(depart_id,subject_id);
                    grid2.requery();
                    break;
                case "EDIT_STREAM":
                    break;
                default:
                    throw new Exception("UNKNOW_COMMAND\n\""+commad+"\"");
            }
        } catch (Exception e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
    
    public void fillSubjectGroup() throws Exception{
        if (grid1.getInegerValue("curriculum_id")==null)
            throw new Exception("DEPART_HAS_NOT_CURRICULUM");
        Integer depart_id=grid1.getInegerValue("id");
        DataTask.fillSubjectGroup2(depart_id);
        grid2.requery();
    }
    
    public void clearSubjectGroup() throws Exception{
        Integer depart_id=grid1.getInegerValue("id");
        DataTask.clearSubjectGroup(depart_id);
        grid2.requery();
    }

    @Override
    public String getCaption() {
        return "DEPART";
    }

    @Override
    public JComponent getPanel() {
        return this;
    }


}

/////////////////////   CURRICULUM PANEL //////////////////////////////////////

class CurriculumPanel extends MasterDetailPanel implements ActionListener,IOpenedForm{

    CommandMngr commands = new CommandMngr() {

        @Override
        public void updateAction(Action a) {
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void doCommand(String command) {
            CurriculumPanel.this.doCommand(command);
        }

    };
    
    @Override
    public Map<String, String> getParams() {
        Map<String,String> map = new HashMap<>();
        map.put(MASTER_DATASET,"curriculum");
        map.put(SLAVE_DATASET, "curriculum_detail");
        map.put(REFERENCES, "curriculum_id=id");
        return map;
    }

    
    public CurriculumPanel() {
        super();
        commands.setCommandList(new String[]{"FILL","CLEAR","EDIT"});
        addMasterAction(commands.getAction("CLEAR"));
        addMasterAction(commands.getAction("FILL"));
        addDetailAction(commands.getAction("EDIT"));
        
    }
    
    public void doCommand(String command){
        try{
            switch (command){
                case "FILL":
                    fillCurriculumnDetail();
                    break;
                case "CLEAR":
                    clearCurriculumDetail();
                    break;
                case "EDIT":
                    editDetails();
                    break;
            }
        } catch (Exception e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        doCommand(e.getActionCommand());
    }
    
    protected void fillCurriculumnDetail() throws Exception {
        Integer curriculum_id = grid1.getInegerValue("id");
        DataTask.fillCurriculumn(curriculum_id);
        grid2.requery();
    }
    
    protected void clearCurriculumDetail() throws Exception{
        Integer curriculum_id = grid1.getInegerValue("id");
        DataTask.removeCurriculum(curriculum_id);
        grid2.requery();
    }
    
    protected void editDetails() throws Exception{
        SelectDialog dlg = new SelectDialog() {

            @Override
            public void doOnEntry() throws Exception {
                Integer curriculum_id=grid1.getInegerValue("id");
                try{
                    for (Object k:getRemoved()){
                        DataTask.excludeSubjectFromCurriculumn(curriculum_id,(Integer)k);
                    }
                    for (Object k:getAdded()){
                        DataTask.includeSubjectFromCurriculumn(curriculum_id,(Integer)k);
                    }
                    dataModule.commit();
                }catch(Exception e){
                    dataModule.rollback();
                    throw new Exception("INCLUDE_EXCLUDE_ERROR\n"+e.getMessage());
                    
                }
            }
        };
        IDataset dataset;

        Set<Object> set = grid2.getDataset().getColumnSet("subject_id");
        
        dataset = dataModule.getDataset("subject");
        dlg.setDataset(dataset, "id", "subject_name");
        dlg.setSelected(set);
        if (dlg.showModal(null)==SelectDialog.RESULT_OK){
             grid2.requery();
        };
    }

    @Override
    public String getCaption() {
        return "CURRICULUMN";
    }

    @Override
    public JComponent getPanel() {
        return this;
    }

}

///////////////////////////  SCHEDULE PANEL ///////////////////////////////////

class SchedulePanel extends JPanel implements ActionListener,IOpenedForm{
    Grid grid = new Grid();
    DataModule dataModule = DataModule.getInstance();
    GridPanel panel;
    Combo combo = new Combo();

    @Override
    public void close() throws Exception {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    class Combo extends DBComboBox{

        @Override
        public void onValueChange() {
            System.out.println("->>"+getValue());
            Map<String,Object> filter = new HashMap<>();
            filter.put("depart_id",getValue());
            try{
                grid.setFilter(filter);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        
    }
    
    public SchedulePanel(){
        super(new BorderLayout());
        panel = new GridPanel("Schedule", grid);
        add(panel,BorderLayout.CENTER);
        JButton button;
        
        button = new JButton("Clear");
        button.addActionListener(this);
        panel.AddButton(button);
        
        button = new JButton("Fill");
        button.addActionListener(this);
        panel.AddButton(button);
        
        panel.AddButton(combo);
        
    }
    
    public void doCommand(String command){
        Integer depart_id;        
        try{
            depart_id = (Integer)combo.getValue();
            switch (command){
                case "Fill":
                    DataTask.fillSchedule(depart_id);
                    break;
                case "Clear":
                    DataTask.clearSchedule(depart_id);
                    break;
            }
            grid.requery();
            
        } catch (Exception e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
    @Override
    public void open(){
        Dataset dataset;
        try{
            
            dataset = dataModule.getDataset("depart");
            dataset.open();
            combo.setDataset(dataset,"id","label");
            
            dataset = dataModule.getDataset("v_schedule");
            grid.setDataset(dataset);
            
            
        } catch (Exception e){
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        doCommand(e.getActionCommand());
    }

    @Override
    public String getCaption() {
        return "SCHEDULE";
    }

    @Override
    public JComponent getPanel() {
        return this;
    }
}
////////////////////////////////  TEACHER PANEL ////////////////////////////////

class TeacherPanel extends JPanel implements IOpenedForm {
    DataModule dataModule = DataModule.getInstance();
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
        try{
            switch (command){
                case "EDIT_PROFILE":
                    ((ProfileTeacherPanel)profilePanel).editProfile();
                    break;
                case "EDIT_SHIFT":
                    ((ShiftTeacherPanel)shiftPanel).editProfile();
                    break;
            }
        } catch (Exception e){
            JOptionPane.showMessageDialog(TeacherPanel.this, e.getMessage());
        }
    }

    
    class ProfileTeacherPanel extends DetailPanel{
        String sqlTeacherProfilee = "select * from v_teacher_profile where teacher_id=%teacher_id";
        
        public void editProfile(){
            SelectDialog dlg = new SelectDialog() {

                @Override
                public void doOnEntry() throws Exception {
                    Integer profile_id = TeacherPanel.this.grid.getInegerValue("profile_id");
                    try{
                        for (Object k:getRemoved()){
                            DataTask.excludeSubjectFromProfile(profile_id,(Integer)k);
                        }
                        for (Object k:getAdded()){
                            DataTask.includeSubjectToProfile(profile_id,(Integer)k);
                        }
                        dataModule.commit();
                    } catch (Exception e){
                        dataModule.rollback();
                        throw new Exception("INCLUDE_EXCLUDE_PROFILE_ERROR\n"+e.getMessage());
                    }
                }
            };
            try{
                
                Set<Object> s = grid.getDataset().getColumnSet("subject_id");
                IDataset dataset = dataModule.getDataset("subject");
                dlg.setDataset(dataset, "id", "subject_name");
                dlg.setSelected(s);
                if (dlg.showModal(null)==SelectDialog.RESULT_OK)
                    grid.requery();
            
            } catch (Exception ee){
                ee.printStackTrace();
            }
        }
        
        @Override
        public void reopen(Integer keyValue) throws Exception{
            dataset = dataModule.getSQLDataset(sqlTeacherProfilee.replace("%teacher_id",keyValue.toString()));
            dataset.open();
            grid.setDataset(dataset);
        }
    }
    
    class ShiftTeacherPanel extends DetailPanel{
        String sqlTeacherShift = "select * from shift_detail a inner join teacher b on a.shift_id=b.shift_id where b.id=%teacher_id;";
        
        @Override
        public void reopen(Integer keyValue) throws Exception{
            dataset = dataModule.getSQLDataset(sqlTeacherShift.replace("%teacher_id", keyValue.toString()));
            dataset.open();
            grid.setDataset(dataset);
        }
        
        public void editProfile() throws Exception{
            ShiftDialog dlg = new ShiftDialog(){

                @Override
                public void doOnEntry() throws Exception {
                    Integer shift_id,day_id,bell_id;
                    shift_id=TeacherPanel.this.grid.getInegerValue("shift_id");
                    try{
                        for (Integer[] n:getRemoved()){
                            day_id=n[0]+1;bell_id=n[1]+1;
                            dataModule.execute(String.format("delete from shift_detail where shift_id=%d and day_id=%d and bell_id=%d;",shift_id,day_id,bell_id));
                        }
                        for (Integer[] n:getAdded()){
                            day_id=n[0]+1;bell_id=n[1]+1;
                            dataModule.execute(String.format("insert into  shift_detail (shift_id,day_id,bell_id) values(%d,%d,%d);",shift_id,day_id,bell_id));
                        }
                        
                        
                        dataModule.commit();
                    } catch (Exception e){
                        dataModule.rollback();
                        throw new Exception("TEACHER_EDIT_PROFILE_ERROR\n"+e.getMessage());
                    }
                }

            };
            Integer shift_id= TeacherPanel.this.grid.getInegerValue("shift_id");
            Recordset rs = dataModule.getRecordet("select day_id,bell_id from shift_detail where shift_id="+shift_id);
            List<Integer[]> list = new ArrayList<>();
            Object[] values;
            Integer day,bell;
            for (int i=0;i<rs.size();i++){
                values = rs.get(i);
                day=(Integer)values[0]-1;
                bell=(Integer)values[1]-1;
                list.add(new Integer[]{day,bell});
            }
            dlg.setSelected(list);
            dlg.showModal(TeacherPanel.this);
            if (dlg.modalResult==SelectDialog.RESULT_OK)
                grid.requery();
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
                    Integer teacher_id = getInegerValue("id");
                    selctPanel.setTeacherId(teacher_id);
                    shiftPanel.reopen(teacher_id);
                    profilePanel.reopen(teacher_id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class TeacherSelectPanel extends SelectPanel{
        int teacher_id = -1;
        String sourceSQL =  //"select * from v_subject_group where default_teacher_id is null;";
          "select d.subject_name,e.label, c.default_teacher_id,\n" +
        "   c.subject_id as teacher_id,c.group_id as group_id,c.depart_id as depart_id,c.subject_id as subject_id,c.hour_per_week\n" +
        "  from teacher a inner join profile_item b\n" +
        "on a.profile_id=b.profile_id\n" +
        "inner join v_subject_group c on c.subject_id=b.subject_id\n" +
        "inner join subject d on d.id=c.subject_id\n" +
        "inner join depart e on e.id=c.depart_id\n" +
        "where c.default_teacher_id is null and  a.id=";
        
        String destanationSQL = "select b.subject_name,d.label,a.subject_id,\n"
                + "a.group_id,a.group_type_id,a.default_room_id,a.depart_id from v_subject_group a\n"+
                " inner join subject b on a.subject_id=b.id\n"+
                " inner join depart d on d.id=a.depart_id\n"+
                " where a.default_teacher_id=";

        public void setTeacherId(Integer teacher_id) {
            this.teacher_id = teacher_id;
            try {
                requery();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void requery() throws Exception {
            Dataset dataset;
            
            dataset = dataModule.getSQLDataset(sourceSQL+teacher_id);
            dataset.open();            
            sourceGrid.setDataset(dataset);
            
            dataset = dataModule.getSQLDataset(destanationSQL+teacher_id);
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
                    values=dataset.getValues(row);
                    depart_id=(Integer)values.get("depart_id");
                    subject_id=(Integer)values.get("subject_id");
                    group_id=(Integer)values.get("group_id");
                    DataTask.inclideGroupToTeacher(depart_id, subject_id, group_id, teacher_id);
                }
                dataModule.commit();
            } catch (Exception e){
                dataModule.rollback();
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
                    values=dataset.getValues(row);
                    depart_id=(Integer)values.get("depart_id");
                    subject_id=(Integer)values.get("subject_id");
                    group_id=(Integer)values.get("group_id");
                    DataTask.excludeGroupFromTeacher(depart_id, subject_id, group_id);
                }
                dataModule.commit();
            } catch (Exception e){
                dataModule.rollback();
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
                dataModule.commit();
            } catch (Exception e){
                dataModule.rollback();
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
                dataModule.commit();
            } catch (Exception e){
                dataModule.rollback();
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
        
        tabs.addTab("Subject group", selctPanel);
        tabs.addTab("Profile", profilePanel);
        tabs.addTab("Shift",shiftPanel);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(new JScrollPane(grid));
        splitPane.setBottomComponent(tabs);
        splitPane.setResizeWeight(0.5);
        add(splitPane);
        setPreferredSize(new Dimension(800,600));
        commands.setCommandList(new String[]{"EDIT_PROFILE","EDIT_SHIFT"});
        profilePanel.addAction(commands.getAction("EDIT_PROFILE"));
        shiftPanel.addAction(commands.getAction("EDIT_SHIFT"));
    }

    @Override
    public void open() throws Exception {
        Dataset dataset = dataModule.getDataset("teacher");
        dataset.open();
        grid.setDataset(dataset);
        selctPanel.requery();
    }

    @Override
    public String getCaption() {
        return "TEACHER";
    }

    @Override
    public JComponent getPanel() {
        return this;
    }
    
}