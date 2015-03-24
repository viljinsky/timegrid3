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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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

    void AddButton(JButton button) {
        titlePanel.add(button);
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
    Map<String,String> params;
    
    public MasterDetailPanel(){
        super(new BorderLayout());
        params = getParams();
        setPreferredSize(new Dimension(800,600));
        grid1 = new MasterGrid();
        grid2 = new DetailGrid();
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(new GridPanel(params.get(MASTER_DATASET),grid1));
        splitPane.setBottomComponent(new GridPanel(params.get(SLAVE_DATASET),grid2));
        splitPane.setResizeWeight(0.5);
        add(splitPane);
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

class DepartPanel extends MasterDetailPanel{

    @Override
    public Map<String, String> getParams() {
        Map<String,String> params;
        params = new HashMap<>();
        params.put(MASTER_DATASET,"depart");
        params.put(SLAVE_DATASET,"subject_group");
        params.put(REFERENCES,"depart_id=id");
        return params;
    }

}

class CurriculumPanel extends MasterDetailPanel{
    @Override
    public Map<String, String> getParams() {
        Map<String,String> map = new HashMap<>();
        map.put(MASTER_DATASET,"curriculum");
        map.put(SLAVE_DATASET, "curriculum_detail");
        map.put(REFERENCES, "curriculum_id=id");
        return map;
    }
}

class SchedulePanel extends JPanel implements ActionListener{
    Grid grid = new Grid();
    DataModule dataModule = DataModule.getInstance();
    GridPanel panel;
    
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
        
        
    }
    public void open(){
        try{
            Dataset dataset = dataModule.getDataset("schedule");
            dataset.open();
            grid.setDataset(dataset);
        } catch (Exception e){
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try{
            switch (e.getActionCommand()){
                case "Fill":
                    DataTask.proc1();
                    break;
                case "Clear":
                    DataTask.proc2();
                    break;
            }
            grid.requery();
        } catch (Exception ee){
            JOptionPane.showMessageDialog(panel, ee.getMessage());
        }
        
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
