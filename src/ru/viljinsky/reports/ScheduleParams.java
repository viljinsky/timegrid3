package ru.viljinsky.reports;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import ru.viljinsky.sqlite.DataModule;
import ru.viljinsky.sqlite.Dataset;
import ru.viljinsky.sqlite.Values;

/**
 *
 * @author вадик
 */

/**
 * Статический класс хранить основные параметря учебного заведения и расписания
 * @author вадик
 */
public class ScheduleParams {
    private static ScheduleParams instance = null;
    /** День начала занятий по расписанию 1 сен 2015*/
    public static final String DATE_BEGIN       ="date_begin";
    /** Последний день начала занятий по расписанию 26 окт 2015 */
    public static final String DATE_END         ="date_end";
    /** Период занятий по расписанию  2015-216 УЧЕБНЫЙ ГОД*/
    public static final String SCHEDULE_SPAN    = "schedule_span";
    /** Заголовок расписания ПЕРВАЯ ЧЕТВЕРТЬ */
    public static final String SCHEDULE_TITLE   = "schedule_title";
    /** Название учебного заведения  ШКОЛА №121 КАЛИНИНСКОГО Р-НА */
    public static final String EDUCATIONAL_INSTITUTION="educational_institution";

    private Map<String,Object> map;

    protected ScheduleParams() throws Exception{
        Dataset dataset = DataModule.getDataset("attr");
        dataset.open();
        Values v;
        map = new HashMap<>();
        for (int i=0;i<dataset.size();i++){
            v=dataset.getValues(i);
            map.put(v.getString("param_name"), v.get("param_value"));
        }
    }
    
    public static ScheduleParams getInstance() throws Exception{
        if (instance==null){
            instance=new ScheduleParams();
        }
        return instance;
    }
    
    public static Integer getParamCount(){
        try{
            return getInstance().map.size();
        } catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }
    
    public static Set<String> getParamNames(){
        try{
            return getInstance().map.keySet();
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
    public static Object getParamByName(String paramName) throws Exception{
        Object result = null ;
        try{
            result = getInstance().map.get(paramName);
        } catch (Exception e){
        }
        return result;
    }
    
    public static String getStringParamByName(String paramName) throws Exception{
        Object p=getParamByName(paramName);
        if (p!=null){
            return p.toString();
        }
        return "*null*" ;
    }
    
    
}
