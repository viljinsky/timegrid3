/**
 *   SQLite Monitor
 * 
 * @author v.iljinsky  
 */

package ru.viljinsky.util;

import java.awt.BorderLayout;
import java.awt.Cursor;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
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
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import ru.viljinsky.sqlite.Column;
import ru.viljinsky.sqlite.DataModule;
import ru.viljinsky.sqlite.Dataset;
import ru.viljinsky.sqlite.DatasetInfo;
import ru.viljinsky.sqlite.Grid;

/**
 *
 * @author вадик
 */


abstract class SQLEditor extends JPanel{
    JTextArea text = new JTextArea();
    JTabbedPane tabbs = new JTabbedPane();
    Action[] actions = {};
    File sourceFile=null;
    
    public JPopupMenu getPopupMenu(){
        JPopupMenu popupMenu = new JPopupMenu();
        for (Action a:actions){
            if (a==null)
                popupMenu.addSeparator();
            else
                popupMenu.add(a);
        }
        return popupMenu;
    }
    
    public void showPopup(){
        getPopupMenu().show(text, 0, 0);
    }
    public void loadFromFile(File file) throws Exception{
        BufferedReader br = null;
        try{
            br = new BufferedReader(new FileReader(file));
            String line;
            while ((line=br.readLine())!=null){
                text.append(line+"\n");
            }
            sourceFile = file;
        } finally{
            if (br!=null)
                br.close();
        }
    };
    
    public void saveFile() throws Exception{
        BufferedWriter bw = null;
        if (sourceFile!=null)
        try{
            bw=new BufferedWriter(new FileWriter(sourceFile));
            bw.write(text.getText());
        } finally{
            if (bw!=null)
                bw.close();
        }
    }
    
    public void saveFileAs(File file) throws Exception{
        BufferedWriter bw = null;
        try{
            bw=new BufferedWriter(new FileWriter(file));
            bw.write(text.getText());
            sourceFile=file;
        } finally{
            if (bw!=null)
                bw.close();
        }
    }
    public SQLEditor(){
        setLayout(new BorderLayout());
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(new JScrollPane(new JScrollPane(text)));
        splitPane.setBottomComponent(tabbs);
        splitPane.setResizeWeight(0.5);
        add(splitPane);
        Action[] aList = text.getActions();
        for (Action a:aList){
            System.out.println(a.getValue(Action.NAME));
        }
        text.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        text.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                showPopup(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                showPopup(e);
            }
            
            private void showPopup(MouseEvent e){
                if (e.isPopupTrigger()){
                    getPopupMenu().show(text, e.getX(),e.getY());
                }
            }
        });
        
        
        text.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode()==KeyEvent.VK_CONTEXT_MENU){
                    showPopup();
                    return;
                }
                
                if (e.isControlDown()){
                    try{
                        switch (e.getKeyCode()){
                            case KeyEvent.VK_ENTER:
                                doExecuteAll();
                                break;
                            case KeyEvent.VK_E:
                                doExecute();
                                break;
                            case KeyEvent.VK_N:
                                doNewTab();
                                break;
                            case KeyEvent.VK_S:
                                doSave();
                                break;
                            case KeyEvent.VK_X:
                                doClose();
                                break;
                        }
                    } catch (Exception r){
                        r.printStackTrace();
                        JOptionPane.showMessageDialog(SQLEditor.this, r.getMessage());
                    }
                }
            }
            
        });
    }
   
    public String getSelectedSQL() throws Exception{
        String result = "";
        String s;
        int start=0;
        int lineStart,lineEnd;
        int carretPosition = text.getCaretPosition();
        for (int line=0;line<text.getLineCount();line++){
            lineStart = text.getLineStartOffset(line);
            lineEnd = text.getLineEndOffset(line);
            s = text.getText(lineStart,lineEnd-lineStart);
            if(!s.trim().startsWith("--")){
            
                result+=s;
                if (s.trim().endsWith(";") && carretPosition>=start && carretPosition<lineEnd)
                    return result;

                if (s.trim().endsWith(";")){
                    result="";
                    start = lineStart;
                }
            }
        }
        return result;
    }
    
    public String[] getSQLList(){
        List<String> result = new ArrayList<>();
        
        int lineCount = text.getLineCount();
        int lineStart;
        int lineEnd;
        String sql = "";
        for (int i=0;i<lineCount;i++){
            try{
                lineStart = text.getLineStartOffset(i);
                lineEnd   = text.getLineEndOffset(i);
                String s  = text.getText(lineStart,lineEnd-lineStart);
                if (!s.trim().isEmpty() && !s.trim().startsWith("--")){
                    sql+=s;
                    if (s.trim().endsWith(";")){
                        result.add(sql);
                        sql="";
                    }
                }
            } catch (Exception  e){
            }
        }
        if (!sql.isEmpty()){
            result.add(sql);
        }
        return result.toArray(new String[result.size()]);
    }
    
    public void addGrid(Dataset dataset) throws Exception{
        addGrid("grid", dataset);
    }
    
    public void addGrid(String gridName,Dataset dataset) throws Exception{
        Grid grid = new Grid();
        grid.setDataset(dataset);
        tabbs.add(gridName,new JScrollPane(grid));        
    }    
    public abstract void doExecuteAll() throws Exception;
    
    public abstract void doExecute() throws Exception;
    
    public abstract void doNewTab();
    
    public abstract void doClose() ;
    
    public void doSave(){
        System.out.println("SAVE");
    }
    
    public void doLoad(){
    }
}

