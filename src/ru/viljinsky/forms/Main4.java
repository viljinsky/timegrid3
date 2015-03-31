/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
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
import ru.viljinsky.TimeGridPanel;
import ru.viljinsky.util.SQLMonitor;


interface IOpenedForm{
    public void open() throws Exception;
    public String getCaption();
    public JComponent getPanel();
}

;

class RoomPanel extends JPanel implements IOpenedForm{
    Grid grid = new Grid();
    DataModule dataModule = DataModule.getInstance();
    SelectRoomPanel selectPanel = new SelectRoomPanel();
    
    
    class SelectRoomPanel extends SelectPanel{

        @Override
        public void include() throws Exception {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void exclude() throws Exception {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void includeAll() throws Exception {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void excludeAll() throws Exception {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void requery() throws Exception {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    public RoomPanel(){
        super(new BorderLayout());
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(new JScrollPane(grid));
        splitPane.setBottomComponent(selectPanel);
        splitPane.setResizeWeight(0.5);
                
        add(splitPane);
    }
    
    @Override
    public void open() throws Exception {
        Dataset dataset=dataModule.getDataset("room");
        dataset.open();
        grid.setDataset(dataset);
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
//        grid1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//
//            @Override
//            public void valueChanged(ListSelectionEvent e) {
//                try{
//                int nn = grid1.getInegerValue("id");
//                System.out.println("-->"+nn);
//                } catch (Exception ee){}
//            }
//        });
        
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
    
}

///////////////////////////  SCHEDULE PANEL ///////////////////////////////////

class SchedulePanel extends JPanel implements ActionListener,IOpenedForm{
    Grid grid = new Grid();
    DataModule dataModule = DataModule.getInstance();
    GridPanel panel;
    Combo combo = new Combo();
    
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

////////////////////////    MAIN 4 ////////////////////////////////////////////

public class Main4 extends JPanel{
    TestShift2 testShift =null ;
    Dictonary dictionary = null;
    
//    public Main4(){
//        super();
//        
////        monitor = new SQLMonitor();
////        monitor.pack();
////        monitor.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
//        
//        
//    }
    
    class Act extends AbstractAction{

        public Act(String name) {
            super(name);
            putValue(ACTION_COMMAND_KEY, name);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            doCommand(e.getActionCommand());
        }
    }
    
    private void doCommand(String command){
        try{
            switch(command){
                case "DICTIONARY":
                    if (dictionary==null){
                        dictionary = new Dictonary();
                        dictionary.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                        dictionary.pack();
                        dictionary.open();
                    }
                    dictionary.setVisible(true);
                    break;
                    
                case "sqlMonitor":
                    SQLMonitor.showMonitor(Main4.this);
                    break;
                case "testShift":
                    
                    if (testShift==null){
                        testShift = new TestShift2();
                        testShift.pack();
                        testShift.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                        testShift.open();
                    }
                    testShift.setVisible(true);
                    break;
                case "timegrid":
                    TimeGridPanel.showTimeGrid(Main4.this);
                    break;
                case "exit":
                    System.exit(0);
                    break;
            }
        } catch (Exception e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
        
    }
    public JMenuBar createMenuBar(){
        JMenuBar result = new JMenuBar();
        result.add(createFileMenu());
        result.add(createUtilMenu());
        return result;
    }
    
    public JMenu createFileMenu(){
        JMenu result = new JMenu("File");
        result.add(new Act("fileOpen"));
        result.add(new Act("fileClose"));
        result.addSeparator();
        result.add(new Act("exit"));
        return result;
    }
    public JMenu createUtilMenu(){
        JMenu result = new JMenu("Util");
        result.add(new Act("sqlMonitor"));
        result.add(new Act("testShift"));
        result.add(new Act("DICTIONARY"));
        result.add(new Act("timegrid"));
        return result;
    }
    
    public static void main(String[] args) throws Exception{
        IOpenedForm[] forms = {
            new CurriculumPanel(),
            new DepartPanel(),
            new SchedulePanel(),
            new TeacherPanel(),
            new RoomPanel()
        };
        
        
        JTabbedPane tabbedPane = new JTabbedPane();
        for (IOpenedForm form:forms){
            tabbedPane.addTab(form.getCaption(), form.getPanel());
        }
                
        JFrame frame = new JFrame("Main4");
        Main4 panel = new Main4();
        panel.setLayout(new BorderLayout());
        panel.add(tabbedPane);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.setJMenuBar(panel.createMenuBar());
        frame.pack();
        
        // позичионирование главного окна
        int x,y;
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        x=(d.width-frame.getWidth())/2;
        y=(d.height-frame.getHeight())/2;         
        frame.setLocation(x,y);
        
        frame.setVisible(true);

      
        try{
            DataModule.getInstance().open(); 

            for (IOpenedForm form:forms){
                form.open();
            }
            
        } catch (Exception e){
            JOptionPane.showMessageDialog(frame, e.getMessage());
        }
        
    }
    
}
