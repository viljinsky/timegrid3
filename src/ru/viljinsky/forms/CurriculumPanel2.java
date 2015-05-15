package ru.viljinsky.forms;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import static ru.viljinsky.forms.IAppCommand.CREATE_CURRICULUM;
import static ru.viljinsky.forms.IAppCommand.CREATE_DEPART;
import static ru.viljinsky.forms.IAppCommand.DELETE_CURRICULUM;
import static ru.viljinsky.forms.IAppCommand.EDIT_CURRICULUM;
import static ru.viljinsky.forms.IAppCommand.EDIT_CURRICULUM_DETAIL;
import static ru.viljinsky.forms.IAppCommand.FILL_CURRICULUM;
import ru.viljinsky.sqlite.DataModule;
import ru.viljinsky.sqlite.Dataset;
import ru.viljinsky.sqlite.Grid;
import ru.viljinsky.sqlite.IDataset;
import ru.viljinsky.sqlite.Recordset;
import ru.viljinsky.sqlite.SelectDialog;
import ru.viljinsky.sqlite.Values;

/**
 *
 * @author вадик
 */




public class CurriculumPanel2 extends JPanel implements IAppCommand,IOpenedForm{

    @Override
    public String getCaption() {
        return CURRICULUM;
    }

    @Override
    public JComponent getPanel() {
        return this;
    }

