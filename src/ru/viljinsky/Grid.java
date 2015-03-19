/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author вадик
 */
class Grid extends JTable {
    Component owner = null;
    GridModel model;
    ICommand commands = null;

    public Grid() {
        super();
        commands = new GridCommand(this);
        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    gridSelectionChange();
                }
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        edit();
                        break;
                    case KeyEvent.VK_INSERT:
                        append();
                        break;
                    case KeyEvent.VK_DELETE:
                        delete();
                        break;
                }
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                showPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showPopup(e);
            }

            public void showPopup(MouseEvent e) {
                if (e.isPopupTrigger() && commands != null) {
                    JPopupMenu popupMenu = commands.getPopup();
                    popupMenu.show(Grid.this, e.getX(), e.getY());
                }
            }
        });
    }

    public void setDataset(Dataset dataset) {
        model = new GridModel(dataset);
        setModel(model);
    }

    abstract class AppendDialog extends DataEntryDialog {

        public AppendDialog(IDataset datset) {
            super();
            panel.setDataset(datset);
        }

        @Override
        public void doOnEntry() throws Exception {
            onAddValues();
        }

        public abstract void onAddValues() throws Exception;
    }

    public void append() {
        BaseDialog dlg = new AppendDialog(model.dataset) {
            @Override
            public void onAddValues() throws Exception {
                IDataset dataset = model.dataset;
                int row = dataset.appned(panel.getValues());
                model.fireTableDataChanged();
                getSelectionModel().setSelectionInterval(row, row);
                scrollRectToVisible(getCellRect(row, getSelectedColumn(), true));
            }
        };
        dlg.showModal(owner);
    }

    public void delete() {
        IDataset dataset = model.dataset;
        int row = getSelectedRow();
        if (row >= 0) {
            dataset.delete(row);
            model.fireTableRowsDeleted(row, row);
        }
    }

    class EditDialog extends DataEntryDialog {

        public EditDialog(IDataset dataset, Map<String, Object> values) {
            super();
            panel.setDataset(dataset);
            panel.setValues(values);
        }

        @Override
        public void doOnEntry() throws Exception {
            try {
                System.out.println(panel.getValues());
                int row = getSelectedRow();
                model.dataset.edit(row, panel.getValues());
            } catch (Exception e) {
            }
        }
    }
    
    abstract class EdtDialog extends DataEntryDialog{
        public EdtDialog(IDataset dataset,Map<String,Object> values){
            super();
            panel.setDataset(dataset);
            panel.setValues(values);
        }

        @Override
        public void doOnEntry() throws Exception {
            f1(panel.getValues());
        }
        
        abstract void f1(Map<String,Object> values) throws Exception;
        
    }

    public void edit() {
        int row = getSelectedRow();
        if (row >= 0) {
            Map<String, Object> values = model.dataset.getValues(row);
            
            BaseDialog dlg = new EdtDialog(model.dataset, values) {
                
                @Override
                void f1(Map<String, Object> values) throws Exception {
                    int row = getSelectedRow();
                    model.dataset.edit(row, values);
                }
            };
            dlg.showModal(owner);
//            EditDialog dlg = new EditDialog(model.dataset, values);
//            if (dlg.showModal(owner) == BaseDialog.RESULT_OK) {
//                JOptionPane.showMessageDialog(null, "OK");
//            }
        }
    }

    public void refresh() {
    }

    public void gridSelectionChange() {
        System.out.println("gridSelectionChange" + getSelectedRow());
        int row = getSelectedRow();
        if (row >= 0) {
            Map<String, Object> map = model.dataset.getValues(row);
            System.out.println(map);
        }
    }
    
}
