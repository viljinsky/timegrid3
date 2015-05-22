/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.viljinsky.forms;

/**
 *
 * @author вадик
 */
public interface IAppCommand {

    public static final String REFRESH = "REFRESH";
    
    public static final String GRID_APPEND      = "GRID_APPEND";
    public static final String GRID_EDIT        = "GRID_EDIT";
    public static final String GRID_DELETE      = "GRID_DELETE";
    public static final String GRID_REFRESH     = "GRID_REFRESH";
    public static final String GRID_REQUERY     = "GRID_REQUERY";
    
    

    // shift panel
    public static final String CREATE_SHIFT         = "CREATE_SHIFT";
    public static final String REMOVE_SHIFT         = "REMOVE_SHIFT";
    public static final String EDIT_SHIFT           = "EDIT_SHIFT";
    
    // profile_panel
    public static final String CREATE_PROFILE       = "CREATE_PROFILE";
    public static final String EDIT_PROFILE         = "EDIT_PROFILE";
    public static final String REMOVE_PROFILE       = "REMOVE_PROFILE";
    
    
    // report_panel
    
    public static final String PAGE_HOME             = "PAGE_HOME";
    public static final String PAGE_PRIOR            = "PAGE_PRIOR";
    public static final String PAGE_NEXT             = "PAGE_NEXT";
    public static final String PAGE_RELOAD           = "PAGE_RELOAD";
    
    public static final String[] REPORT_COMMANDS     ={
        PAGE_HOME,
        PAGE_PRIOR,
        PAGE_NEXT,
        PAGE_RELOAD
    };
    
    // select_panel
    static final String INCLUDE = "INCLUDE";
    static final String EXCLUDE = "EXCLUDE";
    static final String INCLUDE_ALL = "INCLUDE_ALL";
    static final String EXCLUDE_ALL = "EXCLUDE_ALL";
    
    
    
    
    
    // schedule_panel
    
    public static final String TT_CLEAR     = "TT_CLEAR";
    public static final String TT_DELETE    = "TT_DELETE";
    public static final String TT_PLACE     = "TT_PLACE";
    public static final String TT_PLACE_ALL = "TT_PLACE_ALL";
    public static final String TT_FIX       = "TT_FIX";
    public static final String TT_UNFIX     = "TT_UNFIX";
    public static final String TT_REFRESH   = "TT_REFRESH";
    public static final String TT_SCH_STATE = "TT_SCH_STATE";
    
    
    public static final String[] SCHEDULE_COMMANDS ={
        TT_PLACE_ALL,
        TT_PLACE,
        TT_DELETE,
        TT_FIX,
        TT_UNFIX,
        TT_CLEAR,
        TT_REFRESH,
        TT_SCH_STATE
    };
    
    // depart panel
    
    public static final String CREATE_DEPART        = "CREATE_DEPART";
    public static final String EDIT_DEPART          = "EDIT_DEPART";
    public static final String DELETE_DEPART        = "DELETE_DEPART";
    
    public static final String FILL_GROUP           = "FILL_GROUP";
    public static final String CLEAR_GROUP          = "CLEAR";
    public static final String ADD_GROUP            = "ADD_GROUP";
    public static final String EDIT_GROUP           = "EDIT_GROUP";
    public static final String DELETE_GROUP         = "DELETE_GROUP";
    
    public static final String ADD_STREAM           = "ADD_STREAM";
    public static final String EDIT_STREAM          = "EDIT_STREAM";
    public static final String REMOVE_STREAM        = "REMOVE_STREAM";
    
    public static final String[] DEPART_COMMANDS ={
        EDIT_DEPART,
        DELETE_DEPART,
        FILL_GROUP,
        CLEAR_GROUP,
        EDIT_SHIFT,
        ADD_GROUP,
        DELETE_GROUP,
        ADD_STREAM,
        EDIT_STREAM,
        REMOVE_STREAM,
        REFRESH,
        EDIT_GROUP,
        TT_SCH_STATE
    };
    
    // teacher
    
    public static final String CREATE_TEACHER       = "CREATE_TEACHER";
    public static final String EDIT_TEACHER         = "EDIT_TEACHER";
    public static final String DELETE_TEACHER       = "DELETE_TEACHER";
    
    public static final String[] TEACHER_COMMANDS ={
        CREATE_TEACHER,
        EDIT_TEACHER,
        DELETE_TEACHER,
        CREATE_SHIFT,
        EDIT_SHIFT,
        REMOVE_SHIFT,
        CREATE_PROFILE,
        EDIT_PROFILE,
        REMOVE_PROFILE
    };
    
    // room
    public static final String CREATE_ROOM          = "CREATE_ROOM";
    public static final String EDIT_ROOM            = "EDIT_ROOM";
    public static final String DELETE_ROOM          = "DELETE_ROOM";
    
    public static final String[] ROOM_COMMANDS ={
        CREATE_ROOM,
        EDIT_ROOM,
        DELETE_ROOM,
        CREATE_SHIFT,
        EDIT_SHIFT,
        REMOVE_SHIFT,
        CREATE_PROFILE,
        EDIT_PROFILE,
        REMOVE_PROFILE
    };
    
    // curriculumn panel
    
    public static final String CREATE_CURRICULUM    = "CREATE_CURRICULUM";
    public static final String COPY_CURRICULUM      = "COPY_CURRICUUM";
    public static final String EDIT_CURRICULUM      = "EDIT_CURRICULUM";
    public static final String DELETE_CURRICULUM    = "DELETE_CURRICULUM";
    public static final String FILL_CURRICULUM      = "FILL_CURRICULUM";
    public static final String EDIT_CURRICULUM_DETAIL = "EDITCURRICULUM_DETAIL";
    
    public static final String[] CURRICULUM_COMMANDS = {
        CREATE_CURRICULUM,
        EDIT_CURRICULUM,
        DELETE_CURRICULUM,
        COPY_CURRICULUM,
        FILL_CURRICULUM,
        CREATE_DEPART,
        DELETE_DEPART,
        EDIT_CURRICULUM_DETAIL        
    };
    
    
    
}
    
