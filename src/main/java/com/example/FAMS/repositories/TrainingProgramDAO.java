package com.example.FAMS.repositories;

import com.example.FAMS.dto.responses.TrainingProgramDetails;
import com.example.FAMS.models.TrainingProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingProgramDAO extends JpaRepository<TrainingProgram, Integer> {
    <T> List<T> findBy(Class<T> classType);

    List<TrainingProgram> findTrainingProgramsBy();

    List<TrainingProgram> getAllBy();


    List<TrainingProgram> findTrainingProgramsByStatus(String status);

    TrainingProgram findByName(String trainingProgramName);

    Optional<TrainingProgram> getTrainingProgramByName(String name);


    @Query(
            value =
                    "SELECT SUM(s.number_of_day) as 'totalTrainingProgramDates'\n"
                            + "FROM training_programs tp\n"
                            + "LEFT JOIN training_program_syllabuses tps ON tps.training_programs_code = tp.training_program_code\n"
                            + "LEFT JOIN syllabus s ON s.topic_code = tps.topic_code\n"
                            + "WHERE tp.training_program_code = :code and tp.status = 'Active';",
            nativeQuery = true)
    Integer totalSubjectDays(@Param("code") int training_program_code);

    @Query(
            value =
                    "SELECT sum(tu.day_number) as 'totalSubjectDays'\n"
                            + "FROM training_programs tp\n"
                            + "LEFT JOIN training_program_syllabuses tps ON tps.training_programs_code = tp.training_program_code\n"
                            + "LEFT JOIN syllabus s ON s.topic_code = tps.topic_code\n"
                            + "LEFT JOIN training_unit tu ON tu.topic_code = s.topic_code\n"
                            + "WHERE tp.training_program_code = :code and tp.status = 'Active'",
            nativeQuery = true)
    Integer totalTrainingProgramDates(@Param("code") int training_program_code);

    @Query(
            value =
                    "SELECT tp.name AS 'trainingProgramName',\n"
                            + "       tp.modified_by,\n"
                            + "       tp.modified_date,\n"
                            + "       s.course_objective,\n"
                            + "       s.topic_name,\n"
                            + "       s.publish_status,\n"
                            + "       tu.unit_name,\n"
                            + "       tu.day_number,\n"
                            + "       tc.content_name,\n"
                            + "       lo.name AS 'objective',\n"
                            + "       tc.duration\n"
                            + "FROM training_programs tp\n"
                            + "LEFT JOIN training_program_syllabuses tps ON tps.training_programs_code = tp.training_program_code\n"
                            + "LEFT JOIN syllabus s ON s.topic_code = tps.topic_code\n"
                            + "LEFT JOIN training_unit tu ON tu.topic_code = s.topic_code\n"
                            + "LEFT JOIN training_contents tc ON tc.unit_code = tu.unit_code\n"
                            + "LEFT JOIN learning_objective lo ON lo.content_code = tc.content_code\n"
                            + "WHERE tp.training_program_code = :code AND tp.status = 'Active'\n"
                            + "GROUP BY tp.training_program_code, tp.name, tp.modified_by, tp.modified_date, s.course_objective, s.topic_name, s.publish_status, tu.unit_name, tu.day_number, tc.content_name, lo.name, tc.duration;",
            nativeQuery = true)
    List<TrainingProgramDetails> getTrainingProgramDetails(@Param("code") int training_program_code);

    int countByNameLike(String name);

    @Query(value = "SELECT MAX(training_program_code) FROM training_programs", nativeQuery = true)
    Integer findMaxTrainingProgramCode();

    Optional<TrainingProgram> findTrainingProgramByNameAndTrainingProgramCode(String name, Integer trainingProgramCode);
}
