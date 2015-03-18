/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author вадик
 */
class DatasetInfo {
    String tableName;
    String primaryKey = "";
    Map<String, String> references;

    public DatasetInfo() {
        references = new HashMap<>();
    }

    public void addPrimaryKey(String column_name) {
        if (!primaryKey.isEmpty()) {
            primaryKey += ";";
        }
        primaryKey += column_name;
    }

    @Override
    public String toString() {
        String result = "";
        for (String s : references.keySet()) {
            result += s + "=" + references.get(s) + "\n";
        }
        return "table " + tableName + "\n primary:" + primaryKey + "\n" + result;
    }
    
}
