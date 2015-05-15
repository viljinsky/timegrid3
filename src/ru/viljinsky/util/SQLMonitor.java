/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
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
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
import ru.viljinsky.sqlite.IDataset;

/**
 *
 * @author вадик
 */

class GridPanel extends JPanel{
    Grid grid;
    JLabel status = new JLabel("Status");
    public GridPanel(Grid grid){
        super(new BorderLayout());
        this.grid=grid;
        add(new JScrollPane(grid),BorderLayout.CENTER);
        add(status,BorderLayout.PAGE_END);
        grid.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()){
                    recordChange();
                }
            }
        });
    }
    private void recordChange(){
        updateStatus();
    }
    
    public void updateStatus(){
        int row = grid.getSelectedRow();
        int rows = grid.getRowCount();
        setStatusText(String.format("%d из %d", row,rows));        
    }
    
    public void setStatusText(String text){
        status.setText(text);
    }
}

interface MenuConstants{
    
    final String dataConnect    = "connect";
    final String dataDisconnect = "disconnect";
    final String exit           = "Exit";
    
    final String scriptOpen     = "open";
    final String scriptSave     = "save";
    final String scriptNew      = "new";
    final String scriptClose    = "close";
    
    
    final String scriptRun      = "run";
    final String scriptRunAll   = "run all";
    final String scriptClear    = "clear";
    
    final String treeCreateSQL  = "create sql";
    final String treeRefresh    = "refresh";
    final String treeRunSQL     = "run sql";
    
    public static final String  BACK_SLASH = "BACK_SLASH";
}

public class SQLMonitor extends JFrame implements MenuConstants{

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
        
        Container content = getContentPane();
        JToolBar toolBar = new JToolBar("main");
        
        JSplitPane splitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane1.setLeftComponent(new JScrollPane(tree));
        splitPane1.setRightComponent(panels);
        splitPane1.setDividerLocation(200);
        content.add(splitPane1);
        
        toolBar.add(new Act("commit"));
        toolBar.add(new Act("rollback"));
        
        content.add(toolBar,BorderLayout.PAGE_START);
        content.setPreferredSize(new Dimension(800,600));
        
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(tree.createMenu());
        menuBar.add(sqlPanel.createMenu());
        setJMenuBar(menuBar);
        