    @Override
    public void close() throws Exception {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Перенесено и IOpenForm
     */
    class CurriculumnDetailDialg extends SelectDialog{
        public Integer skill_id;
        public Integer curriculum_id;

        @Override
        public void doOnEntry() throws Exception {
            Integer subject_id;
            try{
                for (Object n:getAdded()){
                    subject_id=(Integer)n;
                    DataTask.includeSubjectToCurriculumn(curriculum_id, skill_id,subject_id);
                }

                for (Object n:getRemoved()){
                    subject_id=(Integer)n;
                    DataTask.excludeSubjectFromCurriculumn(curriculum_id, skill_id, subject_id);
                }
                DataModule.commit();
            } catch (Exception e){
                DataModule.rollback();
                throw new Exception("FILL_CURRICULUM_ERROR\n"+e.getMessage());
            }
        }
    }
    
    
    Integer skill_id = null;
    Integer curriculum_id = null;
    Integer subject_id = null;
    
    CommandPanel commandPanel = new CommandPanel();
    
    public static final String MSG_GREATE_DEPART_OK = "Класс \"%s\" успешно создан";    

    private void doCommand(String command) {
        try{
            switch (command){
                case CREATE_CURRICULUM:
                      curriculum_id = tree.addCurriculim(new Curriculum());
                    break;
                    
                case EDIT_CURRICULUM:
//                    curriculum_id=(Integer)curriculumComboBox.getValue();
                    if (Dialogs.editCurriculum(this,curriculum_id)){
//                        curriculumComboBox.requery();
//                        curriculumComboBox.setValue(curriculum_id);
                    };
                    break;
                    
                case DELETE_CURRICULUM:
                    if (Dialogs.deleteCurriculum(this,curriculum_id)){
                        tree.deleteCurriculum();
                        curriculum_id=null;
                    }
                    break;
                    
                case FILL_CURRICULUM:
                        CurriculumnDetailDialg dlg = new CurriculumnDetailDialg();
                        dlg.curriculum_id=curriculum_id;
                        dlg.skill_id=skill_id;

                        IDataset ds = grid.getDataset();
                        Set<Object> set= ds.getColumnSet("subject_id");
                        Dataset dataDataset = DataModule.getSQLDataset("select id,subject_name from subject");
                        dlg.setDataset(dataDataset, "id", "subject_name");
                        dlg.setSelected(set);
                        if (dlg.showModal(this)==SelectDialog.RESULT_OK){
                            grid.requery();
                        }
                    break;
                    
                case EDIT_CURRICULUM_DETAIL:
                    if (Dialogs.editCurriculumDetail(this,curriculum_id,skill_id,subject_id))
                      grid.requery();
                    break;
                    
//                case CLEAR_CURRICULUM:
//                    clearCurriculumDetail();
//                    break;
                    
                case CREATE_DEPART:
                    Integer depart_id = Dialogs.createDepart(this, curriculum_id, skill_id);
                    if (depart_id!=null){
                        try {
                            DataTask.fillSubjectGroup2(depart_id);
                            DataModule.commit();
                            Recordset r=DataModule.getRecordet("select label from depart where id="+depart_id);
                            JOptionPane.showMessageDialog(this,String.format(MSG_GREATE_DEPART_OK,r.getString(0)));
                        } catch (Exception e){
                            DataModule.rollback();
                            throw new Exception("CREATE_DEPART_ERROR\n"+e.getMessage());
                        }
                    }
                    
//                    createDepart();
                    break;
//                case EDIT_CURRICULUM:
//                    editDetails();
//                    break;
                default:
                    System.out.println("ANKNOW_COMMAND:\n"+command);
            }  
            commandMng.updateActionList();
        } catch (Exception e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
//        System.out.println("DoCommand '"+command+"'");
    }
    
    class CommandPanel extends JPanel{

        public CommandPanel() {
            setLayout(new FlowLayout(FlowLayout.LEFT));
//            setPreferredSize(new Dimension(100, 20));
        }
        
        public void addCommand(Action action){
            JButton button = new JButton(action);
            add(button);
        }
        
    }
    
    CommandMngr commandMng = new CommandMngr() {

        @Override
        public void updateAction(Action a) {
            String command = (String)a.getValue(Action.ACTION_COMMAND_KEY);
            switch (command){
                case CREATE_CURRICULUM:
                    break;
                case EDIT_CURRICULUM:
                    a.setEnabled(curriculum_id!=null && skill_id==null);
                    break;
                case DELETE_CURRICULUM:
                    a.setEnabled(curriculum_id!=null && skill_id==null);
                    break;
                case FILL_CURRICULUM:
                    a.setEnabled(skill_id!=null && curriculum_id!=null);
                    break;
                case CREATE_DEPART:
                    a.setEnabled(skill_id!=null && curriculum_id!=null);
                    break;
                case EDIT_CURRICULUM_DETAIL:
                    a.setEnabled(subject_id!=null);
                    break;
                default:
                    System.out.println("ANKNOW_COMMAND:\n"+command);
            }
        }

        @Override
        public void doCommand(String command) {
            CurriculumPanel2.this.doCommand(command);
        }
    };
    
    class Curriculum{
        Integer curriculum_id;
        String caption;
        
        public Curriculum() throws Exception{
            try{
                Recordset r = DataModule.getRecordet("select max(id)+1 from curriculum");
                curriculum_id=r.getInteger(0);
                caption = "Учебнвый план "+curriculum_id;
                
                DataModule.execute("insert into curriculum (caption) values ('"+caption+"')");
                DataModule.commit();
            } catch (Exception e){
                DataModule.rollback();
                throw new Exception("ERROR_ON_CREATE_CURRICULUM\n"+e.getMessage());
            }
        }
        
        public Curriculum(Integer curiculum_id,String caption){
            this.curriculum_id=curiculum_id;
            this.caption=caption;
        }
        
        public String toString(){
            return caption;
        }
    }
    
    class Skill{
        Integer curriculum_id;
        Integer skill_id;
        String caption;
        public Skill(int curriculum_id,int skill_id,String caption){
            this.curriculum_id=curriculum_id;
            this.skill_id=skill_id;
            this.caption=caption;
        }
        
        public String toString(){
            return caption;
        }
    }
    
    abstract class CurriculumTree extends JTree{
        
        DefaultTreeModel model;
        DefaultMutableTreeNode root;
        Dataset skillList;
        
        public CurriculumTree(){
            addTreeSelectionListener(new TreeSelectionListener() {

                @Override
                public void valueChanged(TreeSelectionEvent e) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode)getLastSelectedPathComponent();
                    curriculum_id=null;
                    skill_id=null;
                    if (node!=null){
                        Object data = node.getUserObject();
                        if (data instanceof Skill){
                            curriculum_id=((Skill)data).curriculum_id;
                            skill_id=((Skill)data).skill_id;
                        } else if (data instanceof Curriculum){
                            curriculum_id=((Curriculum)data).curriculum_id;
                        }
                    }
                    skillChange();
                }
            });
       }
        
        
        public abstract void skillChange();
        
        
        public Integer addCurriculim(Curriculum curriculum) throws Exception{
            TreePath path = getSelectionPath();
            DefaultMutableTreeNode parent ;
            
            parent=(DefaultMutableTreeNode)model.getRoot();
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(curriculum);
            model.insertNodeInto(node, parent, parent.getChildCount());
            path = new TreePath(node.getPath());
            scrollPathToVisible(path);
            setSelectionPath(path);
            Values v;
            DefaultMutableTreeNode skillNode;
            Skill skill;
            for (int i=0;i<skillList.size();i++){
                v=skillList.getValues(i);
                System.out.println(curriculum);
                System.out.println(v);
                skill = new Skill(curriculum.curriculum_id,v.getInteger("id"),v.getString("caption"));
                skillNode = new DefaultMutableTreeNode(skill);
                model.insertNodeInto(skillNode, node, node.getChildCount());
            }
            
            return curriculum.curriculum_id;
        }
        
        private void deleteCurriculum() throws Exception{
            TreePath path = getSelectionPath();
            if (path!=null){
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
                DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();
                if (parent!=null){
                    model.removeNodeFromParent(node);
                    return;
                }
            }
            throw new Exception("CURRICULUM_IS_NULL");
        }
        
        public void open() throws Exception{
            DefaultMutableTreeNode node,skillNode;
            root = new DefaultMutableTreeNode("Учебный план");
            
            skillList = DataModule.getDataset("skill");
            skillList.open();
            
            Dataset dataset2 = DataModule.getDataset("curriculum");
            dataset2.open();
            Values v1,v2;
            Skill skill;
            Curriculum curriculum;
            for (int i=0;i<dataset2.size();i++){
                v2 = dataset2.getValues(i);
                curriculum=new Curriculum(v2.getInteger("id"), v2.getString("caption"));
                node = new DefaultMutableTreeNode(curriculum);
                
                for (int j=0;j<skillList.size();j++){
                    v1 = skillList.getValues(j);
                    skill = new Skill(curriculum.curriculum_id, v1.getInteger("id"),v1.getString("caption"));
                    skillNode = new DefaultMutableTreeNode(skill);
                    node.add(skillNode);
                }
                
                root.add(node);
            }
            model = new DefaultTreeModel(root);
            setModel(model);
        }

    }

    
    CurriculumTree tree = new CurriculumTree(){

        @Override
        public void skillChange() {
            System.out.println(skill_id+" "+curriculum_id);
            Map<String,Object> filter = new HashMap<>();
            
            if (skill_id!=null && curriculum_id!=null){
                filter.put("curriculum_id", curriculum_id);
                filter.put("skill_id",skill_id);
            } else {
                filter.put("curriculum_id", null);
                filter.put("skill_id",null);
            }
            System.out.println(filter);
            try{
                grid.setFilter(filter);            
                commandMng.updateActionList();
            } catch (Exception e){
                e.printStackTrace();
            }
            
        }
        
    };
    
