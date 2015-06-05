/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import ru.viljinsky.dialogs.BaseDialog;
import ru.viljinsky.dialogs.EntryDialog;
import ru.viljinsky.sqlite.DataModule;
import ru.viljinsky.sqlite.Dataset;
import ru.viljinsky.sqlite.Grid;
import ru.viljinsky.sqlite.Values;

/**
 *
 * @author вадик
 */

abstract class AbstractPanel extends JPanel implements CommandListener{
    String[] SHIFT_ACTIONS   = {"ADD","EDIT","DELET","POST","CANCEL"};
    String[] PROFILE_ACTIONS = {"ADD","EDIT","DELETE"};
    String[] SUBJECT_ACTIONS = {"ADD","EDIT","DELETE","MOVE_UP","MOVE_DOUN","COLORISE"};
    
    JPanel commandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    CommandMngr manager = new CommandMngr();
    
    public abstract void open() throws Exception;
    
    public AbstractPanel(){
        setLayout(new BorderLayout());
        add(commandPanel,BorderLayout.PAGE_START);
        manager.addCommandListener(this);
    }
    
    public void setCommands(String[] commands){
        manager.setCommands(commands);
        for (Action a :manager.getActions()){
            commandPanel.add(new JButton(a));
        }
        manager.updateActionList();
    }
    
    
}

public class TestShift3 extends BaseDialog{
    
    JTabbedPane tabs ;
    AbstractPanel shiftPanel;
    AbstractPanel profilePanel;
    AbstractPanel subjectPanel;

    public TestShift3() {
        super();
        try{ 
            shiftPanel.open();
            profilePanel.open();
            subjectPanel.open();
        } catch (Exception e){
        }
    }
    
    class SubjectPanel extends AbstractPanel{
        String SQL_SUBJECT_LIST =
                 "select a.subject_name,a.id,b.domain_caption,\n"+
                 "a.subject_domain_id,a.sort_order,a.color\n" +
                 "from subject a \n"+
                 "left join subject_domain b on a.subject_domain_id=b.id\n"
                + "order by a.sort_order";
        String SQL_MOVE_UP_DOWN_1 = "update subject set sort_order = %d where sort_order=%d";
        String SQL_MOVE_UP_DOWN_2 = "update subject set sort_order=%d where id=%d;";
        
        Integer subject_id = null;
        Integer subject_domain_id=null;
        SubjectRenderer subjectRenderer = new SubjectRenderer();
        
