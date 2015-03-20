/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

/**
 *
 * @author вадик
 */
//------------------------------------------------------------------------------


public class Main extends JFrame {
    JTabbedPane tabs = new JTabbedPane();
    Action[] actions = {new Act("new"),new Act("open"),new Act("close"),null,new Act("exit")};
    
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

    protected void doCommand(String command){
        try{
            switch (command){
                case "new":
                    newData();
                    break;
                case "open":
                    openData();
                    break;
                case "close":
                    closeData();
                    break;
                case "exit":
                    System.exit(0);
                    break;
                default:
                    System.out.println(command);
            }
        } catch (Exception e){
            JOptionPane.showMessageDialog(rootPane, e.getMessage());
        }
    }
    
    DataModule dataModule = DataModule.getInstance();
    public Main(){
        Container content = getContentPane();
        content.setPreferredSize(new Dimension(800,600));
        content.setLayout(new BorderLayout());
        content.add(tabs);
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        for (Action a:actions){
            if (a==null)
                menu.addSeparator();
            else
                menu.add(a);
        }
        menuBar.add(menu);
        setJMenuBar(menuBar);
        
    }
    
    public void newData() throws Exception{
        if (dataModule.isActive()){
            dataModule.close();
        }
        File file = new File(".");
        JFileChooser fc = new JFileChooser(file);
        int retVal=fc.showSaveDialog(rootPane);
        if (retVal==JFileChooser.APPROVE_OPTION){
            file = fc.getSelectedFile();
            String path= file.getPath();
        }
    }
    
    public void openData() throws Exception{
        File file = new File(".");
        JFileChooser fc = new JFileChooser(file);
        int retVal = fc.showOpenDialog(rootPane);
        if (retVal==JFileChooser.APPROVE_OPTION){
            file = fc.getSelectedFile();
            dataModule.open(file.getPath());
            onDataOpen();
        }
    }
    
    private void onDataOpen() throws Exception{
        Dataset dataset;
        Grid grid;
        for (DatasetInfo info :dataModule.infoList){
            if (!info.isTable())
                continue;
            dataset = dataModule.getDataset(info.tableName);
            dataset.open();
            grid = new Grid();
            grid.owner=Main.this;
            grid.setDataset(dataset);
            tabs.addTab(dataset.getTableName(), new JScrollPane(grid));

            for (int i=0;i<dataset.getColumnCount();i++){
                System.out.println(dataset.getColumn(i).toString()+"\n");
            }

        }
    }
    
    public void closeData() throws Exception{
        dataModule.close();
        tabs.removeAll();
    }
    
    public void open(){
        Grid grid;
        Dataset dataset ;
        try{
            dataModule.open();
            onDataOpen();

        } catch (Exception e){
            JOptionPane.showMessageDialog(rootPane, e.getMessage());
        }
    }
    
    public static void main(String[] args){
        Main frame = new Main();
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.open();
    }

}
