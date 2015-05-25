/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.timetree;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;
import ru.viljinsky.sqlite.DataModule;
import ru.viljinsky.sqlite.Recordset;
import ru.viljinsky.sqlite.Values;

/**
 *
 * @author вадик
 */
public class Room extends TreeElement {
    private static final String sql = "select day_id-1,bell_id-1 from shift_detail a inner join " + "room b on a.shift_id=b.shift_id where b.id=%d;";

    public Room(Values values) throws Exception {
        id = values.getInteger("id");
        label = values.getString("room_name");
    }

    @Override
    public Values getFilter() {
        Values result = new Values();
        result.put("room_id", id);
        return result;
    }

    @Override
    public Set<Point> getAvalabelCells() {
        Set<Point> result = new HashSet<>();
        Point p;
        Object[] r;
        try {
            Recordset recordset = DataModule.getRecordet(String.format(sql, id));
            for (int i = 0; i < recordset.size(); i++) {
                r = recordset.get(i);
                result.add(new Point((Integer) r[0], (Integer) r[1]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
}