abstract class DBTree extends JTree{
    DefaultMutableTreeNode root,tableNode,viewNode;
    Action[] actions={};
    
    public DBTree(){
        root= new DefaultMutableTreeNode("База данных");
        tableNode = new DefaultMutableTreeNode("Таблицы");
        viewNode = new DefaultMutableTreeNode("Представления");
        root.add(tableNode);
        root.add(viewNode);
        addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)getLastSelectedPathComponent();
                if (node!=null){
                    Object obj = node.getUserObject();
                    if (obj instanceof DatasetInfo){
                        DatasetInfo info = (DatasetInfo)obj;
//                        infoSelect(info);
                    }
                }
            }
        });
        
        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode()==KeyEvent.VK_CONTEXT_MENU){
                    JPopupMenu p = getPopupMenu();
                    p.show(DBTree.this, 0, 0);
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
            
            public void showPopup(MouseEvent e){
                if (e.isPopupTrigger()){
                        JPopupMenu p = getPopupMenu();
                        p.show(DBTree.this, e.getX(), e.getY());
                    }
                }
            
        });
    }
    
    public JPopupMenu getPopupMenu(){
        JPopupMenu popupMenu =new JPopupMenu();
        for (Action a:actions)
            if (a==null)
                popupMenu.addSeparator();
            else
                popupMenu.add(a);
        return popupMenu;
    }
    
    public DatasetInfo getSelectedInfo(){
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)getLastSelectedPathComponent();
        if (node!=null){
            Object obj = node.getUserObject();
            if (obj instanceof DatasetInfo)
                return (DatasetInfo)obj;
        }
        return null;
    }
    
    public void open() throws Exception{
        DefaultMutableTreeNode node;
        tableNode.removeAllChildren();
        viewNode.removeAllChildren();
        Dataset dataset;
        
        Collections.sort(DataModule.getInfoList());
        
        for (DatasetInfo info:DataModule.getInfoList()){
            node = new DefaultMutableTreeNode(info);
            if (info.isTable()){
                tableNode.add(node);
            }  else {
                viewNode.add(node);
            }
            dataset = DataModule.getDataset(info.getTableName());
            dataset.test();
            for (Column column:dataset.getColumns()){
                 node.add(new DefaultMutableTreeNode(column));
            }
        }
        
        DefaultTreeModel model = new DefaultTreeModel(root);
        setModel(model);
    }
}


