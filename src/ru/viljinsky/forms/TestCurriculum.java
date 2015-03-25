/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import ru.viljinsky.DBComboBox;
import ru.viljinsky.DataModule;
import ru.viljinsky.Dataset;
import ru.viljinsky.Grid;
import ru.viljinsky.IDataset;

/**
 *
 * @author вадик
 */

class GridPanel extends JPanel{
    Grid grid;
    JLabel lblStatus = new JLabel("Status");
    JLabel lblTitle = new JLabel("Title");
    JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    public GridPanel(String title,Grid grid){
        super(new BorderLayout());
        this.grid=grid;
        statusPanel.add(lblStatus);
        titlePanel.add(lblTitle);
        lblTitle.setText(title);
        
        add(titlePanel,BorderLayout.PAGE_START);
        add(new JScrollPane(grid),BorderLayout.CENTER);
        add(statusPanel,BorderLayout.PAGE_END);
        
        grid.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                gridSelectionChange();
            }
        });
        
    }
    
    protected void gridSelectionChange(){
        lblStatus.setText(String.format("Запись %d  из %d",grid.getSelectedRow(),grid.getRowCount()));
    }

    void AddButton(JComponent component) {
        titlePanel.add(component);
    }
}

interface IMasterDetailConsts{
    public static final String MASTER_DATASET = "masterDataset";
    public static final String MASTER_SQL = "masterSQL";
    public static final String SLAVE_DATASET = "slaveDataset";
    public static final String SLAVE_SQL = "slaveSQL";
    public static final String REFERENCES = "references";
}

abstract class MasterDetailPanel extends JPanel implements IMasterDetailConsts{
    protected DataModule dataModule =DataModule.getInstance();
    Grid grid1;
    Grid grid2;
    GridPanel masterPanel;
    GridPanel detailPanel;
    Map<String,String> params;
    
    public MasterDetailPanel(){
        super(new BorderLayout());
        params = getParams();
        setPreferredSize(new Dimension(800,600));
        grid1 = new MasterGrid();
        grid2 = new DetailGrid();
        masterPanel= new GridPanel(params.get(MASTER_DATASET), grid1);
        detailPanel = new GridPanel(params.get(SLAVE_DATASET), grid2);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(masterPanel);
        splitPane.setBottomComponent(detailPanel);
        splitPane.setResizeWeight(0.5);
        add(splitPane);
    }
    
    public void addMasterControl(JComponent component){
        masterPanel.titlePanel.add(component);
    }
    
    class MasterGrid extends Grid{

        @Override
        public void gridSelectionChange() {
            String[] ss = params.get(REFERENCES).split("=");
            String keys = ss[0];
            String vv = ss[1];
            IDataset dataset = grid1.getDataset();
            int row = getSelectedRow();
            if (row>=0){
                Map<String,Object> values = dataset.getValues(row);
                Map<String,Object> filter = new HashMap<>();
                filter.put(keys, values.get(vv));
                try{
                    grid2.setFilter(filter);
                } catch (Exception e){
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, e.getMessage());
                }
                
            }
        }
    }
    
    class DetailGrid extends Grid{

        @Override
        public void append() {
            int row = grid1.getSelectedRow();
            if (row>=0){
                
                String[] ss = params.get(REFERENCES).split("=");
                String keys = ss[0];
                String vv = ss[1];
                
                
                Map<String,Object> v1 = grid1.getDataset().getValues(row);
                Map<String,Object> values=getDataset().getNewValues();
                values.put(keys, v1.get(vv));
                super.append(values); //To change body of generated methods, choose Tools | Templates.
            }
        }
        
    }
    
    public void open(){
        try{
            
            Dataset dataset1,dataset2 ;
            dataset1 = dataModule.getDataset(params.get(MASTER_DATASET));
            dataset1.test();
            grid1.setDataset(dataset1);
            
            dataset2= dataModule.getDataset(params.get(SLAVE_DATASET));
            dataset2.test();
            grid2.setDataset(dataset2);
            
            dataset1.open();
            
        } catch (Exception e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
    
    public abstract Map<String,String> getParams();
}
////////////////////////////  DEPART PANEL ////////////////////////////////////

class DepartPanel extends MasterDetailPanel implements ActionListener{

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


}

/////////////////////   CURRICULUM PANEL //////////////////////////////////////

class CurriculumPanel extends MasterDetailPanel implements ActionListener{
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
    
    protected void fillCurriculumnDetail() throws Exception{
        Integer curriculum_id = grid1.getInegerValue("id");
        DataTask.fillCurriculumn(curriculum_id);
        grid2.requery();
    }
    
    protected void clearCurriculumDetail() throws Exception{
        Integer curriculum_id = grid1.getInegerValue("id");
        DataTask.removeCurriculum(curriculum_id);
        grid2.requery();
    }
    
}

///////////////////////////  SCHEDULE PANEL ///////////////////////////////////

class SchedulePanel extends JPanel implements ActionListener{
    Grid grid = new Grid();
    DataModule dataModule = DataModule.getInstance();
    GridPanel panel;
    DBComboBox combo = new DBComboBox();
    
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
    public void open(){
        Dataset dataset;
        try{
            
            dataset = dataModule.getDataset("depart");
            dataset.open();
            combo.setDataset(dataset,"id","label");
            
            dataset = dataModule.getDataset("schedule");
            dataset.open();
            grid.setDataset(dataset);
            
            
        } catch (Exception e){
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        doCommand(e.getActionCommand());
    }
}

public class TestCurriculum extends JPanel{
    
    public static void main(String[] args) throws Exception{
        
        CurriculumPanel curriculumPanel = new CurriculumPanel();        
        DepartPanel departPanel = new DepartPanel();
        SchedulePanel schedulePanel = new SchedulePanel();
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("curriculum", curriculumPanel);
        tabbedPane.addTab("depart",departPanel);
        tabbedPane.addTab("schedule", schedulePanel);
        JFrame frame = new JFrame("Test curriculumn");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(tabbedPane);
        frame.pack();
        frame.setVisible(true);

      
        try{
            DataModule.getInstance().open();        
            curriculumPanel.open();
            departPanel.open();
            schedulePanel.open();
        } catch (Exception e){
            JOptionPane.showMessageDialog(frame, e.getMessage());
        }
        
    }
    
}