    Grid grid = new Grid(){

        @Override
        public void gridSelectionChange() {
            int row = getSelectedRow();
            try{
            if (row<0){
                subject_id=null;
            } else {
                subject_id=getValues().getInteger("subject_id");
            }
            commandMng.updateActionList();
            System.out.println(subject_id);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    public CurriculumPanel2() {
        
        commandMng.setCommandList(CURRICULUM_COMMANDS);
        
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(800,600));
        initComponents();
    }
    
    public void initComponents(){
        JSplitPane splitPane,topSplit;
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        topSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        topSplit.setLeftComponent(new JScrollPane(tree));
        topSplit.setRightComponent(new JScrollPane(grid));
        topSplit.setDividerLocation(200);
        
        splitPane.setTopComponent(topSplit);
        splitPane.setBottomComponent(new JScrollPane(new JTextPane()));
        splitPane.setResizeWeight(.5);
        
        add(splitPane);
        add(commandPanel,BorderLayout.PAGE_START);
        for (Action a:commandMng.getActionList()){
            commandPanel.addCommand(a);
        }
        commandMng.updateActionList();
    }
    
    @Override
    public void open() throws Exception{
        Dataset dataset = DataModule.getDataset("v_curriculum_detail");
        grid.setDataset(dataset);
        tree.open();
        
    }
    
    public static void main(String[] args){
        
        CurriculumPanel2 panel = new CurriculumPanel2();
        
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.pack();
        frame.setVisible(true);
        try{
            DataModule.open();
            panel.open();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
}
