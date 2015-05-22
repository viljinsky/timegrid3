package ru.viljinsky.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.text.Document;
import ru.viljinsky.forms.CommandListener;
import ru.viljinsky.forms.CommandMngr;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author вадик
 */


public class ScriptEditor extends JEditorPane implements CommandListener{
    CommandMngr commands = new CommandMngr();
    JFileChooser fc = new JFileChooser(new File("."));
    Document doc;
    
    public ScriptEditor(String type, String text) {
        super("text",text);
        doc = getDocument();
        setPreferredSize(new Dimension(500,400));
        addMouseListener(new MouseAdapter() {

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
                    getPopupMenu().show(ScriptEditor.this, e.getX(),e.getY());
                }
            }
            
        });
        commands.setCommands(new String[]{"LOAD","EXECUTE"});
        commands.addCommandListener(this);
        
    }
    
    public JPopupMenu getPopupMenu(){
        JPopupMenu popupMenu = new JPopupMenu();
        for (Action a:commands.getActions()){
            popupMenu.add(a);
        }
        return popupMenu;
    }
    
    public Action[] getEditorActions(){
        return commands.getActions();
    }
    
    private void loadScript(File file) throws Exception{
        BufferedReader r = new BufferedReader(new FileReader(file));
        String l;
        doc.remove(0, doc.getLength());
        while ((l=r.readLine())!=null){
            doc.insertString(doc.getLength(), l+"\n", null);
        }
        r.close();
        setCaretPosition(0);
    }
    
    public boolean executeScript() throws Exception{
        Pattern p= Pattern.compile(";");
        Pattern p2 = Pattern.compile("--.*");
        String s = doc.getText(0, doc.getLength());
        String[] items = p.split(s);
        int n=0;
        for (String ss:items){
            Matcher m = p2.matcher(ss);
            String res= m.replaceAll("");
            if (!res.trim().isEmpty())
                System.out.println(n+++"-> "+res.trim());
        }
        return true;
    }
    
    @Override
    public void doCommand(String command) {
        System.out.println(command);
        try{
            switch (command){
                case "LOAD":
                    int retval = fc.showOpenDialog(this);
                    if (retval==JFileChooser.APPROVE_OPTION){
                        loadScript(fc.getSelectedFile());
                    }
                    break;
                case "EXECUTE":
                    executeScript();
                    JOptionPane.showMessageDialog(this, "Script успешно выполнен");
                    break;
            }
        } catch (Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    @Override
    public void updateAction(Action action) {
    }
    
    public static void main(String[] args){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel(new BorderLayout());
        String text="qwwerty;\n12345\n--67890;\nHello\nworld";
        panel.add(new JScrollPane(new ScriptEditor("text",text)));
        
        frame.setContentPane(panel);
        frame.pack();
        frame.setVisible(true);
    }

    
}