public class SQLMonitor2 extends JPanel{
    JLabel statusLabel = new JLabel("status");
    Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);
    Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
    
    FileFilter sqlFileFilter = new FileFilter() {

        @Override
        public boolean accept(File f) {
            return f.isDirectory() || f.getPath().endsWith(".sql");
        }

        @Override
        public String getDescription() {
            return "Запроы SQL";
        }
    };
    
    FileFilter dataFilter = new FileFilter() {

        @Override
        public boolean accept(File f) {
            return f.isDirectory() || f.getPath().endsWith(".db");
        }

        @Override
        public String getDescription() {
            return "База данных";
        }
    };
    
    public static final String APP_TITLE = "SQLite Monitor";
    
    public static final String ERROR_FILE_EXISTS ="FILE EXISTS";
    
    public static final String MN_NEW_DATA    = "New";
    public static final String MN_OPEN_DATA   = "Open";
    public static final String MN_EXIT        = "Exit";
    
    public static final String MN_CRATE_SQL     = "CreateSQL";
    public static final String MN_SELECT_SQL    = "SelectSQL";
    public static final String MN_INSER_SQL     = "InsertSQL";
    public static final String MN_UPDATE_SQL    = "UpdateSQL";
    public static final String MN_SELECT        = "Select";
    public static final String MN_REFRESH_TREE  = "RefreshTree";
    
    public static final String MN_EXECUTE_ALL   = "ExecuteAll";
    public static final String MN_EXECUTE       = "Execute";
    
    public static final String MN_NEW_EDITOR    = "New editor";
    public static final String MN_CLOSE_EDITOR  = "Close";
    public static final String MN_LOAD_SCRIPT   = "Load";
    public static final String MN_SAVE_AS_SCRIPT = "Save as";
    public static final String MN_SAVE_SCRIPT   = "Save";
    
    public static final String MN_COPY          = "Copy";
    public static final String MN_PASTE         = "Paste";
    public static final String MN_CUT           = "Cut";
    
    
    Action[] dataActions = {
        new Act(MN_NEW_DATA),
        new Act(MN_OPEN_DATA),
        null,
        new Act(MN_EXIT)};
    Action[] treeActions = {
        new Act(MN_SELECT),
        new Act(MN_CRATE_SQL),
        new Act(MN_SELECT_SQL),
        new Act(MN_INSER_SQL),
        new Act(MN_UPDATE_SQL),
        null,
        new Act(MN_REFRESH_TREE)};
    Action[] textAction = {
        new Act(MN_LOAD_SCRIPT),
        null,
        new Act(MN_EXECUTE),
        new Act(MN_EXECUTE_ALL),
        null,
        new Act(MN_SAVE_AS_SCRIPT),
        new Act(MN_CLOSE_EDITOR)};
    
    
    DBTree tree;
    JTabbedPane tabbed = new JTabbedPane();
    
    JFileChooser fileChooser = new JFileChooser(new File("."));
    
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
    
    public void wait(Boolean showWaint){
        if (showWaint)
            setCursor(waitCursor);
        else
            setCursor(defaultCursor);
    }
    
    public void doCommand(String command){
        int retVal;
        String fileName;
        File file;
        DatasetInfo info = tree.getSelectedInfo();
        SQLEditor editor = null;
        if (tabbed.getSelectedIndex()>=0){
            editor=(SQLEditor)tabbed.getSelectedComponent();
        }
        
        try{
            switch(command){
                
                case MN_NEW_DATA:
                    fileChooser.setSelectedFile(null);
                    fileChooser.setFileFilter(dataFilter);
                    retVal = fileChooser.showOpenDialog(this);
                    if (retVal==JFileChooser.APPROVE_OPTION){
                        file = fileChooser.getSelectedFile();
                        if (file.exists()){
                            throw new Exception(ERROR_FILE_EXISTS+"\n"+file.getName());
                        }
                        fileName = fileChooser.getSelectedFile().getPath();
                        DataModule.close();
                        DataModule.createData(fileName);
                        DataModule.open(fileName);
                        tree.open();
                        frame.setTitle(fileName);
                    }
                    break;
                    
                case MN_OPEN_DATA:
                    fileChooser.setSelectedFile(null);
                    fileChooser.setFileFilter(dataFilter); 
                    retVal = fileChooser.showOpenDialog(this);
                    if (retVal==JFileChooser.APPROVE_OPTION){
                        fileName = fileChooser.getSelectedFile().getPath();
                        DataModule.close();
                        DataModule.open(fileName);
                        tree.open();
                        frame.setTitle(APP_TITLE+"["+ fileName+"]");
                    }
                    break;
                    
                case MN_EXIT:
                    System.exit(0);
                    break;
                ///////////////////////////////
                case MN_REFRESH_TREE:
                    tree.open();
                    break;
                    
                case MN_LOAD_SCRIPT:
                    fileChooser.setFileFilter(sqlFileFilter);
                    retVal = fileChooser.showOpenDialog(this);
                    if (retVal == JFileChooser.APPROVE_OPTION){
                        file=fileChooser.getSelectedFile();
                        editor = new Editor();
                        editor.loadFromFile(file);
                        tabbed.addTab(file.getName(), editor);
                        tabbed.setSelectedComponent(editor);
                    }
                    break;
                    
                case MN_SAVE_SCRIPT:
                    if (editor!=null && editor.sourceFile!=null){
                        editor.saveFile();
                    }
                    break;
                    
                case MN_SAVE_AS_SCRIPT:
                    fileChooser.setFileFilter(sqlFileFilter);
                    retVal = fileChooser.showSaveDialog(this);
                    if (retVal==JFileChooser.APPROVE_OPTION){
                        file = fileChooser.getSelectedFile();
                        if (file.exists())
                            throw new Exception(ERROR_FILE_EXISTS+"\n"+file.getPath());                        
                        editor.saveFileAs(file);
                        tabbed.setTitleAt(tabbed.indexOfComponent(editor),file.getName());
                    }
                    break;
                    
                case MN_SELECT:
                    Dataset dataset;
                    if (info!=null && editor!=null){
                        if (info.isTable()){
                            dataset = DataModule.getDataset(info.getTableName());
                        } else
                            dataset = DataModule.getSQLDataset("select * from "+info.getTableName());
                    
                        dataset.open();
                        editor = new Editor();
                        editor.addGrid(info.getTableName(),dataset);
                        tabbed.addTab(info.getTableName(), editor);
                        tabbed.setSelectedComponent(editor);
                    }
                    break;
                    
                case MN_CRATE_SQL:
                    if (info!=null && editor!=null){
                        String sql = DataModule.getCreateSql(info.getTableName());
                        editor.text.append(sql+";\n");
                    }
                    break;
                    
                case MN_SELECT_SQL:                    
                    if (info!=null && editor!=null){
                        String sql = "";
                        for (String s:info.getColumnNames()){
                            if (!sql.isEmpty())
                                sql+=",";
                            sql+=s;
                        }
                        sql = "select\n  "+sql+"\nfrom\n  "+ info.getTableName()+";\n";
                        editor.text.append(sql);
                    }
                    break;
                    
                case MN_INSER_SQL:
                    if (info!=null && editor!=null && info.isTable()){
                        editor.text.append(info.insertSQL+"\n");
                    }    
                    break;
                case MN_UPDATE_SQL:
                    if (info!=null && editor!=null && info.isTable()){
                        editor.text.append(info.updateSQL+"\n");
                    }    
                    break;
                    
                case MN_EXECUTE:
                    if (editor!=null){
                        try{
                            wait(true);
                            statusLabel.setText("work...");
                            editor.doExecute();
                        } finally{
                            wait(false);
                            statusLabel.setText("redy");
                        }
                    }
                    break;
                    
                case MN_EXECUTE_ALL:
                    if (editor!=null)
                        try{
                            wait(true);
                            statusLabel.setText("work...");
                            editor.doExecuteAll();
                        } finally {
                            wait(false);
                            statusLabel.setText("redy");
                        }
                    break;
                    
                case MN_CLOSE_EDITOR:
                    break;
            }
        } catch (Exception e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
    
    class Editor extends SQLEditor{

        public Editor() {
            super();
            actions = textAction;
        }
        
        @Override
        public void doSave() {
        }

        @Override
        public void doNewTab() {
            Editor edtr = new Editor();
            tabbed.addTab("new",edtr);
            tabbed.setSelectedIndex(tabbed.indexOfComponent(edtr));
        }

        @Override
        public void doClose() {
            if (tabbed.getTabCount()<2)
                return;
            int i= tabbed.getSelectedIndex();
            tabbed.remove(i);
            if (i>=tabbed.getTabCount()-1){
                i=tabbed.getTabCount()-1;
            }
            if (i>=0){
                tabbed.setSelectedIndex(i);
            }
        }
        

        @Override
        public void doExecuteAll() throws Exception{
            tabbs.removeAll();
            String[] sqls = getSQLList();
            try{
                for (String sql:sqls){
                    System.out.println("-->"+sql);
                    if (sql.trim().startsWith("select")){
                        Dataset dataset = DataModule.getSQLDataset(sql);
                        dataset.open();
                        addGrid(dataset);
                    } else {
                        DataModule.execute(sql);
                    }
                    System.out.println("OK");
                }
                DataModule.commit();
            } catch (Exception e){
                DataModule.rollback();
                throw new Exception (e.getMessage());
            }
        }   

        @Override
        public void doExecute() throws Exception{
            Dataset dataset ;
            String sql = getSelectedSQL();
            if (sql.isEmpty())
                return;
            tabbs.removeAll();
            
            try{
                
                if (sql.startsWith("select")){
                    dataset = DataModule.getSQLDataset(sql);
                    dataset.open();
                    addGrid(dataset);
                } else {
                    DataModule.execute(sql);
                }
                DataModule.commit();
                
            } catch (Exception e) {
                DataModule.rollback();
                throw new Exception(e.getMessage());
            }
        }
        
    }
    
    public SQLMonitor2() {
        setPreferredSize(new Dimension(800,600));
        setLayout(new BorderLayout());
        tree =  new DBTree() {};
        tree.actions=treeActions;
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(new JScrollPane(tree));
        splitPane.setRightComponent(tabbed);
        splitPane.setDividerLocation(200);
        add(splitPane);
        add(statusLabel,BorderLayout.PAGE_END);
    }
    
    public void open() throws Exception{
        tree.open();
        tabbed.add("new",new Editor());
    }
    
    public JMenu getDataMenu(){
        JMenu menu = new JMenu("Data");
        for (Action a:dataActions)
            if (a==null)
                menu.addSeparator();
            else
                menu.add(a);
        return menu;
    }
    
    public JMenu getTreeMenu(){
        JMenu menu = new JMenu("Tree");
        for (Action a:treeActions)
            if (a==null)
                menu.addSeparator();
            else
                menu.add(a);
        return menu;
    }
    
    public JMenu getEditorMenu(){
        JMenu menu = new JMenu("Editor");
        for (Action a:textAction)
            if (a==null)
                menu.addSeparator();
            else    
                menu.add(a);
        return menu;
    }
    
    public static JFrame frame;
    
    public static void showSQLMonitor(JComponent owner) throws Exception{
        SQLMonitor2 sqlMonitor = new SQLMonitor2();
        try{
            frame  = new JFrame(APP_TITLE);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(sqlMonitor);
            
            JMenuBar menuBar = new JMenuBar();
            menuBar.add(sqlMonitor.getDataMenu());
            menuBar.add(sqlMonitor.getTreeMenu());
            menuBar.add(sqlMonitor.getEditorMenu());
            frame.setJMenuBar(menuBar);
            frame.pack();
            
            if (owner!=null){
                int x,y;
                x=owner.getLocationOnScreen().x+(owner.getWidth()-frame.getWidth())/2;
                y=owner.getLocationOnScreen().y+(owner.getHeight()-frame.getHeight())/2;
                frame.setLocation(x, y);
            }
            
            frame.setVisible(true);
            sqlMonitor.open();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args){
        try{
            DataModule.open();
            showSQLMonitor(null);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
}
