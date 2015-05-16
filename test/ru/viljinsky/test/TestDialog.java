 package ru.viljinsky.test;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import ru.viljinsky.dialogs.BaseDialog;
import ru.viljinsky.sqlite.DataModule;
import ru.viljinsky.sqlite.Dataset;
import ru.viljinsky.sqlite.Grid;

/**
 *
 * @author вадик
 */


public class TestDialog extends BaseDialog {
    Grid grid;
    
    @Override
    public Container getPanel() {
        String sql ="select distinct a.id as curriculum_id,c.id as skill_id,a.caption as cur_caption,c.caption from curriculum a inner join curriculum_detail b\n" +
                    "on a.id=b.curriculum_id, skill c;";
        grid = new Grid();
        Dataset dataset;
        try{
            dataset = DataModule.getSQLDataset(sql);
            dataset.open();
            grid.setDataset(dataset);
        } catch (Exception e){
            e.printStackTrace();
        }
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(400,500));
        panel.add(new JScrollPane(grid));
        return panel;
    }

    
    @Override
    public void doOnEntry() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public static void main(String[] args) throws Exception {
        
        DataModule.open();
        
        TestDialog dlg = new TestDialog();
        int res = dlg.showModal(null);
        System.out.println(res);
        dlg.dispose();
        dlg=null;
////        dlg.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        dlg.pack();
//        dlg.setVisible(true);
//        dlg.dispose();
//        dlg=null;
    }
}
