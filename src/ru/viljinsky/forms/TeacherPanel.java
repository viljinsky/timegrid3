/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import ru.viljinsky.DataModule;
import ru.viljinsky.Dataset;
import ru.viljinsky.Grid;
import ru.viljinsky.KeyMap;


class TeacherPanel extends JPanel implements IOpenedForm {
    DataModule dataModule = DataModule.getInstance();
    G grid = new G();
    TeacherSelectPanel selctPanel = new TeacherSelectPanel();
    JTabbedPane tabs = new JTabbedPane();

    class G extends Grid {

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
        int teacher_id;
        String sourceSQL =  //"select * from v_subject_group where default_teacher_id is null;";
          "select d.subject_name,e.label, c.default_teacher_id,\n" +
        "   c.subject_id as teacher_id,c.group_id as group_id,c.depart_id as depart_id,c.subject_id as subject_id,c.hour_per_week\n" +
        "  from teacher a inner join profile_item b\n" +
        "on a.profile_id=b.profile_id\n" +
        "inner join v_subject_group c on c.subject_id=b.subject_id\n" +
        "inner join subject d on d.id=c.subject_id\n" +
        "inner join depart e on e.id=c.depart_id\n" +
        "where c.default_teacher_id is null and  a.id=";
        
        String destanationSQL = "select * from v_subject_group where default_teacher_id=";

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
            int depart_id = sourceGrid.getInegerValue("depart_id");
            int subject_id = sourceGrid.getInegerValue("subject_id");
            int group_id = sourceGrid.getInegerValue("group_id");
            String sql = "update subject_group set default_teacher_id=? where depart_id=? and subject_id=? and group_id=?";
            KeyMap map = new KeyMap();
            map.put(1, teacher_id);
            map.put(2, depart_id);
            map.put(3, subject_id);
            map.put(4, group_id);
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
            setTeacherId(1);
        }

    }

    public TeacherPanel() {
        setLayout(new BorderLayout());
        tabs.addTab("Subject group", selctPanel);
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
        selctPanel.open();
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
