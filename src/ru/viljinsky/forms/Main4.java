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
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import ru.viljinsky.CreateData;
import ru.viljinsky.DataModule;
import ru.viljinsky.util.SQLMonitor;


////////////////////////    MAIN 4 ////////////////////////////////////////////

public class Main4 extends JPanel{
    private static DataModule dataModule = DataModule.getInstance(); 
    
    JFileChooser fileChooser = new JFileChooser(new File("."));
    IOpenedForm[] forms = {
        new CurriculumPanel(),
        new DepartPanel(),
        new TeacherPanel(),
        new RoomPanel(),
        new SchedulePanel()
    };
    
    

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
            if (dataModule.isActive()){
                close();
                dataModule.close();
               
            }
            CreateData.execute(path);
            dataModule.open(path);
            open();
            JOptionPane.showMessageDialog(this, "База \""+path+"\" - успешно создана");
        }
    }
    protected void fileOpen() throws Exception{
        int retVal=fileChooser.showOpenDialog(this);
        if (retVal==JFileChooser.APPROVE_OPTION){
            if (dataModule.isActive()){
                close();
                dataModule.close();
            }
            dataModule.open(fileChooser.getSelectedFile().getPath());
            open();
        }
    }
    
    protected void fileClose() throws Exception{
        if (dataModule.isActive()){
            close();
            dataModule.close();
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
                    Dictonary.showDialog(this);
                    break;
                    
                case "sqlMonitor":
                    SQLMonitor.showFrame(Main4.this);
                    break;
                    
                case "testShift":
                    TestShift2.showShiftDialog(this);
                    
                    break;
                case "timegrid":
                    TimeGridPanel.showFrame(Main4.this);
                    break;
                    
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
        result.add(new Act("timegrid"));
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
    }
    
   
    
    public static void main(String[] args) throws Exception{
        
        
                
        JFrame frame = new JFrame("Main4");
        Main4 panel = new Main4();
        panel.intComponents();
//        panel.add(tabbedPane);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.setJMenuBar(panel.createMenuBar());
        frame.pack();
        
        // позичионирование главного окна
        int x,y;
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        x=(d.width-frame.getWidth())/2;
        y=(d.height-frame.getHeight())/2;         
        frame.setLocation(x,y);
        
        frame.setVisible(true);

      
        try{
            dataModule.open();
            panel.open();
        } catch (Exception e){
            JOptionPane.showMessageDialog(frame, e.getMessage());
        }
        
    }
    
}
