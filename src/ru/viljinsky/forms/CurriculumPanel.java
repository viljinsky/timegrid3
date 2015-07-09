package ru.viljinsky.forms;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import ru.viljinsky.sqlite.DataModule;
import ru.viljinsky.sqlite.Dataset;
import ru.viljinsky.sqlite.Grid;
import ru.viljinsky.sqlite.Recordset;
import ru.viljinsky.sqlite.Values;

/**
 *
 * @author вадик
 */

public class CurriculumPanel extends JPanel implements IAppCommand,IOpenedForm,CommandListener{
    
    Integer skill_id = null;
    Integer curriculum_id = null;
    Integer subject_id = null;
    Integer depart_id = null;
    
    public static final String MSG_GREATE_DEPART_OK = "Класс \"%s\" успешно создан";    
    
    CurriculumTree tree = new CurriculumTree();

    Grid grid = new curriculumGrid();

    CommandPanel commandPanel = new CommandPanel();
    
    
    public CurriculumPanel() {
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
        
        commandMng.setCommands(CURRICULUM_COMMANDS);
        commandMng.addCommandListener(this);
        for (Action a:commandMng.getActions()){
            commandPanel.addCommand(a);
        }
        grid.addExtAction(commandMng.getAction(EDIT_CURRICULUM_DETAIL));
        commandMng.updateActionList();
        
        tree.actions= commandMng.getActions(new String[]{
            CREATE_CURRICULUM,EDIT_CURRICULUM,DELETE_CURRICULUM,null,FILL_CURRICULUM,CREATE_DEPART});
    }
    

    @Override
    public String getCaption() {
        return CURRICULUM;
    }

    @Override
    public JComponent getPanel() {
        return this;
    }

    @Override
    public void open() throws Exception{
        grid.grid_id="v_curriculum_detail";
        Dataset dataset = DataModule.getDataset(grid.grid_id);
        
        grid.setDataset(dataset);
        tree.open();
        commandMng.updateActionList();
    }
    
    @Override
    public void close() throws Exception {
        tree.clear();
    }

    @Override
    public void updateAction(Action a){
            String command = (String)a.getValue(Action.ACTION_COMMAND_KEY);
            switch (command){
                case REFRESH:
                    a.setEnabled(DataModule.isActive());
                    break;
                case CREATE_CURRICULUM:
                    a.setEnabled(DataModule.isActive());
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
                case COPY_CURRICULUM:
                    a.setEnabled(skill_id!=null && curriculum_id!=null);
                    break;
                case CREATE_DEPART:
                    a.setEnabled(skill_id!=null && curriculum_id!=null);
                    break;
                case DELETE_DEPART:
                    a.setEnabled(depart_id!=null);
                    break;
                case EDIT_CURRICULUM_DETAIL:
                    a.setEnabled(subject_id!=null);
                    break;
                default:
                    System.out.println("UKNOW_COMMAND:\n"+command);
            }
    }
    
