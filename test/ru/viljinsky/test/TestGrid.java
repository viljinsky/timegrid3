/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.test;

import ru.viljinsky.ColumnMap;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import ru.viljinsky.Column;
import ru.viljinsky.DataModule;
import ru.viljinsky.Dataset;
import ru.viljinsky.DatasetInfo;
import ru.viljinsky.Grid;
import ru.viljinsky.IGridCommand;
import ru.viljinsky.IDataset;

/**
 *
 * @author вадик
 */



public class TestGrid  extends JPanel{
    Grid grid = new Grid();
    Action a1 = new AbstractAction("act1") {
        
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("act1");
            showGridColumns();
        }
    };
    
    Action a2 = new AbstractAction("act2") {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("act2");
        }
    };
    Action[] actionList = {a1,a2};
    
    
    public void showGridColumns(){
        TableColumnModel model =  grid.getColumnModel();
        TableColumn column;
        IDataset dataset = grid.getDataset();
        for (int i=0;i<model.getColumnCount();i++){
            column =model.getColumn(i);
            Column c = (Column)column.getIdentifier();
            System.out.println("-->"+c.getTableName()+"."+c.getColumnName());
            String als = c.getTableName()+"."+c.getColumnName();
            String[] p =  ColumnMap.getParams(als);
            if (p!=null){
                if (p.length>0){
                    System.out.println("alias="+ p[0]);
                    column.setHeaderValue( p[0].isEmpty()?c.getColumnName():p[0]);
                }
                if (p.length>1 && !p[1].isEmpty()){
                    if (p[1].equals("false")){
                        column.setMaxWidth(0);
                        column.setMinWidth(0);
                        column.setPreferredWidth(0);
                    }
                }
                if (p.length>2){
                    int w = Integer.valueOf(p[2]);
                    column.setPreferredWidth(w);
                }
            }
        }
        grid.revalidate();
    }
    
    public TestGrid() {
        super(new BorderLayout());
        
        setPreferredSize(new Dimension(800,600));
        
        JScrollPane scrollPane = new JScrollPane(grid);
        add(scrollPane);
        
        grid.setCommands(new IGridCommand(){

            @Override
            public void doCommand(String command) {
            }

            @Override
            public void updateAction(Action a) {
            }

            @Override
            public void updateActionList() {
            }

            @Override
            public JPopupMenu getPopup() {
                JPopupMenu menu = new JPopupMenu();
                for (Action a:actionList)
                    menu.add(a);
                return menu;
            }

            @Override
            public void addMenu(JMenu menu) {
                
            }
            
        });
        
       
                
    }
    
    public void open() throws Exception{
        Dataset dataset = DataModule.getDataset("v_depart");
        dataset.open();
        grid.setDataset(dataset);
        showGridColumns();
    }
    
    public static void main(String[] args) throws Exception{
        TestGrid panel = new TestGrid();
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.pack();
        frame.setVisible(true);
        try{
            DataModule.open();
            panel.open();

            Dataset dataset;
            for (DatasetInfo info:DataModule.getInfoList()){
                dataset = DataModule.getDataset(info.getTableName());
                System.out.println("// "+info.getTableName());
                for (Column column:dataset.getColumns()){
                    System.out.println(String.format("{\"%s.%s\",\"\"},", column.getTableName(),column.getColumnName()));
                }
                System.out.println();
            }
            
        } catch (Exception e){
            e.printStackTrace();
        }
    };
    
}