        class SubjectRenderer extends DefaultTableCellRenderer{

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                Values v = subjectList.getDataset().getValues(row) ;
                try{
                    setText(v.getString("color"));
                    String[] ss = v.getString("color").split(" ");
                    Color c= new Color(Integer.valueOf(ss[0]) ,Integer.valueOf(ss[1]), Integer.valueOf(ss[2]));
                    setBackground(c);
                    
                } catch (Exception e){
                }
                return this;
            }
            
        }
        Grid subjectList = new Grid(){

            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                if (column==1){
                    return subjectRenderer;
                }
                return super.getCellRenderer(row, column);
            }

            
            @Override
            public void gridSelectionChange() {
                Values values = getValues();
                try{
                    if (values == null){
                        subject_id=null;
                    } else { 
                        subject_id=values.getInteger("id");
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                manager.updateActionList();
                    
            }
            
        };
        Grid domainList = new Grid(){

            @Override
            public void gridSelectionChange() {
                Values values = getValues();
                try{
                    if (values==null){
                        subject_domain_id=null;
                        subjectList.close();
                    } else {
                        subject_domain_id=values.getInteger("id");
                        subjectList.setFilter(new Values("subject_domain_id", subject_domain_id));
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                manager.updateActionList();
            }
            
        };

        public SubjectPanel() {
            super();
            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            splitPane.setLeftComponent(new JScrollPane(domainList));
            splitPane.setRightComponent(new JScrollPane(subjectList));
            splitPane.setDividerLocation(200);
            add(splitPane);
            subjectList.setRealNames(true);
        }

        
        @Override
        public void open() throws Exception {
            Dataset dataset;
            dataset = DataModule.getDataset("subject_domain");
            domainList.setDataset(dataset);
            dataset.open();
            
            dataset = DataModule.getSQLDataset(SQL_SUBJECT_LIST);
            subjectList.setDataset(dataset);
            dataset.open();
            setCommands(SUBJECT_ACTIONS);
        }
        
        public void colorise() throws Exception{
            String sql = "update subject set color='%s' where id=%d";
            String color;
            String[] s ={"210","220","230","255"};
            Integer n=0;
            Dataset dataset = DataModule.getDataset("subject");
            dataset.open();
            Values values;
            
            try{
                loop:
                for (int i=0;i<s.length;i++)
                    for (int j=0;j<s.length;j++)
                        for (int k=0;k<s.length;k++){
                            color = s[i]+" "+s[j]+" "+s[k];
                            System.out.println(color);
                            values = dataset.getValues(n++);
                            DataModule.execute(String.format(sql,color,values.getInteger("id")));
                            if (n>=dataset.size()-1)
                                break loop;
                        }
                
                DataModule.commit();
            } catch (Exception e){
                DataModule.rollback();
                throw new Exception("COLORISE_ERROR\n"+e.getMessage());
            }
            subjectList.requery();
                    
        }
        
        private void move_up() throws Exception{
            Integer order = subjectList.getIntegerValue("sort_order");
            Integer oldId = subject_id;
            try{
                DataModule.execute(String.format(SQL_MOVE_UP_DOWN_1, order,order-1 ));
                DataModule.execute(String.format(SQL_MOVE_UP_DOWN_2, order-1,subject_id));
                DataModule.commit();
                subjectList.requery();
                subjectList.locate(new Values("id",oldId));
            } catch (Exception e){
                DataModule.rollback();
                throw new Exception("SORT_ERROR\n"+e.getMessage());
            }
        }
        
        private void move_doun() throws Exception{
            Integer order = subjectList.getIntegerValue("sort_order");
            Integer oldId = subject_id;
            try{
                DataModule.execute(String.format(SQL_MOVE_UP_DOWN_1, order,order+1 ));
                DataModule.execute(String.format(SQL_MOVE_UP_DOWN_2, order+1,subject_id));
                DataModule.commit();
                subjectList.requery();
                subjectList.locate(new Values("id",oldId));
            } catch (Exception e){
                DataModule.rollback();
                throw new Exception("SORT_ERROR\n"+e.getMessage());
            }
        }

        @Override
        public void doCommand(String command) {
            EntryDialog dlg;
            Dataset dataset;
            try{
                switch (command){
                    case "ADD":
                        dlg = new EntryDialog() {

                            @Override
                            public void doOnEntry() throws Exception {
                                try{
                                    getDataset().appned(getValues());
                                    DataModule.commit();
                                } catch (Exception e){
                                    DataModule.rollback();
                                    throw new Exception("ADD_COMMAND_ERROR"+e.getMessage());
                                }
                            }
                        };
                        dataset =DataModule.getDataset("subject");
                        dataset.test();
                        dlg.setDataset(dataset);
                        if (dlg.showModal(rootPane)==BaseDialog.RESULT_OK){
                            subjectList.requery();
                        }
                        
                        break;
                    case "EDIT":
                        dlg = new EntryDialog() {

                            @Override
                            public void doOnEntry() throws Exception {
                                try{
                                    getDataset().edit(0, getValues());
                                    DataModule.commit();
                                } catch (Exception e){
                                    DataModule.rollback();
                                    throw new Exception("EDIT_ERROR\n"+e.getMessage());
                                }
                            }
                        };
                        dataset =DataModule.getDataset("subject");
                        dataset.test();
                        Values values = subjectList.getValues();
                        dataset.open(new Values("id",values.getInteger("id")));
                        dlg.setDataset(dataset);
                        dlg.setValues(dataset.getValues(0));
                        if (dlg.showModal(rootPane)==BaseDialog.RESULT_OK){
                            subjectList.requery();
                        }
                        break;
                        
                    case "DELETE":
                        break;
                    case "MOVE_UP":
                        move_up();
                        break;
                    case "MOVE_DOUN":
                        move_doun();
                        break;
                    case "COLORISE":
                        colorise();
                        break;
                    default:
                        throw new Exception("UNKNOW_COMMAND\n"+command);
                        
                }
                System.out.println(command);
                        
            } catch (Exception e){
                JOptionPane.showMessageDialog(rootPane,e.getMessage(),"Ошибка",JOptionPane.ERROR_MESSAGE);
            }
        }

        @Override
        public void updateAction(Action action) {
            String command = (String)action.getValue(Action.ACTION_COMMAND_KEY);
            switch (command){
                case "ADD":
                    action.setEnabled(subject_domain_id!=null);
                case ("EDIT"):
                case ("DELETE"):
                case ("MOVE_UP"):
                case ("MOVE_DOUN"):
                    action.setEnabled(subject_id!=null);
                    break;
                
            }
        }
    }
    
    class ShiftPanel extends AbstractPanel{
        Integer shift_id = null;
        Grid grid = new Grid(){

            @Override
            public void gridSelectionChange() {
                Values values = getValues();
                try{
                    if (values!=null) {
                        shift_id=values.getInteger("id");
                        shiftEditor.setShiftId(shift_id);
                    } else {
                        shift_id=null;
                    }
                    manager.updateActionList();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            
        };
        ShiftEditor shiftEditor = new ShiftEditor(){

            @Override
            public void changing() {
                manager.updateActionList();
            }
            
        };

        public ShiftPanel() {
            super();
//            super(new BorderLayout());
            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            splitPane.setLeftComponent(new JScrollPane(grid));
            splitPane.setRightComponent(new JScrollPane(shiftEditor));
            splitPane.setDividerLocation(200);
            add(splitPane);
            setCommands(SHIFT_ACTIONS);
            
        }
        
        @Override
        public void open() throws Exception{
            Dataset dataset = DataModule.getDataset("shift");
            grid.setDataset(dataset);
            dataset.open();
            shiftEditor.open();
        }

        @Override
        public void doCommand(String command) {
            System.out.println("-->"+command);
            try{
            switch (command){
                case "ADD":
                    Integer new_shift_id = Dialogs.createShift(rootPane, null);
                    if (new_shift_id!=null){
                        grid.requery();
                        grid.locate(new Values("id",new_shift_id));
                    }
                    break;
                case "EDIT":
                    shiftEditor.setAllowEdit(true);
                    break;
                case "POST":
                    shiftEditor.post();
                    shiftEditor.setAllowEdit(false);
                    break;
                case "CANCEL":
                    shiftEditor.cancel();
                    shiftEditor.setAllowEdit(false);
                    break;
                case "DELETE":
                    break;
            }
            } catch (Exception e){
                JOptionPane.showMessageDialog(rootPane,e.getMessage(),"Ошибка",JOptionPane.ERROR_MESSAGE);
            }
        }

        @Override
        public void updateAction(Action action) {
            String command = (String)action.getValue(Action.ACTION_COMMAND_KEY);
            switch (command){
                case "ADD":
                    break;
                case "EDIT":
                    action.setEnabled(!shiftEditor.isAllowEdit());
                    break;
                case "POST":
                    action.setEnabled(shiftEditor.isHasChange());
                    break;
                case "CANCEL":
                    action.setEnabled(shiftEditor.isHasChange());
                    break;
                case "DELETE":
                    break;
            }
        }
        
        
    }
    
    class ProfilePanel extends AbstractPanel{
        Integer profile_id = null;
        Grid profileList = new Grid(){

            @Override
            public void gridSelectionChange() {
                Values values = getValues();
                try{
                    if (values!=null){
                        profile_id=values.getInteger("id");
                        profileSubject.setFilter(new Values("profile_id",profile_id));
                    } else {
                        profile_id = null;
                        profileSubject.close();
                    }
                    manager.updateActionList();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            
        };
        Grid profileSubject = new Grid();

        public ProfilePanel() {
            super();
            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            splitPane.setLeftComponent(new JScrollPane(profileList));
            splitPane.setRightComponent(new JScrollPane(profileSubject));
            splitPane.setDividerLocation(200);
            add(splitPane);
            setCommands(PROFILE_ACTIONS);
                    
        }
        
        @Override
        public void open() throws Exception{
            Dataset dataset = DataModule.getDataset("profile");
            profileList.setDataset(dataset);
            dataset.open();
            
            dataset = DataModule.getSQLDataset("select a.subject_name,a.id,b.profile_id from subject a inner join profile_item b on a.id=b.subject_id");
            profileSubject.setDataset(dataset);
            
        }

        @Override
        public void doCommand(String command) {
            try{
                switch(command){
                    case "EDIT":
                        if (Dialogs.editProfile(rootPane, profile_id)){
                            profileSubject.requery();
                        };
                        break;
                        
                    case "ADD":
                        Integer new_profile_id= Dialogs.createProfile(rootPane, null);
                        if (new_profile_id!=null){
                            profileList.requery();
                            profileList.locate(new Values("id",new_profile_id));
                            profile_id=new_profile_id;
                        }
                        break;
                    case "DELETE":
                        break;
                }
            } catch (Exception e){
                JOptionPane.showMessageDialog(rootPane,e.getMessage(),"Ошибка",JOptionPane.ERROR_MESSAGE);
            }
        }

        @Override
        public void updateAction(Action action) {
        }
        
    }
    
    

    @Override
    public Container getPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(600,400));
        
        shiftPanel= new ShiftPanel();
        profilePanel=new ProfilePanel();
        subjectPanel= new SubjectPanel();
        
        tabs = new JTabbedPane();
        tabs.addTab("Графики",shiftPanel);
        tabs.addTab("Профили", profilePanel);
        tabs.addTab("Предметы",subjectPanel);
                
        panel.add(tabs);
        return panel;
    }

    
    @Override
    public void doOnEntry() throws Exception {
        modalResult=RESULT_OK;
    }
    
    public static Integer showDialog(JComponent owner){
        BaseDialog dlg = new TestShift3();
        return dlg.showModal(owner);
    }
    
    public static void main(String[] args) throws Exception {
        
        DataModule.open();
        
        TestShift3 dlg = new TestShift3();
        int result = dlg.showModal(null);
        System.out.println("RESULT : "+result);
        
        dlg.dispose();
        
        dlg=null;
    }
    
}
