package com.example.FAMS.repositories;

import com.example.FAMS.dto.responses.CalendarDayResponse;
import com.example.FAMS.dto.responses.CalendarWeekResponse;
import com.example.FAMS.dto.responses.SearchFilterResponse;
import com.example.FAMS.models.Class;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ClassDAO extends JpaRepository<Class, String> {

    List<Class> findTop1000ByOrderByCreatedDateDesc();


    Class findByLocation(String location);

    @Query(value = "select * from fams.class order by start_date asc, time_from asc", nativeQuery = true)
    List<Class> getCalendarSort();

    @Query(value = "Select * from Class order by modified_date desc", nativeQuery = true)
    List<Class> getAll();

    @Query(value = "SELECT fc.location, fc.time_from as 'timeFrom', fc.time_to as 'timeTo', fc.fsu, fc.status, cu.users_id AS class_user_id, u.name\n" +
            "FROM class AS fc\n" +
            "INNER JOIN class_user AS cu ON fc.class_code = cu.class_id\n" +
            "INNER JOIN users AS u ON cu.users_id = u.user_id\n" +
            "WHERE  u.role = 2;", nativeQuery = true)
    List<SearchFilterResponse> searchByFilter();

    @Query(
            value =
                    "SELECT a.class_code AS 'classCode',\n"
                            + "       g.time_from AS 'timeFrom',\n"
                            + "       g.time_to AS 'timeTo',\n"
                            + "       a.start_date AS 'startDate',\n"
                            + "       a.end_date AS 'endDate',\n"
                            + "       a.status,\n"
                            + "       b.user_type AS 'userType',\n"
                            + "       c.name,\n"
                            + "       f.number_of_day AS 'numberOfDay'\n"
                            + "FROM class a\n"
                            + "LEFT JOIN class_learning_day g ON g.enroll_date = :currentDate AND a.class_code = g.class_id\n"
                            + "LEFT JOIN training_programs d ON d.training_program_code = a.training_program_code\n"
                            + "LEFT JOIN training_program_syllabuses e ON e.training_programs_code = d.training_program_code\n"
                            + "LEFT JOIN syllabus f ON f.topic_code = e.topic_code\n"
                            + "LEFT JOIN class_user b ON a.class_code = b.class_id\n"
                            + "LEFT JOIN users c ON b.users_id = c.user_id\n"
                            + "WHERE (b.user_type = 'SUPER_ADMIN' OR b.user_type = 'CLASS_ADMIN')\n"
                            + "  AND a.status = 'Active'\n"
                            + "GROUP BY a.class_code,\n"
                            + "         g.time_from,\n"
                            + "         g.time_to,\n"
                            + "         a.start_date,\n"
                            + "         a.end_date,\n"
                            + "         a.status,\n"
                            + "         b.user_type,\n"
                            + "         c.name,\n"
                            + "         f.number_of_day;",
            nativeQuery = true)
    List<CalendarDayResponse> getCalendarByDay(@Param("currentDate") Date currentDate);

    @Query(
            value =
                    "SELECT a.class_code as 'classCode', g.time_from as 'timeFrom', g.time_to as 'timeTo', a.start_date as 'startDate', a.end_date as 'endDate', a.status, g.enroll_date as 'enrollDate'\n"
                            + "FROM class a \n"
                            + "left join class_learning_day g on a.class_code = g.class_id\n"
                            + "WHERE g.enroll_date >= :startDate and g.enroll_date <= :endDate",
            nativeQuery = true)
    List<CalendarWeekResponse> getCalendarByWeek(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

}
