/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.sqlite;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import ru.viljinsky.dialogs.BaseDialog;
import ru.viljinsky.forms.CommandListener;
import ru.viljinsky.forms.CommandMngr;

/**
 *
 * @author вадик
 */

class GridColumnInfo{
    Integer columnOrder;
    String columnName;
    String displayName;
    Integer size = 75;
    boolean visible = true;
    public GridColumnInfo(int columnOrder,String columnName){
        this.columnOrder=columnOrder;
        this.displayName=columnName;
        this.columnName=columnName;
    }
    
    @Override
    public String toString(){
        return String.format("%d %s %s %s",columnOrder,displayName,columnName,visible);
    }
    
}

class GridColumnInfoList extends ArrayList<GridColumnInfo>{
    
    public void saveToFile(String fileName,String grid_id) throws Exception{
        File file = new File(fileName);
        String line;
        StringBuilder result = new StringBuilder();
        if (file.exists()){
            BufferedReader br = null;
            try{
                br=new BufferedReader(new FileReader(file));
                while ((line=br.readLine())!=null){
                    String[] s = line.split(";");
                    if (s[0].equals(grid_id))
                        continue;
                    result.append(line).append("\n");
                }
            } finally {
                if (br!=null) br.close();
            }
        }
        for (GridColumnInfo info:this){
            result.append(String.format("%s;%d;%s;%s;%d;%s\n",grid_id, info.columnOrder,info.columnName,info.displayName,info.size,info.visible));
        }

        BufferedWriter wr = null;
        try{
            wr= new BufferedWriter(new FileWriter(file));
            wr.write(result.toString());
        } finally {
            if (wr!=null) wr.close();
        }
    }
    
    public boolean loadFromFile(String fileName,String grid_id) throws Exception{
        clear();
        File file = new File(fileName);
        if (!file.exists())
            return false;
        BufferedReader rd =null;
        String line;
        GridColumnInfo info;
        try{
            rd = new BufferedReader(new FileReader(file));
            while ((line=rd.readLine())!=null)
             if (!line.isEmpty()){
                    String[] s = line.split(";");
                    if (s[0].equals(grid_id)){
                        info = new GridColumnInfo(Integer.valueOf(s[1]), s[2]);
                        info.displayName=s[3];
                        info.size=Integer.valueOf(s[4]);
                        info.visible=Boolean.valueOf(s[5]);
                        add(info);
                    }
                }
            return true;
        } finally{
            if (rd!=null) rd.close();
        }
    }
}

//--------------------------------------------------
abstract class ColumnDialog extends  BaseDialog implements CommandListener{
    CommandMngr commands ;
    DefaultListModel<GridColumnInfo> model;
    public JList list ;

    class CellRender extends JLabel implements ListCellRenderer{

        public CellRender() {
            setOpaque(true);
        }

        
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            GridColumnInfo info = (GridColumnInfo)value;
            setText(info.displayName+" ("+info.columnName+")");
            if (isSelected){
                setBackground(list.getSelectionBackground());
                if (info.visible)
                    setForeground(list.getSelectionForeground());
                else
                    setForeground(Color.GRAY);
            } else {
                setBackground(list.getBackground());
                if (info.visible)
                    setForeground(list.getForeground());
                else
                    setForeground(Color.LIGHT_GRAY);
            }
            return this;
        }
    }

    public void setColumnInfoList(List<GridColumnInfo> listInfo){
        model = new DefaultListModel<>();
        for (GridColumnInfo info:listInfo){
            model.addElement(info);
        }
        list.setModel(model);
    }

    public GridColumnInfoList getColumnInfoList(){
        GridColumnInfoList infoList = new GridColumnInfoList();
        GridColumnInfo info;
        for (int i=0;i<model.getSize();i++){
            info = model.getElementAt(i);
            info.columnOrder=i;
            infoList.add(info);
        }
        return infoList;
    }


    @Override
    public Container getPanel() {
        list = new JList();
        commands  = new CommandMngr(new String[]{"MOVE_UP","MOVE_DOWN","RENAME","VISIBLE"});
        commands.addCommandListener(this);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(list));
        JPanel commandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (Action a:commands.getActions()){
            commandPanel.add(new JButton(a));
        }
        panel.add(commandPanel,BorderLayout.PAGE_END);
        list.setCellRenderer(new CellRender());
        list.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting())
                    commands.updateActionList();
            }
        });
        list.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode()==KeyEvent.VK_ENTER){
                    doCommand("RENAME");
                }
            }

        });
        commands.updateActionList();
        return panel;
    }

    @Override
    public void doCommand(String command) {
        int n = list.getSelectedIndex();
        GridColumnInfo o1;
        switch (command){
            case "MOVE_DOWN":
                if (n<model.getSize()-1){
                    o1 = model.getElementAt(n);
                    model.setElementAt(model.getElementAt(n+1), n);
                    model.setElementAt(o1, n+1);
                    list.setSelectedIndex(n+1);
                    list.scrollRectToVisible(list.getCellBounds(n+1, n+1));
                }
                break;
            case "MOVE_UP": 
                if (n>0){
                    o1 = model.getElementAt(n);
                    model.setElementAt(model.getElementAt(n-1), n);
                    model.setElementAt(o1, n-1);
                    list.setSelectedIndex(n-1);
                    list.scrollRectToVisible(list.getCellBounds(n-1, n-1));
                }
                break;
            case "RENAME" :
                o1 = (GridColumnInfo)list.getSelectedValue();
                o1.displayName = JOptionPane.showInputDialog("Название "+o1.columnName,o1.displayName);
                list.repaint();
                break;
            case "VISIBLE":
                boolean b = !((GridColumnInfo)list.getSelectedValue()).visible;
                for (int i:list.getSelectedIndices()){
                    o1=model.elementAt(i);
                    o1.visible = b;
                }
                list.repaint();
                break;

            default:
                System.out.println(command);
        }
    }

    @Override
    public void updateAction(Action action) {
        String command = (String)action.getValue(Action.ACTION_COMMAND_KEY);
        switch (command){
            case "MOVE_UP":
                action.setEnabled(list.getSelectedIndex()>0);
                break;
            case "MOVE_DOWN":
                action.setEnabled(list.getSelectedIndex()>=0 
                        && list.getSelectedIndex()<model.size()-1);
                break;
            case "RENAME":
                action.setEnabled(list.getSelectedIndices().length==1);
                break;
            case "VISIBLE":
                action.setEnabled(list.getSelectedIndices().length>0);
                break;
            default:
                System.out.println(command);
        }
    }
};

