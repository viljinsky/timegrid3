/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
//import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import ru.viljinsky.BaseDialog;

/**
 *
 * @author вадик
 */


class SelectDatabase extends BaseDialog {
    JRadioButton rCreateNew = new JRadioButton("Создать новую базу");
    JRadioButton rSelectExists = new JRadioButton("Выбрать из рание созданных");
    ButtonGroup group = new ButtonGroup();
    SelectDataPath panel1 = new SelectDataPath();
    CreatePanel    panel2 = new CreatePanel();
    SelectPanel    panel3 = new SelectPanel();
    
    
    
    public boolean isNewFile(){
        return rCreateNew.isSelected();
    }
    
    public String getFileName(){
        String path = panel1.textPath.getText();
        if (!path.endsWith("\\")){
            path+="\\";
        }
        
        if (rCreateNew.isSelected()){
            return path + panel2.getFileName();
        } else if (rSelectExists.isSelected()){
            return path + panel3.getFileName();
        }  
        return null;
    }
    // panel1
    class SelectDataPath  extends JPanel{
        JTextField textPath = new JTextField(20);
        public SelectDataPath(){
            setLayout(new FlowLayout(FlowLayout.LEFT));
            add(new JLabel("Укажите путь"));
            add(textPath);
            JButton button = new JButton("...");
            button.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser fc = new JFileChooser(new File(textPath.getText()));
                    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int retVal = fc.showDialog(rootPane, "Выбрать");
                    if (retVal==JFileChooser.APPROVE_OPTION){
                        proc1(fc.getSelectedFile().getPath());
                    }
                }
            });
            add(button);
            setBorder(new TitledBorder("Путь к базам данных"));

        }
        
    }
    
    // panel2
    class CreatePanel extends JPanel{
        JTextField txtDataName = new JTextField(20);
        public CreatePanel(){
            setLayout(new FlowLayout(FlowLayout.LEFT));
            setBorder(new TitledBorder("Создать"));
            JLabel label = new JLabel("Имя базы данных");
            add(label);
            add(txtDataName);
            txtDataName.addFocusListener(new FocusAdapter() {

                @Override
                public void focusGained(FocusEvent e) {
                    rCreateNew.setSelected(true);
                }
                
            });
                
            
        }
        
        public String getFileName(){
            return txtDataName.getText();
        }
    }
    
    
        
    //panel3
    class SelectPanel extends JPanel{
        JList<String> fileList = new JList<>();
        public SelectPanel(){
            setLayout(new BorderLayout());
            setBorder(new TitledBorder("Выбрать"));
            add(new JScrollPane(fileList));

            fileList.addListSelectionListener(new ListSelectionListener() {

                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()){
                        if (!rSelectExists.isSelected())
                            rSelectExists.setSelected(true);
                    }
                }
            });
        }
        
        public String getFileName(){
            return fileList.getSelectedValue();
        }
    }

    public void proc1(String dir){
        panel1.textPath.setText(dir);
        DefaultListModel model = new DefaultListModel();
        File f = new File(dir);
        String fileName;
        for (File ff:f.listFiles())
            if (ff.isFile()){
                fileName = ff.getName();
                if (fileName.endsWith(".db"))
                    model.addElement(ff.getName());
            }
        panel3.fileList.setModel(model);
    }
    

    public SelectDatabase() {
        super();
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        Box box;
        
        box = Box.createHorizontalBox();
        box.add(panel1);
        panel.add(box);
        //---------------------------        
        group.add(rCreateNew);
        box = Box.createHorizontalBox();
        box.add(rCreateNew);
        box.add(Box.createHorizontalGlue());
        panel.add(box);
        
        box = Box.createVerticalBox();
        box.add(panel2);
        panel.add(box);
        //-----------------------------
        group.add(rSelectExists);
        box = Box.createHorizontalBox();
        box.add(rSelectExists);
        box.add(Box.createHorizontalGlue());
        panel.add(box);
        
        box = Box.createVerticalBox();
        box.add(panel3);
        panel.add(box);
        //------------------------------
        
        
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        add(panel, BorderLayout.CENTER);
//        proc1("d:\\temp");
        proc1(".");
    }

    @Override
    public void doOnEntry() throws Exception {
    }
    
}
