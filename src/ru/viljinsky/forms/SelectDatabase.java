/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
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
    SelectPanel selectPanel = new SelectPanel();
    CreatePanel createPanel = new CreatePanel();
    
    public String getSelectedData(){
        if (rSelectExists.isSelected()){
            return selectPanel.getFileName();
        }
        return null;
    }
    
    class SelectPanel extends JPanel{
        JList<String> fileList = new JList<>();
        public SelectPanel(){
            setLayout(new BorderLayout());
            setBorder(new TitledBorder("Выбрать"));
            add(new JScrollPane(fileList));

            DefaultListModel model = new DefaultListModel();
            
            File f = new File(".");
            String path ;
            for (File ff: f.listFiles())
                if (ff.isFile()){
                    path = ff.getName();
                    if (path.endsWith(".db"))
                        model.addElement(path);
                }
            fileList.setModel(model);
            
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
    
    class CreatePanel extends JPanel{
        JTextField txtDataName = new JTextField(20);
        public CreatePanel(){
            setLayout(new FlowLayout(FlowLayout.LEFT));
            setBorder(new TitledBorder("Создать"));
            JLabel label = new JLabel("Имя базы данных");
            add(label);
            add(txtDataName);
            
        }
        
        public String getFileName(){
            return txtDataName.getText();
        }
    }

    public SelectDatabase() {
        super();
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        Box box;
        
                
        group.add(rCreateNew);
        box = Box.createHorizontalBox();
        box.add(rCreateNew);
        box.add(Box.createHorizontalGlue());
        panel.add(box);
        
        box = Box.createVerticalBox();
        box.add(createPanel);
        panel.add(box);
        
        group.add(rSelectExists);
        box = Box.createHorizontalBox();
        box.add(rSelectExists);
        box.add(Box.createHorizontalGlue());
        panel.add(box);
        
        box = Box.createVerticalBox();
        box.add(selectPanel);
        panel.add(box);
        
        
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        add(panel, BorderLayout.CENTER);
        
    }

    @Override
    public void doOnEntry() throws Exception {
        
        if  (rCreateNew.isSelected()){
                System.out.println("Создать новый файл "+createPanel.getFileName());
                return;
        }
        if (rSelectExists.isSelected()){
                System.out.println("Открыть существующий файл "+selectPanel.getFileName());
                return;
        }
        throw new Exception("Выберите файл");
        
    }
    
}
