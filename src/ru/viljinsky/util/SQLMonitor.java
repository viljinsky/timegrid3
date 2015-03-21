/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.util;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import ru.viljinsky.Column;
import ru.viljinsky.DataModule;
import ru.viljinsky.Dataset;
import ru.viljinsky.DatasetInfo;
import ru.viljinsky.Grid;
import ru.viljinsky.IDataModule;

/**
 *
 * @author вадик
 */
public class SQLMonitor extends JFrame{

    DataModule dataModule = DataModule.getInstance();
    SQLTree tree = new SQLTree();
    SQLPanel sqlPanel = new SQLPanel();
    
    public SQLMonitor(){
        super("SQLMonitor");
        
        JSplitPane splitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane1.setLeftComponent(tree);
        splitPane1.setRightComponent(sqlPanel);
        splitPane1.setDividerLocation(200);
        setContentPane(splitPane1);
        
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(sqlPanel.createMenu());
        menuBar.add(tree.createMenu());
        setJMenuBar(menuBar);
        
    }

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

    //************************************************************************//

    class SQLTree extends JTree{
        Action[] actions = {new Act("addSQL"),new Act("act2"),new Act("act3"),new Act("act4")};

        public SQLTree(){

        addTreeSelectionListener(new TreeSelectionListener() {

                @Override
                public void valueChanged(TreeSelectionEvent e) {

                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) getLastSelectedPathComponent();
                    if (node!=null){
                        Object d = node.getUserObject();
                        if (d instanceof DatasetInfo){
                            DatasetInfo info = (DatasetInfo)d;
                            datasetInfoClick(info);
                        } else if ( d instanceof Column){
                            columnClick((Column)d);
                        }
                    }
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
                        showPopup1(e.getX(),e.getY());
                    }
                }

            });

        }

        public JMenu createMenu(){
            JMenu result = new JMenu("Tree");
            for (Action a:actions){
                result.add(a);
            }
            return result;        
        }
        
        public  void showPopup1(Integer x,Integer y){
            JPopupMenu popupMenu = new JPopupMenu();
            for (Action a:actions){
                popupMenu.add(a);
            }
            popupMenu.show(this, x, y);
        }

        public void datasetInfoClick(DatasetInfo info){
            System.out.println(info.getTableName()+" "+info.getTableType());
        }

        public void columnClick(Column column){
            System.out.println(column);
        }

        public void open(){
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("База данных");
            DefaultMutableTreeNode node,columnNode;
            Dataset dataset ;

            for (DatasetInfo info:dataModule.getInfoList()){
                node= new DefaultMutableTreeNode(info);
                root.add(node);
                try{
                    dataset = dataModule.getDataset(info.getTableName());
                    dataset.test();
                    for (Column column:dataset.getColumns()){
                        columnNode = new DefaultMutableTreeNode(column);
                        node.add(columnNode);
                    }
                } catch (Exception e){
                }

            }

            DefaultTreeModel  model = new DefaultTreeModel(root);
            setModel(model);
        }
        
        public DatasetInfo getDelectedDataset(){
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)getLastSelectedPathComponent();
            Object userObject = node.getUserObject();
            if (userObject!=null){
                if (userObject instanceof DatasetInfo)
                    return (DatasetInfo)userObject;
            }
            return null;
        }


    }

    //************************************************************************//

    class SQLPanel extends JSplitPane{
        JTextArea textEditor = new JTextArea();
        JTabbedPane tabs = new JTabbedPane();
        Action[] actions = {new Act("execute"),new Act("executeAll"),new Act("clear")};
        
        public JMenu createMenu(){
            JMenu result = new JMenu("Sql");
            for (Action a:actions){
                result.add(a);
            }
            return result;
        }

        public void popup(int x,int y){
            JPopupMenu popupMenu = new JPopupMenu();
            for (Action a:actions)
                popupMenu.add(a);
            popupMenu.show(textEditor, x, y);
        }
        

        public SQLPanel(){
            super(JSplitPane.VERTICAL_SPLIT);
            setPreferredSize(new Dimension(800,600));
            setTopComponent(textEditor);
            setBottomComponent(tabs);
            setResizeWeight(0.5);
            textEditor.addKeyListener(new KeyAdapter() {

                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.isControlDown()){
                        switch (e.getKeyCode()){
                            case KeyEvent.VK_E:
                                execute();
                                break;
                        }
                    }
                }
            });
            
            textEditor.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseReleased(MouseEvent e) {
                    showPopup(e);
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    System.out.println("OK");
                    showPopup(e);
                }
                
                public void showPopup(MouseEvent e){
                    if (e.isPopupTrigger()){
                        popup(e.getX(), e.getY());
                    }
                }
                
            });
        }
        

        public void executeSql(String sql){
            System.out.println(sql);
            if (sql.startsWith("select")){
                System.out.println("SELECT");
                try{
                    Dataset dataset = dataModule.getSQLDataset(sql);
                    dataset.open();
                    Grid grid = new Grid();
                    grid.setDataset(dataset);
                    tabs.addTab("sql", new JScrollPane(grid));
                    tabs.setSelectedIndex(tabs.getTabCount()-1);
                } catch(Exception e){
                    JOptionPane.showMessageDialog(null, e.getMessage());
                }


            } else {
                try{
                    dataModule.execute(sql);
                } catch (Exception e){
                    JOptionPane.showMessageDialog(null, e.getMessage());
                }
            }
        }

        public void execute(){
            tabs.removeAll();

            String sql = "";
            String[] lines = textEditor.getText().split("\n");
            for (String line:lines){
                if (line.isEmpty())
                    continue;

                if (line.trim().startsWith("--"))
                    continue;

                sql+=line.trim()+"\n";
                if (line.endsWith(";"))
                    executeSql(sql);
                    sql="";
            }

        }

        public void executeAll(){
        }

        public void clear(){
        }





        public void addSQL(){
            DatasetInfo info = tree.getDelectedDataset();
            if (info!=null){
                sqlPanel.textEditor.append("select * from "+info.getTableName()+";\n");
            }
        }
    
    }
    
    //--------------------------------------------------------------------------
    
    public void doCommand(String command){
        try{
            switch(command){
                case "clear":
                    sqlPanel.clear();
                    break;
                case "execute":
                    sqlPanel.execute();
                    break;
                case "executeAll":
                    sqlPanel.executeAll();
                    break;
                case "addSQL":
                    sqlPanel.addSQL();
                    break;
            }
        } catch (Exception e){
            JOptionPane.showMessageDialog(rootPane, e.getMessage());
        }
    }
    
    public void open(){
        tree.open();
    }
    
    //-------------------------------------------------------------------------
    public static void main(String[] args) throws Exception{
        DataModule.getInstance().open();
        SQLMonitor frame = new SQLMonitor();
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.open();
    }
}
