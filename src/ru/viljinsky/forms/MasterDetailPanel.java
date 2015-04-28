/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
import ru.viljinsky.DataModule;
import ru.viljinsky.Dataset;
import ru.viljinsky.Grid;
import ru.viljinsky.IDataset;
import ru.viljinsky.Values;

/**
 *
 * @author вадик
 */
interface IMasterDetailConsts{
    public static final String MASTER_DATASET = "masterDataset";
    public static final String MASTER_SQL = "masterSQL";
    public static final String SLAVE_DATASET = "slaveDataset";
    public static final String SLAVE_SQL = "slaveSQL";
    public static final String REFERENCES = "references";
}

abstract class MasterDetailPanel extends JPanel implements IMasterDetailConsts {
    Grid grid1;
    Grid grid2;
    GridPanel masterPanel;
    GridPanel detailPanel;
    Map<String, String> params;

    public MasterDetailPanel() {
        super(new BorderLayout());
        params = getParams();
        setPreferredSize(new Dimension(800, 600));
        grid1 = new MasterGrid();
        grid2 = new DetailGrid();
        masterPanel = new GridPanel(params.get(MASTER_DATASET), grid1);
        detailPanel = new GridPanel(params.get(SLAVE_DATASET), grid2);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(masterPanel);
        splitPane.setBottomComponent(detailPanel);
        splitPane.setResizeWeight(0.5);
        add(splitPane);
    }

    public void addMasterControl(JComponent component) {
        masterPanel.titlePanel.add(component);
    }
    
    public void addMasterAction(Action action){
        JButton button = new JButton(action);
        button.setToolTipText((String)action.getValue(Action.LONG_DESCRIPTION));
        masterPanel.titlePanel.add(button);
    }
    
    public void addDetailAction(Action action){
        JButton button = new JButton(action);
        button.setToolTipText((String)action.getValue(Action.LONG_DESCRIPTION));
        detailPanel.titlePanel.add(button);
    }

    public abstract void edit();
    public abstract void append();
    public abstract void delete();
    
    
    class MasterGrid extends Grid {

        @Override
        public void delete() throws Exception {
            MasterDetailPanel.this.delete();
        }

        @Override
        public void edit() {
            MasterDetailPanel.this.edit();
        }

        @Override
        public void append() {
            MasterDetailPanel.this.append();
            
        }

        
        
        @Override
        public void gridSelectionChange() {
            IDataset dataset = grid1.getDataset();
            if (dataset==null)
                return;
            int row = getSelectedRow();
            if (row < 0) 
                return;
            
            Values values = dataset.getValues(row);
            Map<String, Object> filter = new HashMap<>();
            try {
            
            
                String[] refs = params.get(REFERENCES).split(";");

                String[] ss;
                String keys,vv;

                try{

                    for (String ref_item:refs){
                        ss=ref_item.split("=");
                        keys=ss[0];
                        vv=ss[1];
                        filter.put(keys, values.get(vv));
                    }
                    
                } catch (Exception e){
                    throw new Exception("PARSE_REF_ERROR\n"+refs);
                }
            
                grid2.setFilter(filter);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    class DetailGrid extends Grid {

        @Override
        public void append() {
            int row = grid1.getSelectedRow();
            if (row >= 0) {
                String[] ss = params.get(REFERENCES).split("=");
                String keys = ss[0];
                String vv = ss[1];
                Map<String, Object> v1 = grid1.getDataset().getValues(row);
                Map<String, Object> values = getDataset().getNewValues();
                values.put(keys, v1.get(vv));
                super.append(values); //To change body of generated methods, choose Tools | Templates.
            }
        }
    }

    public void open() {
        try {
            Dataset dataset1;
            Dataset dataset2;
            dataset1 = DataModule.getDataset(params.get(MASTER_DATASET));
            dataset1.test();
            grid1.setDataset(dataset1);
            dataset2 = DataModule.getDataset(params.get(SLAVE_DATASET));
            dataset2.test();
            grid2.setDataset(dataset2);
            dataset1.open();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    public void close() throws Exception{
        grid2.setDataset(null);
        grid1.setDataset(null);
    }
    
    public abstract Map<String, String> getParams();
    
}

