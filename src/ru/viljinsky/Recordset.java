/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky;

import java.util.ArrayList;

/**
 *
 * @author вадик
 */
public class Recordset extends ArrayList<Object[]> {
    public Integer getInteger(int columnIndex) throws Exception{
        if (isEmpty()){
            throw new Exception("RECORDSET IS EMPTY");
        }
        Object[] result = get(0);
        return (Integer)result[columnIndex];
    }
    
}