        panels.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                sqlPanel = (SQLPanel)panels.getSelectedComponent();
            }
        });
        updateActionList();
    }
    Action[] actions = {new Act(dataConnect),new Act(dataDisconnect),null,new Act(exit)};
    
    public JMenu createFileMenu(){
        JMenu menu = new JMenu("File");
        for (Action a:actions){
            if (a==null)
                menu.addSeparator();
            else
                menu.add(a);
        }
        return menu;
    }
    
    public void updateActionList(){
        sqlPanel.updateActionList();
        for (Action a:actions)
            if (a!=null)
                updateAction(a);
    }

    public void updateAction(Action a){
        switch ((String)a.getValue(Action.ACTION_COMMAND_KEY)){
            case dataConnect:
                a.setEnabled(!DataModule.isActive());
                break;
            case dataDisconnect:
                a.setEnabled(DataModule.isActive());
                break;
        }
        
    }
    
        public void doCommand(String command){
        String sql;    
        try{
            switch(command){
                case dataConnect:
                    fileOpen();
                    break;
                case dataDisconnect:
                    fileClose();
                    break;
                case scriptNew:
                    scriptNew();
                    break;
                case scriptOpen:
                    scriptOpen();
                    break;
                case scriptSave:
                    scriptSave();
                    break;
                case scriptClose:
                    scriptClose();
                    break;
                    
                case exit:
                    System.exit(0);
                    break;
                case  scriptClear :
                    sqlPanel.clear();
                    break;
                case scriptRun:
                    sqlPanel.execute();
                    break;
                case scriptRunAll:
                    sqlPanel.executeAll();
                    break;
                    
                case treeCreateSQL:
                    sql = tree.createSQL();
                    sqlPanel.textEditor.append(sql);
                    break;
                case treeRefresh:
                    DataModule.reopen();
                    tree.fill();
                    break;
                    
                // Получаем sql из дерева и выполняем    
                case treeRunSQL:
                    sql = tree.createSQL();
                    sqlPanel.textEditor.append(sql);
                    sqlPanel.executeSql(sql);
                    break;
                case "commit":
                    DataModule.commit();
                    break;
                case "rollback":
                    DataModule.rollback();
                    break;
                case BACK_SLASH:
                    backSlash();
                    break;
                default:
                    System.err.println(command);
            }
            updateActionList();
        } catch (Exception e){
            JOptionPane.showMessageDialog(rootPane, e.getMessage());
        }
    }

    public void backSlash(){
        Component component = panels.getSelectedComponent();
        if (component==null)
            return;
        SQLPanel  panel = (SQLPanel)component;
        int start = panel.textEditor.getSelectionStart();
        int end = panel.textEditor.getSelectionEnd();
        if (start!=end){
            try{
                String text = panel.textEditor.getText(start, end-start);
                String[] lines = text.split("\n");
                String res = "";
                for (String s:lines){
                    if (s.startsWith("--"))
                        res += s.substring(2)+"\n";
                    else
                        res += "--"+s+"\n";
                }
                panel.textEditor.replaceSelection(res);
            } catch (Exception e){
                
            }
            
        }
        
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
            new Act(treeRunSQL),    
            new Act(treeCreateSQL),
            new Act(treeRefresh)
        };

        public SQLTree(){
            clear();

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
            IDataset dataset ;

            for (DatasetInfo info:DataModule.getInfoList()){
                node= new DefaultMutableTreeNode(info);
                root.add(node);
                try{
                    dataset = DataModule.getDataset(info.getTableName());
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
        
        public void clear(){
            DefaultMutableTreeNode root;
            root = new DefaultMutableTreeNode("База данных");
            DefaultTreeModel model = new DefaultTreeModel(root);
            setModel(model);
            
            removeAll();
            
        }

        private String createSQL() {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)getLastSelectedPathComponent();
            if (node!=null){
                Object obj = node.getUserObject();
                if (obj instanceof DatasetInfo){
                    DatasetInfo info = (DatasetInfo)obj;
                    String sql = "";
                    for (String columName:info.getColumnNames()){
                        if (!sql.isEmpty()) sql+=",";
                            sql+=columName;
                    }
                            
                    return "select "+sql+" from "+info.getTableName()+";\n";
                }
            }
            return null;
        }


    }

    //************************************************************************//

    class SQLPanel extends JSplitPane{
        String title = "new";
        String fileName = null;
        Boolean hasChange = false;
        
        JTextArea textEditor = new JTextArea();
        JTabbedPane tabs = new JTabbedPane();
        Action[] actions = {
            new Act(scriptRun),
            new Act(scriptRunAll),
            new Act(scriptClear),
            null,
            new Act(scriptNew),
            new Act(scriptOpen),
            new Act(scriptSave),
            null,
            new Act(scriptClose)
        };
        
        public void loadText(String fileName) throws Exception{
            File file = new File(fileName);
            BufferedReader reader = null;
            textEditor.setText("");
            try{
                reader= new BufferedReader(new FileReader(file));
                String line;
                while ((line=reader.readLine())!=null){
                    textEditor.append(line+"\n");
                }
                this.fileName=fileName;
                this.hasChange=false;
            } finally {
                reader.close();
            }
            textEditor.setCaretPosition(0);
        }
        
        public void saveText(String fileName) throws Exception{
            File file = new File(fileName);
            BufferedWriter writer = null;
            try{
                writer = new BufferedWriter(new FileWriter(file));
                writer.write(textEditor.getText());
                this.fileName=fileName;
                this.hasChange=false;
            } finally {
                writer.close();
            }
        }
        
        public void updateActionList(){
            for (Action a:actions){
                if (a!=null)
                    updateAction(a);
            }
        }
        
        public void updateAction(Action a){
            String command = (String)a.getValue(Action.ACTION_COMMAND_KEY);
            switch(command){
                case scriptRun:case scriptRunAll:
                    break;
                case scriptNew:case scriptOpen:case scriptSave:case scriptClose:
                    break;
                    
            }
        }
        
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
                                doCommand(scriptRun);
                                break;
                            case KeyEvent.VK_ENTER:
                                doCommand(scriptRunAll);
                                break;
                            case KeyEvent.VK_S:
                                doCommand(scriptSave);
                                break;
                            case KeyEvent.VK_BACK_SLASH:
                                doCommand(BACK_SLASH);
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
            
            textEditor.getDocument().addDocumentListener(new DocumentListener() {

                @Override
                public void insertUpdate(DocumentEvent e) {
                    setIsChanges(true);
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    setIsChanges(true);
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    setIsChanges(true);
                }
            });
            
        }

        public void setIsChanges(boolean value){
            int index = panels.getSelectedIndex();
            panels.setTitleAt(index, title+"*");
//            System.out.println("*Changes*");
        }
 ///////////////////////////////////////////////////////////////////////////////

        synchronized  public void executeSql(String sql) throws Exception{
            System.out.print("EXECUTE:\n'"+sql+"'....");
            try{
            if (sql.startsWith("select")){
                    Dataset dataset = DataModule.getSQLDataset(sql);
                    dataset.open();
                    Grid grid = new Grid();
                    grid.setAutoCreateRowSorter(true);
                   
                    grid.setDataset(dataset);
                    GridPanel gridPanel = new GridPanel(grid);
                    tabs.addTab("sql", gridPanel);
                    tabs.setSelectedIndex(tabs.getTabCount()-1);
                    gridPanel.updateStatus();
            } else {
                    DataModule.execute(sql);
            }
                System.out.println("OK");
            } catch (Exception e){
                System.out.println("ERROR:\n"+e.getMessage());
                throw new Exception("ERROR:\n"+e.getMessage());
                
            }
        }

        public void execute() throws Exception{
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

        public void executeAll() throws Exception{
            String[] lines;
            String sql="";
            tabs.removeAll();

//            dataModule.setAutoConmmit(false);
            try{

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
                DataModule.commit();
            } catch (Exception e){
                DataModule.rollback();
                throw new Exception("EXECUTE_ALL_ERROR\n"+e.getMessage());
            }
//            } finally {
//                dataModule.setAutoConmmit(true);
//            }
        }

        public void clear(){
        }

    
    }
    
    public void fileOpen() throws Exception{
        int retVal = fileChooser.showOpenDialog(panels);
        if (retVal==JFileChooser.APPROVE_OPTION){
            open(fileChooser.getSelectedFile().getPath());
        }
    }
    
    public void fileClose() throws Exception{
        close();
    }
    
//////////////////////////   SCRIPT  ///////////////////////////////////////////    
    public void scriptOpen() throws Exception{
        SQLPanel newPanel = new SQLPanel();
        String path;
        File file;
        int retVal = fileChooser.showOpenDialog(rootPane);
        if (retVal==JFileChooser.APPROVE_OPTION){
            file = fileChooser.getSelectedFile();
            path = file.getPath();
            try{
                newPanel.loadText(path);
                 panels.addTab(file.getName(), newPanel);
                 panels.setSelectedIndex(panels.getTabCount()-1);
                 
            } catch (Exception e){
                throw new Exception("Ошибка сохранения \n"+e.getMessage());
            }
        }
    }
    
    public void scriptSaveAs() throws Exception{
        Component comp = panels.getSelectedComponent();
        if (comp!=null){
            SQLPanel panel = (SQLPanel)comp;
            int retVal = fileChooser.showSaveDialog(rootPane);
            if (retVal==JFileChooser.APPROVE_OPTION){
                File file = fileChooser.getSelectedFile();
                String path = file.getPath();
                panel.saveText(path);

                int index = panels.getSelectedIndex();
                panels.setTitleAt(index, file.getName());
                
            }
        }
    }
    
    public void scriptSave() throws Exception{

        Component component = panels.getSelectedComponent();
        if (component!=null){
        
            SQLPanel panel = (SQLPanel)component;

            if (panel.fileName!=null){
                panel.saveText(sqlPanel.fileName);
            } else {
                scriptSaveAs();
            }
            System.out.println("save OK");
        }
    }
    
    public void scriptClose() throws Exception{
       Integer index = panels.getSelectedIndex();
       if (index>=0){
            panels.remove(index);
       }
        
    }
    public void scriptNew(){
        panels.addTab("new", new SQLPanel());
        panels.setSelectedIndex(panels.getTabCount()-1);
    }
    
    
    //--------------------------------------------------------------------------
    
    
    
    public void open() throws Exception{
//        dataModule.open();
        tree.fill();
        scriptNew();
    }
    
    public void open(String path) throws Exception{
        DataModule.open(path);
        tree.fill();
        scriptNew();
        setTitle("SQLMonitot "+path);
    }
    

    public void close() throws Exception{
        DataModule.close();
        panels.removeAll();
        tree.clear();
    }
    
    private static SQLMonitor monitor = null;
    
    public static void showFrame(JComponent owner) throws Exception{
        if (monitor==null){
            monitor = new SQLMonitor();
            monitor.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            monitor.pack();
            if (owner !=null){
                Point p=owner.getLocationOnScreen();
                monitor.setLocation(p.x+10, p.y+10);
            }
            monitor.open();
        }
        monitor.setVisible(true);
    }
    
    //-------------------------------------------------------------------------
    public static void main(String[] args) throws Exception{
        SQLMonitor frame = new SQLMonitor();
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        
//        DataModule.getInstance().open();
//        frame.open();
    }
}

