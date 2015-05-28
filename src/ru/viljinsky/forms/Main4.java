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
import java.awt.Toolkit;
import java.io.File;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import ru.viljinsky.dialogs.BaseDialog;
import ru.viljinsky.sqlite.CreateData;
import ru.viljinsky.sqlite.DataModule;
import ru.viljinsky.util.SQLMonitor2;
import ru.viljinsky.util.ScriptEditor;



////////////////////////    MAIN 4 ////////////////////////////////////////////


public class Main4 extends JFrame implements CommandListener{
    
    public static final String MENU_FILE   = "Файл";
    
    public static final String FILE_OPEN   = "FILE_OPEN";
    public static final String FILE_NEW    = "FILE_NEW";
    public static final String FILE_CLOSE  = "FILE_CLOSE";
    public static final String FILE_EXIT   = "FILE_EXIT";
    public static final String LOAD_SCRIPT = "LOAD_SCRIPT";
    
    public static final String MENU_UTILS   = "Утилиты";
    
    public static final String DICTIONARY  = "DICTIONARY";
    public static final String SHIFT       = "SHIFT";
    public static final String TIMEGRID    = "TIME_GRID";
    public static final String MONITOR     = "MONITOR";
    
    public static String APP_NAME = "TimeTable2015";
    
    CommandMngr commands = new CommandMngr(new String[]{
        FILE_CLOSE,
        FILE_EXIT,
        FILE_NEW,
        FILE_OPEN,
        DICTIONARY,
        SHIFT,
        TIMEGRID,
        MONITOR,
        LOAD_SCRIPT
    });
    
    JFileChooser fileChooser = new JFileChooser(new File("."));
    
    IOpenedForm[] forms = {
        new CurriculumPanel(),
        new DepartPanel(),
        new TeacherPanel(),
        new RoomPanel(),
//        new TimeGridPanel2(),
        new SchedulePanel3(),
        new ReportPanel(),
    };

    public Main4(String title){
        super(title);
        intComponents();
    }
    
    public void intComponents(){
        commands.addCommandListener(this);
        commands.updateActionList();
        setLayout(new BorderLayout());        
        JTabbedPane tabbedPane = new JTabbedPane();
        add(tabbedPane);
        for (IOpenedForm form:forms){
            tabbedPane.addTab(form.getCaption(), form.getPanel());
        }
        setJMenuBar(createMenuBar());
    }

    @Override
    public void updateAction(Action action) {
        String command = (String)action.getValue(Action.ACTION_COMMAND_KEY);
        switch (command){
            case FILE_OPEN:
                action.setEnabled(!DataModule.isActive());
                break;
            case FILE_CLOSE:
                action.setEnabled(DataModule.isActive());
                break;
            case DICTIONARY:
                action.setEnabled(DataModule.isActive());
                break;
            case MONITOR:
                action.setEnabled(DataModule.isActive());
                break;
            case SHIFT:
                action.setEnabled(DataModule.isActive());
                break;
        }
    }

    @Override
    public void doCommand(String command){
        try{
            switch(command){
                
                case FILE_OPEN:
                    fileOpen();
                    break;
                    
                case FILE_CLOSE:
                    fileClose();
                    break;
                
                case DICTIONARY:
                    Dictonary.showDialog(rootPane);
                    break;
                    
                case LOAD_SCRIPT:
                    loadScript();
                    break;
                case MONITOR:
                    SQLMonitor2.showSQLMonitor(rootPane);
                    break;
                    
                case SHIFT:
                    TestShift2.showShiftDialog(rootPane);
                    
                    break;
                    
                case FILE_EXIT:
                    System.exit(0);
                    break;
            }
        } catch (Exception e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
    
    
    protected void fileNew() throws Exception{
        File file;
        String path;
        int retVal =  fileChooser.showOpenDialog(this);
        if (retVal==JFileChooser.APPROVE_OPTION){
            file = fileChooser.getSelectedFile();
            path = file.getPath();
            if (DataModule.isActive()){
                close();
                DataModule.close();
               
            }
            CreateData.execute(path);
            DataModule.open(path);
            open();
            setTitle(APP_NAME+" ["+path+"]");
            JOptionPane.showMessageDialog(this, "База \""+path+"\" - успешно создана");
        }
    }
    
    public void loadScript(){
        BaseDialog dlg = new BaseDialog() {

            @Override
            public Container getPanel() {
                ScriptEditor editor = new ScriptEditor("text","Редактор скрипта");
                JPanel panel = new JPanel(new BorderLayout());
                panel.add(new JScrollPane(editor));
                JPanel commandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                for (Action a:editor.getEditorActions()){
                    commandPanel.add(new JButton(a));
                }
                panel.add(commandPanel,BorderLayout.PAGE_START);
                
                return panel;
            }

            
            @Override
            public void doOnEntry() throws Exception {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        dlg.showModal(this);
    }
    
    public void fileOpen(){
        String fileName ="\\.";
        SelectDatabase dlg = new SelectDatabase(){

            @Override
            public void doOnEntry() throws Exception {
                if (getFileName()==null)
                    throw new Exception("Укажите файл");
                if (isNewFile()){
                    CreateData.execute(getFileName());
                }
            }
        };
        dlg.showModal(rootPane);
        if (dlg.modalResult==SelectDatabase.RESULT_OK)
            try {
                fileName = dlg.getFileName();
                if (DataModule.isActive()){
                    close();
                    DataModule.close();
                }
                DataModule.open(fileName);
                setTitle(APP_NAME+"["+fileName+"]");
                open();
            } catch (Exception e){
                JOptionPane.showMessageDialog(rootPane, e.getMessage());
            } finally {
                commands.updateActionList();
            }
    }
    
    protected void fileClose() throws Exception{
        if (DataModule.isActive()){
            close();
            DataModule.close();
            setTitle(APP_NAME);
        }
    }
    
    protected void fileName(){
        int retVal = fileChooser.showOpenDialog(this);
        if (retVal==JFileChooser.APPROVE_OPTION){
        }
    
    }
    
    public JMenuBar createMenuBar(){
        JMenuBar result = new JMenuBar();
        result.add(createFileMenu());
        result.add(createUtilMenu());
        return result;
    }
    
    public JMenu createFileMenu(){
        JMenu result = new JMenu(MENU_FILE);
        result.add(commands.getAction(FILE_NEW));
        result.add(commands.getAction(FILE_OPEN));
        result.add(commands.getAction(FILE_CLOSE));
        result.addSeparator();
        result.add(commands.getAction(FILE_EXIT));
        return result;
    }
    public JMenu createUtilMenu(){
        JMenu result = new JMenu(MENU_UTILS);
        result.add(commands.getAction(MONITOR));
        result.add(commands.getAction(SHIFT));
        result.add(commands.getAction(DICTIONARY));
        result.addSeparator();
        result.add(commands.getAction(LOAD_SCRIPT));
        return result;
    }

    public void close() throws Exception{
        for (IOpenedForm form:forms)
            form.close();
        
    }
    
    public void open() throws Exception{
        for (IOpenedForm form:forms)
            form.open();        
    }
    
    public static void main(String[] args){
        
        Main4 frame = new Main4(APP_NAME);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        
        // позичионирование главного окна
        int x,y,w,h;
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

        h= d.height*8/10;
        w= h*5/4;
        frame.setSize(new Dimension(w,h));
        
        x=(d.width-frame.getWidth())/2;
        y=(d.height-frame.getHeight())/2;         
        frame.setLocation(x,y);
        
        frame.setVisible(true);
        frame.fileOpen();
      
        
    }
    
}