    @Override
    public void doCommand(String command) {
        try{
            switch (command){
                case CREATE_CURRICULUM:
                      curriculum_id = tree.addCurriculim(new Curriculum());
                    break;
                    
                case EDIT_CURRICULUM:
                    if (Dialogs.editCurriculum(this,curriculum_id)){
                        tree.editCurriculum(new Curriculum(curriculum_id,"XXX"));
                    };
                    break;
                    
                case DELETE_CURRICULUM:
                    if (Dialogs.deleteCurriculum(this,curriculum_id)){
                        tree.deleteCurriculum();
                        curriculum_id=null;
                    }
                    break;
                    
                case FILL_CURRICULUM:
                    if (Dialogs.fillCurriculumDetails(this, curriculum_id, skill_id))
                        grid.requery();
                    break;
                    
                case COPY_CURRICULUM:
                    if (Dialogs.copyCurriculum(this,curriculum_id,skill_id))
                        grid.requery();
                    break;
                    
                    
                case EDIT_CURRICULUM_DETAIL:
                    if (Dialogs.editCurriculumDetail(this,curriculum_id,skill_id,subject_id))
                      grid.requery();
                    break;
                    
                case CREATE_DEPART:
                    depart_id = Dialogs.createDepart(this, curriculum_id, skill_id);
                    if (depart_id!=null){
                        try {
                            DataTask.fillSubjectGroup2(depart_id);
                            DataModule.commit();
                            Recordset r=DataModule.getRecordet("select label from depart where id="+depart_id);
                            Depart depart  = new Depart(depart_id, r.getString(0));
                            tree.addDepart(depart);
                            
                            JOptionPane.showMessageDialog(this,String.format(MSG_GREATE_DEPART_OK,r.getString(0)));
                        } catch (Exception e){
                            DataModule.rollback();
                            throw new Exception("CREATE_DEPART_ERROR\n"+e.getMessage());
                        }
                    }
                    break;
                    
                case DELETE_DEPART:
                    if (Dialogs.deleteDepart(this, depart_id)==true){
                        tree.deleteDepart();
                    }
                    break;
                    
                default:
                    System.out.println("UNKNOW_COMMAND:\n"+command);
            }  
            
        } catch (Exception e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
    
    class CommandPanel extends JPanel{

        public CommandPanel() {
            setLayout(new FlowLayout(FlowLayout.LEFT));
        }
        
        public void addCommand(Action action){
            JButton button = new JButton(action);
            button.setToolTipText((String)action.getValue(Action.SHORT_DESCRIPTION));
            add(button);
        }
        
    }
    
    CommandMngr commandMng = new CommandMngr(); 
    
    //            классы дерева  
    class Curriculum{
        Integer curriculum_id;
        String caption;
        
        public Curriculum() throws Exception{
            try{
                Recordset r = DataModule.getRecordet("select max(id)+1 from curriculum");
                curriculum_id=r.getInteger(0);
                caption = "Учебный план ("+curriculum_id+")";
                
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
        
        @Override
        public String toString(){
            return caption;
        }
    }
    
    class Depart{
        Integer depart_id;
        String label;
        public Depart(Integer depart_id,String label){
            this.depart_id=depart_id;
            this.label=label;
        }
        
        @Override
        public String toString(){
          return label;  
        }
    }
    
    class MyTreeModelListener implements TreeModelListener{

        @Override
        public void treeNodesChanged(TreeModelEvent e) {
            DefaultMutableTreeNode node;
            node = (DefaultMutableTreeNode)e.getTreePath().getLastPathComponent();
            int index = e.getChildIndices()[0];
            node = (DefaultMutableTreeNode)node.getChildAt(index);
            System.out.println(node);
            try{
                if (curriculum_id!=null){
                    try{
                        DataModule.execute("update curriculum set caption='"+node.toString()+"' where id="+curriculum_id);
                        DataModule.commit();
                    } catch (Exception ee){
                        DataModule.rollback();
                        ee.printStackTrace();
                    }
                }
            } catch (SQLException r){
            }
        }

        @Override
        public void treeNodesInserted(TreeModelEvent e) {
            System.out.println("treeNodesInserted");
        }

        @Override
        public void treeNodesRemoved(TreeModelEvent e) {
            System.out.println("treeNodesRemoved");
        }

        @Override
        public void treeStructureChanged(TreeModelEvent e) {
            System.out.println("treeStructureChanged");
        }
    }
    
    class CurriculumTree extends JTree{
        
        DefaultTreeModel model;
        DefaultMutableTreeNode root;
        Dataset skillList;
        Dataset departList;
        Action[] actions = {};
        
        public void clear(){
            root = new DefaultMutableTreeNode("Учебный план");
            model = new DefaultTreeModel(root);
            setModel(model);
        }
        
        
        public CurriculumTree(){
            clear();
            
            setEditable(true);
            getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            addTreeSelectionListener(new TreeSelectionListener() {

                @Override
                public void valueChanged(TreeSelectionEvent e) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode)getLastSelectedPathComponent();
                    curriculum_id=null;
                    skill_id=null;
                    depart_id = null;
                    if (node!=null){
                        Object data = node.getUserObject();
                        if (data instanceof Skill){
                            curriculum_id=((Skill)data).curriculum_id;
                            skill_id=((Skill)data).skill_id;
                        } else if (data instanceof Curriculum){
                            curriculum_id=((Curriculum)data).curriculum_id;
                        } else if (data instanceof Depart){
                            depart_id=((Depart)data).depart_id;
                        }
                    }
                    skillChange();
                }
            });
            
            addMouseListener(new MouseAdapter() {

                @Override
                public void mouseReleased(MouseEvent e) {
                    showPopup(e);
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    showPopup(e);
                }
                
                protected void showPopup(MouseEvent e){
                    if (e.isPopupTrigger()){
                        getPopupMenu().show(CurriculumTree.this, e.getX(), e.getY());
                    }
                        
                }
                
            });
       }
        
        
        public JPopupMenu getPopupMenu(){
            JPopupMenu result = new JPopupMenu();
            for (Action a:actions){
                if (a==null)
                    result.addSeparator();
                else
                    result.add(a);
            }
            return result;
        }
        
        
        public void skillChange(){
            Map<String,Object> filter = new HashMap<>();
            
            if (skill_id!=null && curriculum_id!=null){
                filter.put("curriculum_id", curriculum_id);
                filter.put("skill_id",skill_id);
            } else {
                filter.put("curriculum_id", null);
                filter.put("skill_id",null);
            }
            
            try{
                grid.setFilter(filter);            
                commandMng.updateActionList();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        
        public void editCurriculum(Curriculum curriculum) throws Exception{
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)getSelectionPath().getLastPathComponent();
            node.setUserObject(curriculum);
            model.nodeChanged(node);
//            model.nodeChanged(new DefaultMutableTreeNode(curriculum));
        }
        
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
        
        public Integer addDepart(Depart depart) throws Exception{
            TreePath path = getSelectionPath();
            if (path==null)
                throw new Exception("NODE_PATH_IS_NULL");
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode)path.getLastPathComponent();
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(depart);
            model.insertNodeInto(node,parent, parent.getChildCount());
            path = new TreePath(node.getPath());
//            setSelectionPath(path);
            scrollPathToVisible(path);
            return null;
        }
        
        public Boolean deleteDepart() throws Exception{
            DefaultMutableTreeNode node,parent;
            TreePath path = getSelectionPath();
            if (path!=null){
                node = (DefaultMutableTreeNode)path.getLastPathComponent();
                parent = (DefaultMutableTreeNode)node.getParent();
                if (parent!=null){
                    model.removeNodeFromParent(node);
                }
                
            }
            return false;
        }
        
        public void open() throws Exception{
            DefaultMutableTreeNode node,skillNode,departNode;
            root.removeAllChildren();
            
            skillList = DataModule.getSQLDataset("select * from skill order by sort_order");
            skillList.open();
            
            departList = DataModule.getDataset("depart");
            
            Dataset curriculumList = DataModule.getDataset("curriculum");
            curriculumList.open();
            Values v1,v2,v3,filter;
            Skill skill;
            Curriculum curriculum;
            filter = new Values();
            for (int i=0;i<curriculumList.size();i++){
                v2 = curriculumList.getValues(i);
                curriculum=new Curriculum(v2.getInteger("id"), v2.getString("caption"));
                node = new DefaultMutableTreeNode(curriculum);
                
                for (int j=0;j<skillList.size();j++){
                    v1 = skillList.getValues(j);
                    skill = new Skill(curriculum.curriculum_id, v1.getInteger("id"),v1.getString("caption"));
                    skillNode = new DefaultMutableTreeNode(skill);
                    node.add(skillNode);
                    filter.put("skill_id",v1.getInteger("id") );
                    filter.put("curriculum_id",v2.getInteger("id"));
                    departList.open(filter);
                    for (int k=0;k<departList.size();k++){
                        v3=departList.getValues(k);
                        departNode = new DefaultMutableTreeNode(new Depart(v3.getInteger("id"),v3.getString("label")));
                        skillNode.add(departNode);
                    }
                }
                
                root.add(node);
            }
            model = new DefaultTreeModel(root);
            model.addTreeModelListener(new MyTreeModelListener());
            setModel(model);
        }

    }

    
    
    class curriculumGrid extends Grid{
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
    }
    

    
    public static void main(String[] args){
        
        CurriculumPanel panel = new CurriculumPanel();
        
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
