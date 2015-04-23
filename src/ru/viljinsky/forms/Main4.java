/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import ru.viljinsky.CreateData;
import ru.viljinsky.DataModule;
import ru.viljinsky.util.SQLMonitor;



////////////////////////    MAIN 4 ////////////////////////////////////////////
interface IMenu{
    public static final int FILE_OPEN   = 1;
    public static final int FILE_NEW    = 2;
    public static final int FILE_CLOSE  = 3;
    public static final int FILE_EXIT   = 4;
    
    public static final int DICTIONARY  = 5;
    public static final int SHIFT       = 6;
    public static final int TIMEGRID    = 7;
    public static final int MONITOR     = 8;
}

public class Main4 extends JFrame{
    public static String APP_NAME = "TimeTable2015";
//    private static DataModule dataModule = DataModule.getInstance(); 
    
    JFileChooser fileChooser = new JFileChooser(new File("."));
    IOpenedForm[] forms = {
        new CurriculumPanel(),
        new DepartPanel(),
        new TeacherPanel(),
        new RoomPanel(),
//        new SchedulePanel(),
        new TimeGridPanel2(),
        new ReportPanel()
    };

    public Main4(String title){
        super(title);
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
    
    private void doCommand(String command){
        try{
            switch(command){
                
                case "fileNew":
                    fileNew();
                    break;
                case "fileOpen":
                    fileOpen();
                    break;
                case "fileClose":
                    fileClose();
                    break;
                            
                
                case "DICTIONARY":
                    Dictonary.showDialog(rootPane);
                    break;
                    
                case "sqlMonitor":
                    SQLMonitor.showFrame(rootPane);
                    break;
                    
                case "testShift":
                    TestShift2.showShiftDialog(rootPane);
                    
                    break;
//                case "timegrid":
//                    TimeGridPanel.showFrame(rootPane);
//                    break;
                    
                case "exit":
                    System.exit(0);
                    break;
            }
        } catch (Exception e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
        
    }
    public JMenuBar createMenuBar(){
        JMenuBar result = new JMenuBar();
        result.add(createFileMenu());
        result.add(createUtilMenu());
        return result;
    }
    
    public JMenu createFileMenu(){
        JMenu result = new JMenu("File");
        result.add(new Act("fileNew"));
        result.add(new Act("fileOpen"));
        result.add(new Act("fileClose"));
        result.addSeparator();
        result.add(new Act("exit"));
        return result;
    }
    public JMenu createUtilMenu(){
        JMenu result = new JMenu("Util");
        result.add(new Act("sqlMonitor"));
        result.add(new Act("testShift"));
        result.add(new Act("DICTIONARY"));
//        result.add(new Act("timegrid"));
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
    
    public void intComponents(){
        setLayout(new BorderLayout());        
        JTabbedPane tabbedPane = new JTabbedPane();
        add(tabbedPane);
        for (IOpenedForm form:forms){
            tabbedPane.addTab(form.getCaption(), form.getPanel());
        }
        setJMenuBar(createMenuBar());
    }
    
   
    
    public static void main(String[] args) throws Exception{
        
        Main4 frame = new Main4(APP_NAME);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.intComponents();
        
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
