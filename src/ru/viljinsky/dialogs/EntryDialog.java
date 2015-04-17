/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.dialogs;

import java.awt.Container;
import java.util.Map;
import ru.viljinsky.IDataset;
import ru.viljinsky.Values;

/**
 *
 * @author вадик
 */
public abstract class EntryDialog extends BaseDialog {
    protected EntryPanel entryPanel;

    @Override
    public Container getPanel() {
        entryPanel = new EntryPanel();
        return entryPanel;
    }

    public void setDataset(IDataset dataset) {
        entryPanel.setDataset(dataset);
    }
    
    public void setValues(Map<String,Object> values){
        entryPanel.setValues(values);
    }
    
    public Values getValues(){
        return entryPanel.getValues();
    }
    
}
