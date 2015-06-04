/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.awt.BorderLayout;
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
import ru.viljinsky.dialogs.BaseDialog;
import ru.viljinsky.sqlite.DataModule;
import ru.viljinsky.sqlite.Dataset;
import ru.viljinsky.sqlite.Grid;
import ru.viljinsky.sqlite.Values;

/**
 *
 * @author вадик
 */

abstract class AbstractPanel extends JPanel implements CommandListener{
    String[] SHIFT_ACTIONS = {"ADD","EDIT","DELET","POST","CANCEL"};
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
        Grid subjectList = new Grid();
        Grid domainList = new Grid(){

            @Override
            public void gridSelectionChange() {
                Values values = getValues();
                try{
                    subjectList.setFilter(new Values("subject_domain_id", values.getInteger("id")));
                } catch (Exception e){
                    e.printStackTrace();
                }
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
            
            dataset = DataModule.getDataset("subject");
            subjectList.setDataset(dataset);
            dataset.open();
            setCommands(SUBJECT_ACTIONS);
        }

        @Override
        public void doCommand(String command) {
        }

        @Override
        public void updateAction(Action action) {
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
