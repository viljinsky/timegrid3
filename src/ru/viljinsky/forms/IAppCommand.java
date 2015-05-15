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
    
    // curriculumn panel
    
    public static final String CREATE_CURRICULUM    = "CREATE_CURRICULUM";
    public static final String EDIT_CURRICULUM      = "EDIT_CURRICULUM";
    public static final String DELETE_CURRICULUM    = "DELETE_CURRICULUM";
    public static final String FILL_CURRICULUM      = "FILL_CURRICULUM";
    public static final String CLEAR_CURRICULUM     = "CLEAR_CURRICULUM";
    public static final String EDIT_CURRICULUM_DETAIL = "EDITCURRICULUM_DETAIL";
    
    // depart panel
    
    public static final String CREATE_DEPART        = "CREATE_DEPART";
    public static final String EDIT_DEPART          = "EDIT_DEPART";
    public static final String DELETE_DEPART        = "DELETE_DEPART";
    
    public static final String FILL_GROUP           = "FILL_GROUP";
    public static final String CLEAR_GROUP          = "CLEAR";
//    public static final String EDIT_SHIFT ="EDIT_SHIFT";
    public static final String ADD_GROUP            = "ADD_GROUP";
    public static final String EDIT_GROUP           = "EDIT_GROUP";
    public static final String DELETE_GROUP         = "DELETE_GROUP";
    
    public static final String ADD_STREAM           = "ADD_STREAM";
    public static final String EDIT_STREAM          = "EDIT_STREAM";
    public static final String REMOVE_STREAM        = "REMOVE_STREAM";

    // teacher
    
    public static final String CREATE_TEACHER       = "CREATE_TEACHER";
    public static final String EDIT_TEACHER         = "EDIT_TEACHER";
    public static final String DELETE_TEACHER       = "DELETE_TEACHER";
    
    // room
    public static final String CREATE_ROOM          = "CREATE_ROOM";
    public static final String EDIT_ROOM            = "EDIT_ROOM";
    public static final String DELETE_ROOM          = "DELETE_ROOM";
    
    // shift panel
    public static final String CREATE_SHIFT         = "CREATE_SHIFT";
    public static final String REMOVE_SHIFT         = "REMOVE_SHIFT";
    public static final String EDIT_SHIFT           = "EDIT_SHIFT";
    
    // profile_panel
    public static final String CREATE_PROFILE       = "CREATE_PROFILE";
    public static final String EDIT_PROFILE         = "EDIT_PROFILE";
    public static final String REMOVE_PROFILE       = "REMOVE_PROFILE";
    
    // schedule_panel
    
    public static final String TT_CLEAR     = "TT_CLEAR";
    public static final String TT_DELETE    = "TT_DELETE";
    public static final String TT_PLACE     = "TT_PLACE";
    public static final String TT_PLACE_ALL = "TT_PLACE_ALL";
    public static final String TT_FIX       = "TT_FIX";
    public static final String TT_UNFIX     = "TT_UNFIX";
    public static final String TT_REFRESH   = "TT_REFRESH";
    
    
    
    public static final String[] TEACHER_COMMANDS ={
        CREATE_TEACHER, EDIT_TEACHER, DELETE_TEACHER,
        CREATE_SHIFT,EDIT_SHIFT,REMOVE_SHIFT,
        CREATE_PROFILE,EDIT_PROFILE,REMOVE_PROFILE};
    
    public static final String[] ROOM_COMMANDS ={
        CREATE_ROOM,EDIT_ROOM,DELETE_ROOM,
        CREATE_SHIFT,EDIT_SHIFT,REMOVE_SHIFT,
        CREATE_PROFILE,EDIT_PROFILE,REMOVE_PROFILE};
    
    public static final String[] SCHEDULE_COMMANDS ={
             TT_PLACE_ALL,
             TT_PLACE,
             TT_DELETE,
             TT_FIX,
             TT_UNFIX,
             TT_CLEAR,
             TT_REFRESH   
    };
    
    public static final String[] CURRICULUM_COMMANDS = {
        CREATE_CURRICULUM,EDIT_CURRICULUM,DELETE_CURRICULUM,
        FILL_CURRICULUM,CREATE_DEPART,EDIT_CURRICULUM_DETAIL        
    };
    
    
}
    
