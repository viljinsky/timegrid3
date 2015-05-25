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
public class Depart extends TreeElement {
    
    public static final String sql_by_group =
            "select distinct b.label,b.id as depart_id,a.group_id,c.group_type_id,g.group_type_caption\n" +
            "from subject_group a inner join curriculum_detail c\n" +
            "on c.skill_id=b.skill_id and c.subject_id=a.subject_id\n" +
            "inner join depart b on a.depart_id=b.id\n" +
            "inner join group_type g on g.id=c.group_type_id\n" +
            "order by b.skill_id,b.id,c.group_type_id,a.group_id;";
    
    public static final String sql = "select day_id-1,bell_id-1 from shift_detail a inner join " + "depart b on a.shift_id=b.shift_id where b.id=%d;";

    public Depart(Values values) throws Exception {
        id = values.getInteger("id");
        label = values.getString("label");
    }

    @Override
    public Values getFilter() {
        Values result = new Values();
        result.put("depart_id", id);
        return result;
    }

    @Override
    public Set<Point> getAvalabelCells() {
        Set<Point> result = new HashSet<>();
        Object[] p;
        try {
            Recordset resordset = DataModule.getRecordet(String.format(sql, id));
            for (int i = 0; i < resordset.size(); i++) {
                p = resordset.get(i);
                result.add(new Point((Integer) p[0], (Integer) p[1]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
}
