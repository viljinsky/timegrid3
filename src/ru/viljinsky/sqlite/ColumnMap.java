package ru.viljinsky.sqlite;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import java.util.HashMap;

/**
 *
 * @author вадик
 */
/**
 * Преобразование отображения таблицы
 * @author вадик
 */
public class ColumnMap extends HashMap<String, String[]> {
    private static ColumnMap instance = null;
    private static final String[][] alisaes = {
        // stream
        {"stream.id",""},
        {"stream.stream_caption",""},
        {"stream.subject_id",""},
        {"stream.skill_id",""},
        {"stream.room_id",""},
        {"stream.teacher_id",""},

        // sqlite_sequence
        {"sqlite_sequence.name",""},
        {"sqlite_sequence.seq",""},

        // group_sequence
        {"group_sequence.id",""},
        {"group_sequence.group_sequence_name",""},

        // week
        {"week.id",""},
        {"week.caption",""},

        // building
        {"building.id",""},
        {"building.building_name",""},

        // room
        {"room.id",",false"},
        {"room.room_name","Номер(Назавние)"},
        {"room.capacity","Вместимость(чел.)"},
        {"room.building_id","Здание"},
        {"room.profile_id","Профиль"},
        {"room.shift_id","График"},

        // subject
        {"subject.id",""},
        {"subject.subject_name","Название"},
        {"subject.default_hour_per_week","Кол час./нед (по умолчанию)"},
        {"subject.default_group_type_id","Тип групы (по умолчанию)"},
        {"subject.default_hour_per_day","Кол час./день"},
        {"subject.color","Цвет"},

        // profile_type
        {"profile_type.id",""},
        {"profile_type.caption",""},
        {"profile_type.default_profile_id",""},

        // profile
        {"profile.id",""},
        {"profile.profile_type_id",""},
        {"profile.profile_name",""},

        // profile_item
        {"profile_item.profile_id",""},
        {"profile_item.subject_id",""},

        // shift_type
        {"shift_type.id",""},
        {"shift_type.caption",""},
        {"shift_type.default_shift_id",""},

        // shift
        {"shift.id",""},
        {"shift.shift_type_id",""},
        {"shift.shift_name",""},

        // shift_detail
        {"shift_detail.shift_id",""},
        {"shift_detail.day_id",""},
        {"shift_detail.bell_id",""},
        {"shift_detail.enable",""},

        // teacher
        {"teacher.id",",false"},
        {"teacher.last_name","Фамилия"},
        {"teacher.first_name","Имя"},
        {"teacher.patronymic","Отчество"},
        {"teacher.photo","Фотография"},
        {"teacher.profile_id","Профиль"},
        {"teacher.shift_id","График"},
        {"teacher.teacher_room_id","Кабиент"},
        {"teacher.comments","Примечания"},

        // skill
        {"skill.id",""},
        {"skill.caption",""},

        // depart
        {"depart.id",",false"},
        {"depart.label","Метка"},
        {"depart.skill_id","Уровень,false"},
        {"depart.shift_id","График"},
        {"depart.curriculum_id","Уч.план,false"},
        {"depart.class_room","Кл.помещение"},
        {"depart.class_former","Кл.руководитель"},
        {"depart.boy_count","Кол.мальчиков"},
        {"depart.gerl_count","Кол.девочек"},
        {"depart.schedule_state_id","Статус расписания"},

        // subject_group
        {"subject_group.group_id",""},
        {"subject_group.depart_id",""},
        {"subject_group.subject_id",""},
        {"subject_group.default_teacher_id","Преподаватель"},
        {"subject_group.default_room_id","Кабинет"},
        {"subject_group.week_id","Неделя"},
        {"subject_group.stream_id","Поток"},
        {"subject_group.pupil_count","Кол.учащихся"},

        // curriculum
        {"curriculum.id",""},
        {"curriculum.caption",""},

        // group_type
        {"group_type.id",""},
        {"group_type.group_type_caption",""},

        // curriculum_detail
        {"curriculum_detail.curriculum_id",",false"},
        {"curriculum_detail.skill_id",",false"},
        {"curriculum_detail.subject_id",",false"},
        {"curriculum_detail.hour_per_day","Кол.часов в день"},
        {"curriculum_detail.hour_per_week","Кол.часов в неделю"},
        {"curriculum_detail.group_type_id","Тип группы"},
        {"curriculum_detail.group_sequence_id","Посл.-ть"},
        {"curriculum_detail.is_stream","Поток"},

        // day_list
        {"day_list.day_no",""},
        {"day_list.day_caption",""},

        // bell_list
        {"bell_list.bell_id",""},
        {"bell_list.time_start",""},
        {"bell_list.time_end",""},

        // schedule
        {"schedule.day_id",""},
        {"schedule.bell_id",""},
        {"schedule.depart_id",""},
        {"schedule.subject_id",""},
        {"schedule.group_id",""},
        {"schedule.teacher_id",""},
        {"schedule.room_id",""},
        {"schedule.ready",""},

        // v_teacher_profile
        {"v_teacher_profile.subject_name",""},
        {"v_teacher_profile.subject_id",""},
        {"v_teacher_profile.teacher_id",""},
        {"v_teacher_profile.profile_id",""},

        // v_room_profile
        {"v_room_profile.subject_name",""},
        {"v_room_profile.subject_id",""},
        {"v_room_profile.room_id",""},
        {"v_room_profile.profile_id",""},

        // v_subject_group
        {"v_subject_group.week_id",         ",false"},
        {"v_subject_group.subject_name",    "Предмет"},
        {"v_subject_group.group_label",     "Группа"},
        {"v_subject_group.week_caption",    "Неделя"},
        {"v_subject_group.group_id",        ",false"},
        {"v_subject_group.depart_id",       ",false"},
        {"v_subject_group.subject_id",      ",false"},
        {"v_subject_group.group_type_id",   ",false"},
        {"v_subject_group.stream_id",       ",false"},
        {"v_subject_group.hour_per_week",   "Час./нед."},
        {"v_subject_group.hour_per_day",    "Час./день"},
        {"v_subject_group.group_sequence_id",",false"},
        {"v_subject_group.default_teacher_id",",false"},
        {"v_subject_group.default_room_id", ",false"},
        {"v_subject_group.pupil_count",     "Кол.чел."},
        {"v_subject_group.teacher",         "Преподаватель"},
        {"v_subject_group.room",            "Кабинет"},

        // v_subject_group_on_schedule
        {"v_subject_group_on_schedule.group_label","Группа"},
        {"v_subject_group_on_schedule.depart_id",""},
        {"v_subject_group_on_schedule.subject_id",""},
        {"v_subject_group_on_schedule.group_id",""},
        {"v_subject_group_on_schedule.hour_per_week",""},
        {"v_subject_group_on_schedule.hour_per_day",""},
        {"v_subject_group_on_schedule.group_type_id",""},
        {"v_subject_group_on_schedule.default_teacher_id",""},
        {"v_subject_group_on_schedule.default_room_id",""},
        {"v_subject_group_on_schedule.placed",""},
        {"v_subject_group_on_schedule.unplaced","Не расставлено"},
        {"v_subject_group_on_schedule.stream_id",""},
        {"v_subject_group_on_schedule.group_sequence_id",""},
        {"v_subject_group_on_schedule.pupil_count",""},

        // v_schedule
        {"v_schedule.depart_label",""},
        {"v_schedule.day_caption",""},
        {"v_schedule.lesson_time",""},
        {"v_schedule.subject_name",""},
        {"v_schedule.group_label",""},
        {"v_schedule.teacher",""},
        {"v_schedule.room",""},
        {"v_schedule.building",""},
        {"v_schedule.day_id",""},
        {"v_schedule.bell_id",""},
        {"v_schedule.depart_id",""},
        {"v_schedule.group_id",""},
        {"v_schedule.week_id",""},
        {"v_schedule.subject_id",""},
        {"v_schedule.group_type_id",""},
        {"v_schedule.teacher_id",""},
        {"v_schedule.room_id",""},
        {"v_schedule.stream_id",""},
        {"v_schedule.building_id",""},
        {"v_schedule.ready",""},
        {"v_schedule.color",""},

        // v_room
        {"v_room.building_name","Здание"},
        {"v_room.room_name","Номер"},
        {"v_room.profile_name","Профиль"},
        {"v_room.shift_name","График"},
        {"v_room.capacity","Вместимость(чел.)"},
        {"v_room.id",",false"},
        {"v_room.building_id",",false"},
        {"v_room.profile_id",",false"},
        {"v_room.shift_id",",false"},

        // v_depart_on_schedule
        {"v_depart_on_schedule.depart_id",""},
        {"v_depart_on_schedule.subject_id",""},
        {"v_depart_on_schedule.group_type_id",""},
        {"v_depart_on_schedule.placed",""},
        {"v_depart_on_schedule.hour_per_week",""},

        // v_curriculum_detail
        {"v_curriculum_detail.subject_name","Предмет"},
        {"v_curriculum_detail.group_sequence_name","Посл.ть"},
        {"v_curriculum_detail.group_type_caption","Тип группы"},
        {"v_curriculum_detail.hour_per_week","Час./Нед."},
        {"v_curriculum_detail.hour_per_day","Час./День"},
        {"v_curriculum_detail.is_stream","Поток"},
        {"v_curriculum_detail.subject_id",",false"},
        {"v_curriculum_detail.curriculum_id",",false"},
        {"v_curriculum_detail.group_type_id",",false"},
        {"v_curriculum_detail.group_sequence_id",",false"},
        {"v_curriculum_detail.skill_id",",false"},

        // v_curriculum
        {"v_curriculum.curriculum_id",""},
        {"v_curriculum.curriculum",""},
        {"v_curriculum.skill_id",""},
        {"v_curriculum.skill",""},

        // v_schedule_calc
        {"v_schedule_calc.day_id",""},
        {"v_schedule_calc.bell_id",""},
        {"v_schedule_calc.depart_id",""},
        {"v_schedule_calc.group_id",""},
        {"v_schedule_calc.group_type_id",""},

        // v_depart
        {"v_depart.depart_label","Класс"},
        {"v_depart.shift_name","График"},
        {"v_depart.curriculum_caption","Уч.план"},
        {"v_depart.teacher","Кл.руководитель"},
        {"v_depart.room","Кл.помещение"},
        {"v_depart.main_building","Здание"},
        {"v_depart.boy_count","Кол.мальчиков"},
        {"v_depart.gerl_count","Кол.девочек"},
        {"v_depart.depart_id",",false"},
        {"v_depart.curriculum_id",",false"},
        {"v_depart.skill_id",",false"},
        {"v_depart.shift_id",",false"},
        {"v_depart.state_description","Статус"},

        // user_role
        {"user_role.id",""},
        {"user_role.role_name",""},

        // users
        {"users.id",""},
        {"users.user_name",""},
        {"users.password",""},
        {"users.nick_name",""},
        {"users.user_role_id",""},

        // v_teacher
        {"v_teacher.teacher_fio","Фамилия И.О."},
        {"v_teacher.last_name","Фамилия"},
        {"v_teacher.first_name","Имя"},
        {"v_teacher.patronymic","Отчество"},
        {"v_teacher.profile_name","Профиль"},
        {"v_teacher.room_name","Кабинет"},
        {"v_teacher.building_name","Здание"},
        {"v_teacher.shift_name","График"},
        {"v_teacher.total_hour","Загрузка(ч./нед)"},
        {"v_teacher.id",",false"},
        {"v_teacher.profile_id",",false"},
        {"v_teacher.teacher_room_id",",false"},
        {"v_teacher.shift_id",",false"},

        // v_teacher_hour
        {"v_teacher_hour.default_teacher_id",""},
        {"v_teacher_hour.group_sequence_id",""},
        {"v_teacher_hour.total_hour",""}
};

    private ColumnMap() {
        for (String[] str : alisaes) {
            put(str[0], str[1].split(","));
        }
    }

    /**
     * Получение параметров поля по ключу <имя таблицы><точка><имя поля>
     * @param str
     * @return список строк Отображаемое имя,Видимость,Начальная ширина
     */
    public static String[] getParams(String str) {
        if (instance == null) {
            instance = new ColumnMap();
        }
        return instance.get(str);
    }
    
}
