/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import ru.viljinsky.Column;
import ru.viljinsky.DataModule;
import ru.viljinsky.Dataset;
import ru.viljinsky.DatasetInfo;
import ru.viljinsky.Grid;

/**
 *
 * @author вадик
 */
public class SQLMonitor extends JFrame implements MenuConstants{

    DataModule dataModule = DataModule.getInstance();
    SQLTree tree = new SQLTree();
    SQLPanel sqlPanel = new SQLPanel();
    JTabbedPane panels = new JTabbedPane();
    JFileChooser fileChooser;
    
    
    
    public SQLMonitor(){
        super("SQLMonitor");
        
        fileChooser = new JFileChooser(new File("."));
        
        fileChooser.addChoosableFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) {
                if (f.isDirectory())
                    return true;
                else {
                    String path = f.getName();
                    String ext = null;
                    int i = path.lastIndexOf(".");
                    if (i>0 && i<path.length()-1){
                        ext = path.substring(i+1).toLowerCase();
                    }
                    return (ext!=null) && (ext.equals("txt") || ext.equals("sql"));
                
                    
                    
                }
            }

            @Override
            public String getDescription() {
                return "SQL text (*.sql,*.txt)";
            }
        });
        
        panels.addTab("new",sqlPanel);
        
        JSplitPane splitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane1.setLeftComponent(tree);
        splitPane1.setRightComponent(panels);
        splitPane1.setDividerLocation(200);
        setContentPane(splitPane1);
        
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(tree.createMenu());
        menuBar.add(sqlPanel.createMenu());
        setJMenuBar(menuBar);
        
        panels.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
