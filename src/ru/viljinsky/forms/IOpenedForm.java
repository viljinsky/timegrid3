/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import ru.viljinsky.DBComboBox;
import ru.viljinsky.DataModule;
import ru.viljinsky.Dataset;
import ru.viljinsky.Grid;
import ru.viljinsky.KeyMap;
import static ru.viljinsky.forms.IMasterDetailConsts.MASTER_DATASET;

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

abstract class  AbstractOpenedForm implements IOpenedForm{
}


class RoomPanel extends JPanel implements IOpenedForm{
    MasterGrid grid = new MasterGrid();
    DataModule dataModule = DataModule.getInstance();
    SelectRoomPanel selectPanel = new SelectRoomPanel();

    
    
    class MasterGrid extends Grid{

        @Override
        public void gridSelectionChange() {
            int row = getSelectedRow();
            if (row>=0){
                try{
                    int room_id= grid.getInegerValue("id");
                    selectPanel.setRoomId(room_id);
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
        String sqlDest ="select s.subject_name,d.label,a.group_id,a.hour_per_week,a.subject_id\n"
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
            depart_id=sourceGrid.getInegerValue("depart_id");
            subject_id=sourceGrid.getInegerValue("subject_id");
            group_id=sourceGrid.getInegerValue("group_id");
            
            String sql = "update subject_group set default_room_id=? where depart_id=? and subject_id=? and group_id=?;";
            KeyMap map = new KeyMap();
            map.put(1, room_id);
            map.put(2, depart_id);
            map.put(3, subject_id);
            map.put(4, group_id);
            dataModule.execute(sql, map);
            requery();
        }

        @Override
        public void exclude() throws Exception {
            Integer depart_id,subject_id,group_id;
            String sql ="update subject_group set default_room_id=null where depart_id=? and subject_id=? and group_id=?;";
            depart_id = destanationGrid.getInegerValue("depart_id");
            subject_id=destanationGrid.getInegerValue("subject_id");
            group_id=destanationGrid.getInegerValue("group_id");
            KeyMap map = new KeyMap();
            map.put(1, depart_id);
            map.put(2,subject_id);
            map.put(3,group_id);
            dataModule.execute(sql,map);
            requery();
            
        }

        @Override
        public void includeAll() throws Exception {
            throw new UnsupportedOperationException("Not supported yet."); 
        }

        @Override
        public void excludeAll() throws Exception {
            throw new UnsupportedOperationException("Not supported yet."); 
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
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(new JScrollPane(grid));
        splitPane.setBottomComponent(tabs);
        splitPane.setResizeWeight(0.5);
                
        add(splitPane);
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

class DepartPanel extends MasterDetailPanel implements ActionListener,IOpenedForm{

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
        JButton button;
        button = new JButton("Fill");
        button.addActionListener(this);
        addMasterControl(button);
        
        button = new JButton("Clear");
        button.addActionListener(this);
        addMasterControl(button);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        doCommand(e.getActionCommand());
    }
    
    public void doCommand(String commad){
        try{
            switch(commad){
                case "Fill":
                    fillSubjectGroup();
                    break;
                case "Clear":
                    clearSubjectGroup();
                    break;
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

//    @Override
//    public void close() throws Exception {
////        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }


}

/////////////////////   CURRICULUM PANEL //////////////////////////////////////

class CurriculumPanel extends MasterDetailPanel implements ActionListener,IOpenedForm{
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
        JButton button = new JButton("Fill");
        button.addActionListener(this);
        addMasterControl(button);
        button = new JButton("Clear");
        button.addActionListener(this);
        addMasterControl(button);
    }
    
    public void doCommand(String command){
        try{
            switch (command){
                case "Fill":
                    fillCurriculumnDetail();
                    break;
                case "Clear":
                    clearCurriculumDetail();
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

    @Override
    public String getCaption() {
        return "CURRICULUMN";
    }

    @Override
    public JComponent getPanel() {
        return this;
    }

//    @Override
//    public void close() throws Exception {
////        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
    
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
            
            dataset = dataModule.getDataset("schedule");
//            dataset.open();
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
    TeacherSelectPanel selctPanel = new TeacherSelectPanel();
    JTabbedPane tabs = new JTabbedPane();

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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

//    class TeacherSelectPanel extends JPanel implements ActionListener {
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
            Integer room_id = grid.getInegerValue("teacher_room_id");
            int depart_id = sourceGrid.getInegerValue("depart_id");
            int subject_id = sourceGrid.getInegerValue("subject_id");
            int group_id = sourceGrid.getInegerValue("group_id");
            
            String sql = "update subject_group set default_teacher_id=?,default_room_id=? where depart_id=? and subject_id=? and group_id=?";
            KeyMap map = new KeyMap();
            map.put(1, teacher_id);
            map.put(2, room_id);
            map.put(3, depart_id);
            map.put(4, subject_id);
            map.put(5, group_id);
            dataModule.execute(sql, map);
            requery();
        }

        @Override
        public void exclude() throws Exception {
            int depart_id = destanationGrid.getInegerValue("depart_id");
            int subject_id = destanationGrid.getInegerValue("subject_id");
            int group_id = destanationGrid.getInegerValue("group_id");
            String sql = "update subject_group set default_teacher_id=null where depart_id=? and subject_id=? and group_id=?";
            KeyMap map = new KeyMap();
            map.put(1, depart_id);
            map.put(2, subject_id);
            map.put(3, group_id);
            dataModule.execute(sql, map);
            requery();
        }

        @Override
        public void includeAll() {
        }

        @Override
        public void excludeAll() {
        }


        public void open() throws Exception {
//            setTeacherId(1);
        }

    }

    public TeacherPanel() {
        setLayout(new BorderLayout());
        tabs.addTab("Subject group", selctPanel);
        tabs.addTab("Profile", new JPanel());
        tabs.addTab("Shift",new JPanel());
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(new JScrollPane(grid));
        splitPane.setBottomComponent(tabs);
        splitPane.setResizeWeight(0.5);
        add(splitPane);
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


