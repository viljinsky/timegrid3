package ru.viljinsky.reports;

import java.util.HashMap;
import java.util.Map;
import ru.viljinsky.sqlite.DataModule;
import ru.viljinsky.sqlite.Dataset;
import ru.viljinsky.sqlite.Values;

/**
 *
 * @author вадик
 */


public class ScheduleParams {
    private static ScheduleParams instance = null;
    public static final String DATE_BEGIN="date_begin";
    public static final String DATE_END="date_end";
    public static final String SCHEDULE_SPAN = "schedule_span";
    public static final String SCHEDULE_TITLE = "schedule_title";
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
        return "null" ;
    }
    
    
}
