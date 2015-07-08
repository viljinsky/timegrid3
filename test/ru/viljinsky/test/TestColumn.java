/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.test;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilterWriter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.table.TableColumn;
import ru.viljinsky.dialogs.BaseDialog;
import ru.viljinsky.forms.CommandListener;
import ru.viljinsky.forms.CommandMngr;
import ru.viljinsky.sqlite.Column;
import ru.viljinsky.sqlite.DataModule;
import ru.viljinsky.sqlite.Dataset;
import ru.viljinsky.sqlite.Grid;

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
    public void saveToFile(String fileName) throws Exception{
        File file = new File(fileName);
        BufferedWriter wr = null;
        try{
            wr= new BufferedWriter(new FileWriter(file));
            for (GridColumnInfo info:this){
                wr.write(String.format("%d;%s;%s;%d;%s\n", info.columnOrder,info.columnName,info.displayName,info.size,info.visible));
            }
        } finally {
            if (wr!=null) wr.close();
        }
    }
    
    public boolean loadFromFile(String fileName) throws Exception{
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
                    info = new GridColumnInfo(Integer.valueOf(s[0]), s[1]);
                    info.displayName=s[2];
                    info.size=Integer.valueOf(s[3]);
                    info.visible=Boolean.valueOf(s[4]);
                    add(info);
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
                }
                break;
            case "MOVE_UP": 
                if (n>0){
                    o1 = model.getElementAt(n);
                    model.setElementAt(model.getElementAt(n-1), n);
                    model.setElementAt(o1, n-1);
                    list.setSelectedIndex(n-1);
                }
                break;
            case "RENAME" :
                o1 = (GridColumnInfo)list.getSelectedValue();
                o1.displayName = JOptionPane.showInputDialog("Название "+o1.columnName,o1.displayName);
                list.repaint();
                break;
            case "VISIBLE":
                o1 = (GridColumnInfo)list.getSelectedValue();
                o1.visible = !o1.visible;
                list.repaint();
                break;

            default:
                System.out.println(command);
        }
    }

    @Override
    public void updateAction(Action action) {
    }
};


            
    class ColumnGrid extends Grid{
        
        public Integer grid_id = 100; 
        
        Action actSaveColumns = new AbstractAction("Сохранить"){

            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    readColumnInfoList().saveToFile("grid"+grid_id+".txt");
                    JOptionPane.showMessageDialog(null,"Настройки сохранены");
                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        };
        Action actColumnDisiner = new AbstractAction("Настройка") {

            @Override
            public void actionPerformed(ActionEvent e) {
                testColumn();
            }
        };

        @Override
        public void setDataset(Dataset dataset) throws Exception {
            super.setDataset(dataset); 
            if (grid_id!=null)
                try{
                    GridColumnInfoList list = new GridColumnInfoList();
                    if (list.loadFromFile("grid"+grid_id+".txt")){
                        applyColumnInfoList(list);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
        }
        
        
        

        @Override
        public JPopupMenu getPopupMenu() {
            JPopupMenu result = super.getPopupMenu();
            if (grid_id!=null){
                result.addSeparator();
                result.add(actColumnDisiner);
                result.add(actSaveColumns);
            }
            return result; 
        }
        
        public void testColumn(){
            
            // Это всё в гриде
//            GridColumnInfoList listInfo = readColumnInfoList();
            
            ColumnDialog columnDialog = new ColumnDialog(){

                @Override
                public void doOnEntry() throws Exception {
                    applyColumnInfoList(getColumnInfoList());
                }
            };
            
            columnDialog.setColumnInfoList(readColumnInfoList());
            columnDialog.showModal(null);
        }
        
        /**
         * Применить сохранённые настройки полей к таблице
         * @param list 
         */
        public void applyColumnInfoList(GridColumnInfoList list){
            Dataset dataset = getDataset();
            for (GridColumnInfo info:list){
               
                Column column = dataset.getColumn(info.columnName);
                
                for (int i=0;i<columnModel.getColumnCount();i++){
                    TableColumn tcolumn = columnModel.getColumn(i);
                    if (tcolumn.getIdentifier()==column){
                        System.out.println(info+"  OK  "+column.getColumnName());
                        System.out.println("move from "+i+" to "+info.columnOrder);
                        if (info.visible && tcolumn.getWidth()==0){
                            tcolumn.setMinWidth(15);
                            tcolumn.setMaxWidth(600);
                            tcolumn.setPreferredWidth(75);
                        } else if (info.visible){
                            tcolumn.setPreferredWidth(info.size);
                        } else if (!info.visible){
                            tcolumn.setMinWidth(0);
                            tcolumn.setMaxWidth(0);
                            tcolumn.setPreferredWidth(0);
                        }
                        tcolumn.setHeaderValue(info.displayName);
                        columnModel.moveColumn(i, info.columnOrder);
                        break;
                    }
                }
            }
            setColumnModel(columnModel);
        }
        
        /**
         * Прочитать текущее настройки колонок
         */
        public GridColumnInfoList readColumnInfoList(){
            GridColumnInfoList result = new GridColumnInfoList();
            GridColumnInfo info;
            TableColumn tcolumn;
            for (int i=0;i<columnModel.getColumnCount();i++){
                tcolumn = columnModel.getColumn(i);
                Object t = tcolumn.getIdentifier();
                if ( t instanceof Column){
                    Column c = (Column)t;
                    info= new GridColumnInfo(i,c.getColumnName() );
                    info.displayName = (String)tcolumn.getHeaderValue();
                    info.size = tcolumn.getWidth();
                    info.visible = (info.size>0);
                    result.add(info);
                }
            }
            return result;
        }
    }
    
public class TestColumn  extends JPanel{
    
    ColumnGrid grid = new ColumnGrid();

    public TestColumn() {
        setPreferredSize(new Dimension(800,600));
        setLayout(new BorderLayout());
        add(new JScrollPane(grid));
    }
    
    public void open() throws Exception{
        DataModule.open();
        Dataset dataset = DataModule.getDataset("v_schedule");
        grid.setDataset(dataset);
        dataset.open();
        
    }
    
    public static void main(String[] args){
        TestColumn panel = new TestColumn();
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.pack();
        frame.setVisible(true);
        try{
           
           panel.open();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
}
