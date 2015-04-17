/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky;

import java.awt.Color;
import javax.swing.JColorChooser;

/**
 *
 * @author вадик
 */
abstract class ColorDialog extends BaseDialog {
    Color color;
    JColorChooser colorChooser;

    public ColorDialog(Color color) {
        super();
        this.color = color;
        colorChooser = new JColorChooser(color);
        add(colorChooser);
    }
    
}