//                System.out.println(panels.getSelectedIndex());
//                System.out.println(panels.getSelectedComponent().getClass().getName());
                sqlPanel = (SQLPanel)panels.getSelectedComponent();
            }
        });
        
    }
    
    public JMenu createFileMenu(){
        JMenu menu = new JMenu("File");
        menu.add(new Act(fileNew));
        menu.add(new Act(fileOpen));
        menu.add(new Act(fileSave));
        menu.addSeparator();
        menu.add(new Act(exit));
        return menu;
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
        Action[] actions = {
            new Act(treeCreateSQL),
            new Act(treeRefresh)
        };

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
                        MyByshow(e);
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        MyByshow(e);
                    }

                    protected void MyByshow(MouseEvent e){
                        if (e.isPopupTrigger()){
                            showPopup(e.getX(),e.getY());
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
        
        public  void showPopup(Integer x,Integer y){
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

        public DatasetInfo getSelectedDataset(){
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)getLastSelectedPathComponent();
            Object userObject = node.getUserObject();
            if (userObject!=null){
                if (userObject instanceof DatasetInfo)
                    return (DatasetInfo)userObject;
            }
            return null;
        }
        
        public void fill(){
            removeAll();
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


    }

    //************************************************************************//

    class SQLPanel extends JSplitPane{
        JTextArea textEditor = new JTextArea();
        JTabbedPane tabs = new JTabbedPane();
        Action[] actions = {
            new Act(sqlRun),
            new Act(sqlRunAll),
            new Act(sqlClear),
//            null,
//            new Act(fileNew),
//            new Act(fileOpen),
//            new Act(fileSave)
                
                
        };
        
        public JMenu createMenu(){
            JMenu result = new JMenu("Script");
            for (Action a:actions){
                if (a==null)
                    result.addSeparator();
                else
                    result.add(a);
            }
            return result;
        }

        public void popup(int x,int y){
            JPopupMenu popupMenu = new JPopupMenu();
            for (Action a:actions)
                if (a==null)
                    popupMenu.addSeparator();
                else
                    popupMenu.add(a);
            popupMenu.show(textEditor, x, y);
        }
        

        public SQLPanel(){
            super(JSplitPane.VERTICAL_SPLIT);
            setPreferredSize(new Dimension(800,600));
            JPanel p = new JPanel(new BorderLayout());
            p.add(textEditor,BorderLayout.CENTER);
            p.add(new JLabel("  "),BorderLayout.WEST);
            textEditor.setFont(new Font("Monospaced", Font.PLAIN, 13));
            setTopComponent(new JScrollPane(p));
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

 ///////////////////////////////////////////////////////////////////////////////

        synchronized  public void executeSql(String sql){
            System.out.print("EXECUTE:\n'"+sql+"'....");
            try{
            if (sql.startsWith("select")){
//                System.out.println("SELECT");
                
                    Dataset dataset = dataModule.getSQLDataset(sql);
                    dataset.open();
                    Grid grid = new Grid();
                    grid.setDataset(dataset);
                    tabs.addTab("sql", new JScrollPane(grid));
                    tabs.setSelectedIndex(tabs.getTabCount()-1);


            } else {
                    dataModule.execute(sql);
            }
                System.out.println("OK");
            } catch (Exception e){
                System.out.println("ERROR:\n"+e.getMessage());
                
            }
        }

        public void execute(){
            String sql = "";
            String[] lines;
            int pos=0,lineNumber = 0;
            try {
                pos = textEditor.getCaretPosition();
                lineNumber = textEditor.getLineOfOffset(pos);
            } catch (Exception e){}
            
            lines=textEditor.getText().split("\n");
            System.out.println(lines[lineNumber]);
            for (int i=lineNumber;i<lines.length;i++){
                if (lines[i].trim().isEmpty())
                    continue;
                sql+=lines[i]+"\n";
                if (lines[i].trim().endsWith(";"))
                    break;
                
            }
            if (!sql.isEmpty()){
                    tabs.removeAll();
                    executeSql(sql);
            }
            
        }

        public void executeAll(){
            String[] lines;
            String sql="";
            tabs.removeAll();

            lines = textEditor.getText().split("\n");
            for (String line:lines){
                if (line.isEmpty())
                    continue;

                if (line.trim().startsWith("--"))
                    continue;

                sql+=line.trim()+"\n";
                if (line.endsWith(";")){
                    executeSql(sql);
                    sql="";
                }
            }
        }

        public void clear(){
        }

        public void addSQL(){
            DatasetInfo info = tree.getSelectedDataset();
            if (info!=null){
                sqlPanel.textEditor.append("select * from "+info.getTableName()+";\n");
                
            }
        }
    
    }
    
    public void fileOpen() throws Exception{
        SQLPanel newPanel = new SQLPanel();
        
        int retVal = fileChooser.showOpenDialog(rootPane);
        if (retVal==JFileChooser.APPROVE_OPTION){
            BufferedReader br = null;
            try{
                newPanel.textEditor.setText("");
                File f = fileChooser.getSelectedFile();
                br = new BufferedReader(new FileReader(f));
                String line;
                    while ((line=br.readLine())!=null){
                        newPanel.textEditor.append(line+"\n");
                    }
                 newPanel.textEditor.setCaretPosition(0);
                 
                 panels.addTab(f.getName(), newPanel);
                 panels.setSelectedIndex(panels.getTabCount()-1);
//                 sqlPanel=newPanel;
                 
            } catch (Exception e){
                throw new Exception("Ошибка сохранения \n"+e.getMessage());
            } finally {
                if (br!=null) br.close();
                
            }
        }
    }
    
    public void fileSave() throws Exception{
        int retVal = fileChooser.showSaveDialog(rootPane);
        if (retVal==JFileChooser.APPROVE_OPTION){
            File file = fileChooser.getSelectedFile();
            if (!file.exists()){
                BufferedWriter br = null;
                try{
                    br = new BufferedWriter(new FileWriter(file));
                    String text = sqlPanel.textEditor.getText();
                    br.write(text);
                    
                } catch (IOException ioe){
                    ioe.printStackTrace();
                    throw new Exception("Ошибка при сохранени");
                } finally {
                    if (br!=null) br.close();
                } 
            } else {
                throw new Exception("file exists");
            }
        }
    }
    
    public void fileNew(){
        panels.addTab("new", new SQLPanel());
        panels.setSelectedIndex(panels.getTabCount()-1);
    }
    //--------------------------------------------------------------------------
    
    public void doCommand(String command){
        try{
            switch(command){
                case fileNew:
                    fileNew();
                    break;
                case fileOpen:
                    fileOpen();
                    break;
                case fileSave:
                    fileSave();
                    break;
                    
                case exit:
                    System.exit(0);
                    break;
                case  sqlClear :
                    sqlPanel.clear();
                    break;
                case sqlRun:
                    sqlPanel.execute();
                    break;
                case sqlRunAll:
                    sqlPanel.executeAll();
                    break;
                    
                case treeCreateSQL:
                    sqlPanel.addSQL();
                    break;
                case treeRefresh:
                    tree.fill();
            }
        } catch (Exception e){
            JOptionPane.showMessageDialog(rootPane, e.getMessage());
        }
    }
    
    public void open(){
        tree.fill();
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

interface MenuConstants{
    
    final String dataConnect    ="connect";
    final String dataDisconnect ="disconnect";
    
    final String fileOpen       = "open";
    final String fileSave       = "save";
    final String fileNew        = "new";
    
    final String exit           = "Exit";
    
    final String sqlRun         = "run";
    final String sqlRunAll      = "run all";
    final String sqlClear       = "clear";
    
    final String treeCreateSQL  = "create sql";
    final String treeRefresh    = "refresh";
}
